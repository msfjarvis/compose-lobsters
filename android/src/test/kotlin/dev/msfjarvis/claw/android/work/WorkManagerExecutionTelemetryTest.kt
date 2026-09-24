/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import com.google.common.truth.Truth.assertThat
import java.util.UUID
import org.junit.jupiter.api.Test

class WorkManagerExecutionTelemetryTest {
  @Test
  fun `workerName returns the worker class name`() {
    val workInfo = workInfo(workerClassName = "dev.msfjarvis.claw.SavedPostUpdaterWorker")

    assertThat(workInfo.workerName()).isEqualTo("dev.msfjarvis.claw.SavedPostUpdaterWorker")
  }

  @Test
  fun `workerName falls back when the class name is absent`() {
    val workInfo = workInfo(workerClassName = null)

    assertThat(workInfo.workerName()).isEqualTo("unknown")
  }

  @Test
  fun `resultOutcome maps every ListenableWorker result`() {
    assertThat(resultOutcome(ListenableWorker.Result.success())).isEqualTo("success")
    assertThat(resultOutcome(ListenableWorker.Result.retry())).isEqualTo("retry")
    assertThat(resultOutcome(ListenableWorker.Result.failure())).isEqualTo("failure")
  }

  @Test
  fun `stopReasonName maps known stop reasons`() {
    assertThat(stopReasonName(WorkInfo.STOP_REASON_TIMEOUT)).isEqualTo("timeout")
    assertThat(stopReasonName(WorkInfo.STOP_REASON_CANCELLED_BY_APP)).isEqualTo("cancelled_by_app")
    assertThat(stopReasonName(WorkInfo.STOP_REASON_QUOTA)).isEqualTo("quota")
  }

  @Test
  fun `stopReasonName preserves unknown stop reasons`() {
    assertThat(stopReasonName(12345)).isEqualTo("unknown_code_12345")
  }

  private fun workInfo(workerClassName: String?): WorkInfo =
    WorkInfo(
      id = UUID.randomUUID(),
      state = WorkInfo.State.RUNNING,
      tags = emptySet(),
      workerClassName = workerClassName,
    )
}
