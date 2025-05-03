plugins {
    application
    checkstyle
    jacoco
    id("io.freefair.lombok") version "8.12.2.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.konghq:unirest-java-core:4.4.5")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")
    implementation("io.javalin:javalin:6.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("gg.jte:jte:3.2.1")
    implementation("io.javalin:javalin-rendering:6.6.0")
    implementation("io.javalin:javalin-bundle:6.6.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.postgresql:postgresql:42.7.5")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")
    implementation("org.jsoup:jsoup:1.18.3")
}

application {
    mainClass.set("hexlet.code.App")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}