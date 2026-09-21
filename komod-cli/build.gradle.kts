plugins {
    kotlin("jvm")
    application
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.osardio.komod"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":komod-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.apache.maven.resolver:maven-resolver-api:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-impl:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-named-locks:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-util:1.9.27")
    implementation("org.apache.maven.resolver:maven-resolver-supplier:1.9.27")
    implementation("org.apache.maven:maven-model-builder:3.9.12")
    implementation("org.apache.maven:maven-resolver-provider:3.9.12")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation(kotlin("test"))
    testImplementation(project(":komod-java"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "com.osardio.komod.cli.MainKt"
    applicationName = "komod"
}

tasks.shadowJar {
    archiveClassifier = "all"
    mergeServiceFiles()
}
