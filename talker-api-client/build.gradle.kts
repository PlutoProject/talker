plugins {
    id("java")
}

group = "club.plutomc.talker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":talker-api"))

    testApi(platform("org.junit:junit-bom:5.9.1"))
    testApi("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}