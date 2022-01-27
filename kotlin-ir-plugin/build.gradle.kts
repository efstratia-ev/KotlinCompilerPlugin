import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.gmazzo.buildconfig")
  application
}

application {
  mainClass.set("MainKt")
  mainClassName="MainKt"
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

  kapt("com.google.auto.service:auto-service:1.0-rc7")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")

  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("commons-cli:commons-cli:1.5.0")
  implementation("org.clyze:jphantom:1.3")
  implementation("com.google.guava:guava:30.1.1-jre")

  testImplementation("org.jetbrains.kotlin:kotlin-test")

  testImplementation(kotlin("test-junit"))
  implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
  implementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.6")
    implementation(kotlin("stdlib-jdk8"))

  implementation("org.clyze:metadata-model:2.3.0")
}

buildConfig {
  packageName(group.toString())
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
}

val mainClass = "MainKt" // replace it!

tasks {
  register("fatJar", Jar::class.java) {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
      attributes("Main-Class" to mainClass)
    }
    from(configurations.runtimeClasspath.get()
      .onEach { println("add from dependencies: ${it.name}") }
      .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
repositories {
    mavenLocal()
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}




