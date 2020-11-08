/*
 * Copyright © 2014-2020 The Android Password Store Authors. All Rights Reserved.
 * SPDX-License-Identifier: GPL-3.0-only
 */

private const val ANDROIDX_HILT_VERSION = "1.0.0-alpha02"
private const val DAGGER_HILT_VERSION = "2.29.1-alpha"

object Dependencies {
  const val COMPOSE_VERSION = "1.0.0-alpha06"
  object Kotlin {

    object Coroutines {

      private const val version = "1.4.1"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    }

    object Ktor {

      private const val version = "1.4.0"
      const val clientCore = "io.ktor:ktor-client-core:$version"
      const val clientJson = "io.ktor:ktor-client-json:$version"
      const val clientSerialization = "io.ktor:ktor-client-serialization:$version"
      const val clientOkHttp = "io.ktor:ktor-client-okhttp:$version"
      const val clientTest = "io.ktor:ktor-client-mock:$version"
    }

    object Serialization {

      private const val version = "1.0.1"
      const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
    }
  }

  object AndroidX {

    const val activityKtx = "androidx.activity:activity-ktx:1.2.0-beta01"
    const val appCompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
    const val browser = "androidx.browser:browser:1.3.0-beta01"
    const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha04"
    const val coreLibraryDesugaring = "com.android.tools:desugar_jdk_libs:1.0.10"
    const val material = "com.google.android.material:material:1.3.0-alpha03"

    object Compose {

      const val compiler = "androidx.compose.compiler:compiler:$COMPOSE_VERSION"
      const val foundation = "androidx.compose.foundation:foundation:$COMPOSE_VERSION"
      const val foundationLayout = "androidx.compose.foundation:foundation-layout:$COMPOSE_VERSION"
      const val foundationText = "androidx.compose.foundation:foundation-text:$COMPOSE_VERSION"
      const val material = "androidx.compose.material:material:$COMPOSE_VERSION"
      const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha01"
      const val runtime = "androidx.compose.runtime:runtime:$COMPOSE_VERSION"
      const val ui = "androidx.compose.ui:ui:$COMPOSE_VERSION"
      const val uiText = "androidx.compose.ui:ui-text:$COMPOSE_VERSION"
      const val uiTextAndroid = "androidx.compose.ui:ui-text-android:$COMPOSE_VERSION"
      const val uiUnit = "androidx.compose.ui:ui-unit:$COMPOSE_VERSION"
      const val uiTooling = "androidx.ui:ui-tooling:$COMPOSE_VERSION"
    }

    object Hilt {
      const val dagger = "com.google.dagger:hilt-android:$DAGGER_HILT_VERSION"
      const val daggerCompiler = "com.google.dagger:hilt-compiler:$DAGGER_HILT_VERSION"
      const val daggerHiltCompiler = "androidx.hilt:hilt-compiler:$ANDROIDX_HILT_VERSION"
      const val hiltLifecycleViewmodel =
        "androidx.hilt:hilt-lifecycle-viewmodel:$ANDROIDX_HILT_VERSION"
    }

    object Lifecycle {

      private const val version = "2.3.0-beta01"
      const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
      const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
    }

    object Room {

      private const val version = "2.3.0-alpha03"
      const val compiler = "androidx.room:room-compiler:$version"
      const val ktx = "androidx.room:room-ktx:$version"
      const val runtime = "androidx.room:room-runtime:$version"
    }
  }

  object ThirdParty {

    const val accompanist = "dev.chrisbanes.accompanist:accompanist-coil:0.3.2"
    const val customtabs = "saschpe.android:customtabs:3.0.2"

    object Kodein {

      private const val version = "0.4.0-beta"
      const val apiJvm = "org.kodein.db:kodein-db-api-jvm:$version"
      const val dbJvm = "org.kodein.db:kodein-db-jvm:$version"
      const val serializerKotlinx = "org.kodein.db:kodein-db-serializer-kotlinx:$version"
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
    const val uiTest = "androidx.ui:ui-test:$COMPOSE_VERSION"

    object AndroidX {

      private const val version = "1.3.1-alpha02"
      const val runner = "androidx.test:runner:$version"
      const val rules = "androidx.test:rules:$version"
    }
  }
}
