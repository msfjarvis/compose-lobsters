/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
plugins {
  kotlin("multiplatform")
}

kotlin {
  jvm()
  js {
    nodejs()
  }

  sourceSets {
    commonMain {
      dependencies {
        api(libs.zipline)
        api(libs.kotlinx.serialization.json)
      }
    }
    jvmTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}
