/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

private const val ANDROIDX_HILT_VERSION = "1.0.0-alpha02"
private const val DAGGER_HILT_VERSION = "2.30.1-alpha"

object Dependencies {
  const val COMPOSE_VERSION = "1.0.0-alpha09"
  object Kotlin {

    object Coroutines {

      private const val version = "1.4.2"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    }
  }

  object AndroidX {

    const val activityKtx = "androidx.activity:activity-ktx:1.2.0-rc01"
    const val appCompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
    const val browser = "androidx.browser:browser:1.3.0"
    const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha05"
    const val coreLibraryDesugaring = "com.android.tools:desugar_jdk_libs:1.0.10"
    const val material = "com.google.android.material:material:1.3.0-alpha04"

    object Compose {

      const val compiler = "androidx.compose.compiler:compiler:$COMPOSE_VERSION"
      const val foundation = "androidx.compose.foundation:foundation:$COMPOSE_VERSION"
      const val foundationLayout = "androidx.compose.foundation:foundation-layout:$COMPOSE_VERSION"
      const val material = "androidx.compose.material:material:$COMPOSE_VERSION"
      const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha04"
      const val paging = "androidx.paging:paging-compose:1.0.0-alpha04"
      const val runtime = "androidx.compose.runtime:runtime:$COMPOSE_VERSION"
      const val ui = "androidx.compose.ui:ui:$COMPOSE_VERSION"
      const val uiUnit = "androidx.compose.ui:ui-unit:$COMPOSE_VERSION"
      const val uiTooling = "androidx.compose.ui:ui-tooling:$COMPOSE_VERSION"
    }

    object Hilt {
      const val dagger = "com.google.dagger:hilt-android:$DAGGER_HILT_VERSION"
      const val daggerCompiler = "com.google.dagger:hilt-compiler:$DAGGER_HILT_VERSION"
      const val daggerHiltCompiler = "androidx.hilt:hilt-compiler:$ANDROIDX_HILT_VERSION"
      const val hiltLifecycleViewmodel =
        "androidx.hilt:hilt-lifecycle-viewmodel:$ANDROIDX_HILT_VERSION"
    }

    object Lifecycle {

      private const val version = "2.3.0-rc01"
      const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
      const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
    }

    object Room {

      private const val version = "2.3.0-alpha04"
      const val compiler = "androidx.room:room-compiler:$version"
      const val ktx = "androidx.room:room-ktx:$version"
      const val runtime = "androidx.room:room-runtime:$version"
    }
  }

  object ThirdParty {

    const val accompanist = "dev.chrisbanes.accompanist:accompanist-coil:0.4.0"
    const val customtabs = "saschpe.android:customtabs:3.0.2"

    object Moshi {

      private const val version = "1.11.0"
      const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
      const val lib = "com.squareup.moshi:moshi:$version"
    }

    object Retrofit {

      private const val version = "2.9.0"
      const val lib = "com.squareup.retrofit2:retrofit:$version"
      const val moshi = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object Roomigrant {

      private const val version = "0.2.0"
      const val compiler = "com.github.MatrixDev.Roomigrant:RoomigrantCompiler:$version"
      const val runtime = "com.github.MatrixDev.Roomigrant:RoomigrantLib:$version"
    }
  }

  object Testing {

    const val daggerHilt = "com.google.dagger:hilt-android-testing:$DAGGER_HILT_VERSION"
    const val junit = "junit:junit:4.13.1"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.6.0"
    const val uiTest = "androidx.ui:ui-test:$COMPOSE_VERSION"

    object AndroidX {

      private const val version = "1.3.1-alpha02"
      const val runner = "androidx.test:runner:$version"
      const val rules = "androidx.test:rules:$version"
    }
  }
}
