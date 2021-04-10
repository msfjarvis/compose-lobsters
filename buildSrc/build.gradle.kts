plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
}

kotlinDslPluginOptions { experimentalWarning.set(false) }

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
  implementation(Plugins.android)
  implementation(Plugins.hilt)
  implementation(Plugins.jsemver)
  implementation(Plugins.kotlin)
  implementation(Plugins.shot)
  implementation(Plugins.sqldelight)
}
