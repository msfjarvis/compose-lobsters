/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.annotation.SuppressLint
import androidx.work.ExecutionEventListener
import androidx.work.ExperimentalEventsApi
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import io.sentry.Sentry
import io.sentry.SentryAttribute
import io.sentry.SentryAttributes
import io.sentry.metrics.SentryMetricsParameters
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Reports [ExecutionEventListener] callbacks to Sentry so the execution health of every
 * [androidx.work.Worker] can be aggregated across the installed base.
 *
 * Every callback emits a `worker.execution` counter tagged with the worker class and an outcome.
 * Terminal callbacks additionally emit a `worker.duration` distribution, and exceptions are
 * captured as error events so their stack traces are grouped by Sentry.
 */
@OptIn(ExperimentalEventsApi::class)
internal class WorkManagerExecutionTelemetry : ExecutionEventListener {

  private val startedAt = ConcurrentHashMap<UUID, Long>()

  override suspend fun onStarted(workInfo: WorkInfo) {
    startedAt[workInfo.id] = System.currentTimeMillis()
    emitExecution(outcome = "started", workInfo = workInfo)
  }

  override suspend fun onStopped(stopReason: Int, workInfo: WorkInfo) {
    emitDuration(outcome = "stopped", workInfo = workInfo)
    emitExecution(
      outcome = "stopped",
      workInfo = workInfo,
      SentryAttribute.stringAttribute("worker.stop_reason", stopReasonName(stopReason)),
      SentryAttribute.integerAttribute("worker.stop_reason_code", stopReason),
    )
  }

  override suspend fun onFinished(result: ListenableWorker.Result, workInfo: WorkInfo) {
    val outcome = resultOutcome(result)
    emitDuration(outcome = outcome, workInfo = workInfo)
    emitExecution(outcome = outcome, workInfo = workInfo)
  }

  override suspend fun onException(throwable: Throwable, workInfo: WorkInfo) {
    emitDuration(outcome = "exception", workInfo = workInfo)
    emitExecution(
      outcome = "exception",
      workInfo = workInfo,
      SentryAttribute.stringAttribute("worker.exception_type", throwable.javaClass.name),
    )
    Sentry.withScope { scope ->
      scope.setTag("worker.class", workInfo.workerName())
      Sentry.captureException(throwable)
    }
  }

  private fun emitExecution(
    outcome: String,
    workInfo: WorkInfo,
    vararg extraAttributes: SentryAttribute,
  ) {
    val attributes =
      SentryAttributes.of(
        SentryAttribute.stringAttribute("worker.class", workInfo.workerName()),
        SentryAttribute.stringAttribute("worker.outcome", outcome),
        SentryAttribute.integerAttribute("worker.run_attempt", workInfo.runAttemptCount),
        SentryAttribute.integerAttribute("worker.generation", workInfo.generation),
        *extraAttributes,
      )
    Sentry.metrics()
      .count(WORKER_EXECUTION_METRIC, 1.0, null, SentryMetricsParameters.create(attributes))
  }

  private fun emitDuration(outcome: String, workInfo: WorkInfo) {
    val start = startedAt.remove(workInfo.id) ?: return
    val durationMillis = (System.currentTimeMillis() - start).coerceAtLeast(0L)
    Sentry.metrics()
      .distribution(
        WORKER_DURATION_METRIC,
        durationMillis.toDouble(),
        "millisecond",
        SentryMetricsParameters.create(
          SentryAttributes.of(
            SentryAttribute.stringAttribute("worker.class", workInfo.workerName()),
            SentryAttribute.stringAttribute("worker.outcome", outcome),
          )
        ),
      )
  }

  companion object {
    internal const val WORKER_EXECUTION_METRIC = "worker.execution"
    internal const val WORKER_DURATION_METRIC = "worker.duration"
  }
}

internal fun WorkInfo.workerName(): String = workerClassName ?: "unknown"

// There doesn't seem to be any other way to get these names out of a Worker's exit status.
// Filed a request under https://issuetracker.google.com/issues/565504491
@SuppressLint("RestrictedApi")
internal fun resultOutcome(result: ListenableWorker.Result): String {
  return when (result) {
    is ListenableWorker.Result.Success -> "success"
    is ListenableWorker.Result.Retry -> "retry"
    is ListenableWorker.Result.Failure -> "failure"
    else -> "unknown"
  }
}

internal fun stopReasonName(stopReason: Int): String =
  when (stopReason) {
    WorkInfo.STOP_REASON_FOREGROUND_SERVICE_TIMEOUT -> "foreground_service_timeout"
    WorkInfo.STOP_REASON_NOT_STOPPED -> "not_stopped"
    WorkInfo.STOP_REASON_UNKNOWN -> "unknown"
    WorkInfo.STOP_REASON_CANCELLED_BY_APP -> "cancelled_by_app"
    WorkInfo.STOP_REASON_PREEMPT -> "preempt"
    WorkInfo.STOP_REASON_TIMEOUT -> "timeout"
    WorkInfo.STOP_REASON_DEVICE_STATE -> "device_state"
    WorkInfo.STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW -> "constraint_battery_not_low"
    WorkInfo.STOP_REASON_CONSTRAINT_CHARGING -> "constraint_charging"
    WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY -> "constraint_connectivity"
    WorkInfo.STOP_REASON_CONSTRAINT_DEVICE_IDLE -> "constraint_device_idle"
    WorkInfo.STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW -> "constraint_storage_not_low"
    WorkInfo.STOP_REASON_QUOTA -> "quota"
    WorkInfo.STOP_REASON_BACKGROUND_RESTRICTION -> "background_restriction"
    WorkInfo.STOP_REASON_APP_STANDBY -> "app_standby"
    WorkInfo.STOP_REASON_USER -> "user"
    WorkInfo.STOP_REASON_SYSTEM_PROCESSING -> "system_processing"
    WorkInfo.STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED -> "estimated_app_launch_time_changed"
    else -> "unknown_code_$stopReason"
  }
