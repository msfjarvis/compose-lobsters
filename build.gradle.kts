plugins {
  `lobsters-plugin`
}

subprojects {
  configurations.configureEach {
    resolutionStrategy {
      // Retrofit depends on a very old version of Moshi that causes moshi-ksp to fail
      force(Dependencies.ThirdParty.Moshi.lib)
    }
  }
}
