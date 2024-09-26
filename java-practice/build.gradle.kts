plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.junit5)
    testImplementation(libs.assertj)
}