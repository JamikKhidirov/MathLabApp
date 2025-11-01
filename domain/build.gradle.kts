plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}


// 1. Установите целевую версию для компилятора Java
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}


