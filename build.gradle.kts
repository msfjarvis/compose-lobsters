buildscript {
  repositories {
    gradlePluginPortal()
    jcenter()
    google()
    mavenCentral()
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
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
  }
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/**")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
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
