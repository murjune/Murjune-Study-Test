plugins {
    `kotlin-dsl`
}

group = "com.murjune.practice.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.kotlin.gradleplugin)
}

gradlePlugin {
    plugins {
        create("kotlin-serialization") {
            id = "com.murjune.practice.kotlinx_serialization"
            implementationClass =
                "com.murjune.practice.plugins.KotlinSerializationPlugin"
        }

        register("kotlin-module") {
            id = "com.murjune.practice.kotlin.module"
            implementationClass = "com.murjune.practice.plugins.KotlinModulePlugin"
        }
        
        create("junit5") {
            id = "com.murjune.practice.junit5"
            implementationClass = "com.murjune.practice.plugins.JUnit5Plugin"
        }
        
        register("jvm-library") {
            id = "com.murjune.practice.jvm.library"
            implementationClass = "com.murjune.practice.plugins.JvmLibraryPlugin"
        }
    }
}
