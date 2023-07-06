plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.1.94.Final")

    implementation(project(":talker-api"))
    implementation(project(":talker-api-client"))
    implementation(project(":talker-common"))
    implementation(project(":talker-client")) {
        exclude("io.netty:netty-all")
    }
    implementation(project(":talker-minecraft"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.shadowJar {
    exclude("io.netty.*")
    exclude("org.jetbrains.*")
}

tasks.test {
    useJUnitPlatform()
}