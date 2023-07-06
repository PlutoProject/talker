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
    compileOnly("io.netty:netty-all:4.1.94.Final")

    implementation(project(":talker-api"))
    implementation(project(":talker-api-server"))
    implementation(project(":talker-common"))
    implementation(project(":talker-server")) {
        exclude("io.netty:netty-all")
    }
    implementation(project(":talker-minecraft"))

    kapt("com.velocitypowered:velocity-api:3.1.1")

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