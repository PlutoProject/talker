plugins {
    id("java")
}

group = "club.plutomc.talker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    api("org.jetbrains.kotlin:kotlin-reflect:1.8.22")

    testApi(platform("org.junit:junit-bom:5.9.1"))
    testApi("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}