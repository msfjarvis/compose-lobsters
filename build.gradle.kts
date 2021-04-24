plugins { `lobsters-plugin` }

allprojects { apply(plugin = "com.diffplug.spotless") }

subprojects {
  configurations.configureEach {
    resolutionStrategy {
      // Retrofit depends on a very old version of Moshi that causes moshi-ksp to fail
      // Using gradle catalog here prevents compilation
      force("com.squareup.moshi:moshi:1.12.0")
    }
  }
}
