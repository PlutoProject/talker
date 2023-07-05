plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.1.94.Final")

    implementation(project(":talker-api"))
    implementation(project(":talker-api-server"))
    implementation(project(":talker-common"))
    implementation(project(":talker-server")) {
        exclude("io.netty:netty-all")
    }
    implementation(project(":talker-minecraft"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}