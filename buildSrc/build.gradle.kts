plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}

gradlePlugin {
  plugins {
    register("lobsters") {
      id = "lobsters-plugin"
      implementationClass = "LobstersPlugin"
    }
    register("coreLibraryDesugaring") {
      id = "core-library-desugaring"
      implementationClass = "CoreLibraryDesugaringPlugin"
    }
    register("versioning") {
      id = "versioning-plugin"
      implementationClass = "VersioningPlugin"
    }
  }
}

dependencies {
  implementation(Plugins.androidGradlePlugin)
  implementation(Plugins.androidGradlePlugin_lintModel)
  implementation(Plugins.daggerGradlePlugin)
  implementation(Plugins.jsemver)
  implementation(Plugins.kotlinGradlePlugin)
  implementation(Plugins.sqldelightGradlePlugin)
}
