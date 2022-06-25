# paparazzi-tests

[Paparazzi](https://github.com/cashapp/paparazzi) is a Gradle plugin and library that allows writing
Android UI tests that run on the JVM. However, it does not support Kotlin Multiplatform so we're
using this Android library module to hide the Multiplatform aspect of the project from Paparazzi. In
the future this will be natively supported and we can disperse the tests contained in this module
into existing ones.
