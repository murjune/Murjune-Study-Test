plugins {
    alias(libs.plugins.murjune.jvm.library)
    alias(libs.plugins.murjune.unit.test)
}

group = "com.murjune.practice.kotlin.practice"

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
}
