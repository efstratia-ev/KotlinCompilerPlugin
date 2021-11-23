buildscript {
  extra["kotlin_plugin_id"] = "com.bnorm.template.kotlin-ir-plugin"
}

plugins {
  kotlin("jvm") version "1.4.20" apply false
  id("org.jetbrains.dokka") version "0.10.0" apply false
  id("com.gradle.plugin-publish") version "0.11.0" apply false
  id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
}

allprojects {
  group = "com.bnorm.template"
  version = "0.1.0-SNAPSHOT"
}

subprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
  }
}
