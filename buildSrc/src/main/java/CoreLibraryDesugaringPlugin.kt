import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestedExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * A plugin that enables Java 8 desugaring for consuming new Java language APIs.
 *
 * Apply this plugin to the build.gradle.kts file in Android Application or Android Library
 * projects:
 * ```
 * plugins {
 *     `core-library-desugaring`
 * }
 * ```
 */
class CoreLibraryDesugaringPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.withType<AppPlugin> {
      project.extensions.getByType<TestedExtension>().configure(project)
    }

    project.plugins.withType<LibraryPlugin> {
      project.extensions.getByType<TestedExtension>().configure(project)
    }
  }

  private fun TestedExtension.configure(project: Project) {
    compileOptions.isCoreLibraryDesugaringEnabled = true
    project.dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:1.0.10")
  }
}
