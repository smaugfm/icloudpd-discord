plugins {
  kotlin("jvm") version "1.9.23"
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.smaugfm.icloudpd.discord"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("commons-io:commons-io:2.16.1")
  implementation("org.apache.commons:commons-collections4:4.5.0-M1")
  testImplementation(kotlin("test"))
}

tasks.shadowJar {
  archiveBaseName = project.name
  archiveClassifier = ""
  archiveVersion = ""
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(11)
}
