/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
import app.cash.zipline.loader.SignatureAlgorithmId

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.zipline)
}

kotlin {
  jvm()
  js {
    browser()
    binaries.executable()
  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.ziplineParserApi)
        implementation(libs.ksoup)
      }
    }
    jvmTest {
      dependencies {
        implementation(kotlin("test"))
      }
      resources.srcDir("../api/src/test/resources")
    }
    jsTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

zipline {
  mainFunction.set("dev.msfjarvis.claw.parser.launchZipline")
  val signingKey = providers.environmentVariable("ZIPLINE_SIGNING_KEY").orNull
  if (signingKey != null) {
    signingKeys {
      create("key0") {
        algorithmId = SignatureAlgorithmId.Ed25519
        privateKeyHex = signingKey
      }
    }
  }
}
