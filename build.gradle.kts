buildscript {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
      content { includeModule("me.amanjeet.daggertrack", "dagger-track") }
    }
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    classpath("com.android.tools.build:gradle:7.1.0-alpha01")
    classpath("com.diffplug.spotless:spotless-plugin-gradle:5.12.5")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.37")
    classpath("me.amanjeet.daggertrack:dagger-track:1.0.6-SNAPSHOT")
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
        .config(mapOf("parser" to "xml", "tabWidth" to 2))
    }
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "7.1"
  distributionType = Wrapper.DistributionType.ALL
  distributionSha256Sum = "a9e356a21595348b6f04b024ed0b08ac8aea6b2ac37e6c0ef58e51549cd7b9cb"
}
