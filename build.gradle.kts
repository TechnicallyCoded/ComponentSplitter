plugins {
    id("java")
}

group = "com.tcoded"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.25.0")
    compileOnly("com.google.guava:guava:32.1.2-jre")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.kyori:adventure-api:4.25.0")
    testImplementation("com.google.guava:guava:32.1.2-jre")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}