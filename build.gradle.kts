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
  implementation("net.dv8tion:JDA:5.0.0-beta.24") {
    exclude(module = "opus-java")
  }
  implementation("club.minnced:jda-ktx:0.11.0-beta.19")
  implementation("org.slf4j:slf4j-simple:2.0.13")
  implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
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
tasks.jar {
  manifest {
    attributes(
      mapOf(
        "Main-Class" to "io.github.smaugfm.icloudpd.discord.MainKt"
      )
    )
  }
}

kotlin {
  jvmToolchain(11)
}
