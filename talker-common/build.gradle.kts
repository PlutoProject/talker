plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.netty:netty-all:4.1.94.Final")

    implementation(project(":talker-api"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}