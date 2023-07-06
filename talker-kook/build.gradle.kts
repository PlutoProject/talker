plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://www.jitpack.io")
    }
}

dependencies {
    compileOnly("com.github.SNWCreations:JKook:0.49.1")
    compileOnly("io.netty:netty-all:4.1.94.Final")

    implementation("com.google.code.gson:gson:2.10.1")

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

tasks.test {
    useJUnitPlatform()
}