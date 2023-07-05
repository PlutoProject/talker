plugins {
    id("java")
}

group = "club.plutomc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")

    compileOnly(project(":talker-api"))
    compileOnly(project(":talker-api-server"))
    compileOnly(project(":talker-common"))
    compileOnly(project(":talker-server"))
    compileOnly(project(":talker-minecraft"))
    compileOnly(project(":talker-velocity"))

    kapt("com.velocitypowered:velocity-api:3.1.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}