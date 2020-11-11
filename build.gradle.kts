buildscript {
  apply(from = "buildSrc/buildDependencies.gradle")
  val build: Map<Any, Any> by extra
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath(build.getValue("androidGradlePlugin"))
    classpath(build.getValue("daggerGradlePlugin"))
    classpath(build.getValue("kotlinGradlePlugin"))
  }
}

plugins {
  id("com.github.ben-manes.versions") version "0.36.0"
  `lobsters-plugin`
}
