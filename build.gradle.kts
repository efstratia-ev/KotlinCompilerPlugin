import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
  extra["kotlin_plugin_id"] = "com.bnorm.template.kotlin-ir-plugin"
}

plugins {
  kotlin("jvm") version "1.4.20" apply false
  id("org.jetbrains.dokka") version "0.10.0" apply false
  id("com.gradle.plugin-publish") version "0.11.0" apply false
  id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
  id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}
repositories {
  jcenter()
}

allprojects {
  group = "org.clyze"
  version = "0.1.0"
}

subprojects {
  repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url = uri("https://clyze.jfrog.io/artifactory/default-maven-local") }
  }
}





