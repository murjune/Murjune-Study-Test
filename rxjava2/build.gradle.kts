plugins {
    alias(libs.plugins.murjune.jvm.library)
    alias(libs.plugins.murjune.unit.test)
}

group = "com.murjune.practice.rxjava2"

dependencies {
    implementation(libs.rxjava2)
    testImplementation(libs.rxjava2)
}