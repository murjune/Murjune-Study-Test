plugins {
    alias(libs.plugins.murjune.jvm.library)
    alias(libs.plugins.murjune.unit.test)
}

group = "com.murjune.practice.coroutine"

dependencies {
    implementation("io.reactivex.rxjava2:rxjava:2.2.6")
}