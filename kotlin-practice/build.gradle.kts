plugins {
    alias(libs.plugins.murjune.jvm.library)
    alias(libs.plugins.murjune.unit.test)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.murjune.practice.kotlin.practice"

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
}
