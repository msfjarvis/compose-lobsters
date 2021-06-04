buildscript {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    classpath("com.android.tools.build:gradle:4.1.1")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:5.12.4")
  }
}

group = "dev.msfjarvis.claw"

version = "1.0"

allprojects {
  repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    google()
  }
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/**")
      ktfmt("0.25").googleStyle()
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktfmt("0.25").googleStyle()
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/**", ".idea/**")
      prettier(mapOf("prettier" to "2.0.5", "@prettier/plugin-xml" to "0.13.0"))
        .config(mapOf("parser" to "xml", "tabWidth" to 4))
    }
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "7.0.2"
  distributionType = Wrapper.DistributionType.ALL
  distributionSha256Sum = "13bf8d3cf8eeeb5770d19741a59bde9bd966dd78d17f1bbad787a05ef19d1c2d"
}
