plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:2.0.7")

    implementation(project(":talker-api"))
    implementation(project(":talker-api-server"))
    implementation(project(":talker-common"))
    implementation("io.netty:netty-all:4.1.94.Final")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}