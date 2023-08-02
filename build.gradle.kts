import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("kapt") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "club.plutomc.talker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

allprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("com.github.johnrengelman.shadow")
    }
}

val apis = listOf("talker-api", "talker-api-client", "talker-api-server")

val repoUser: String = System.getenv("TALKER_MAVEN_USER")
val repoPwd: String = System.getenv("TALKER_MAVEN_PASSWORD")

subprojects {
    if (apis.contains(name)) {
        apply {
            apply(plugin = "maven-publish")

            publishing {
                configure<PublishingExtension> {
                    publications.create<MavenPublication>(name) {
                        from(components["kotlin"])

                        groupId = project(":$name").group.toString()
                        artifactId = name
                        version = project(":$name").version.toString()
                    }
                }

                repositories {
                    maven {
                        url = uri("https://nexus.nostaldaisuki.cc/repository/maven-releases/")
                        credentials {
                            username = repoUser
                            password = repoPwd
                        }
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}