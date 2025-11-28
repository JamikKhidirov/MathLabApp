import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.plugin.serialization")

    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}


// --- Логика Версионирования (version.txt) ---
val versionPropsFile = project.rootProject.file("version.txt")

var versionCode = 1
var versionName = "1.0.1"

if (versionPropsFile.exists()) {
    versionCode = versionPropsFile.readText().trim().toInt()
    versionName = "1.0.$versionCode"
}

// --- Логика Подписания (signing.properties) ---
val signingPropsFile = project.file("signing.properties")
val signingProps = Properties()
var useCiSigning = false // Флаг, указывающий, использовать ли CI-подпись



if (signingPropsFile.exists()) {
    try {
        FileInputStream(signingPropsFile).use { signingProps.load(it) }
        useCiSigning = true // Файл найден, будем использовать CI-подпись
        println("Using CI/CD signing configuration from app/signing.properties")
    } catch (e: Exception) {
        println("Error loading signing.properties: ${e.message}")
    }
}
else{
    println("File app/signing.properties not found. Using debug signing for local build.")
}


android {
    namespace = "com.example.mathlab"
    compileSdk = 36


    signingConfigs{

    }

    defaultConfig {
        applicationId = "com.example.mathlab"
        minSdk = 26
        targetSdk = 36
        // Применяем versionCode и versionName
        this.versionCode = versionCode
        this.versionName = versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs{
        // Конфигурация, которая будет использоваться, ЕСЛИ файл signing.properties существует (т.е. в CI/CD)
        if (useCiSigning) {
            create("release") {
                storeFile = file(signingProps.getProperty("storeFile")!!) // !! - Уверены, что ключ есть, т.к. useCiSigning = true
                storePassword = signingProps.getProperty("storePassword")!!
                keyAlias = signingProps.getProperty("keyAlias")!!
                keyPassword = signingProps.getProperty("keyPassword")!!
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            // ...

            // --- Ключевой Момент ---
            if (useCiSigning) {
                // Если файл signing.properties найден (CI/CD): используем настроенный release-ключ
                signingConfig = signingConfigs.getByName("release")
            } else {
                // Если файл НЕ найден (Локальная сборка): используем стандартный debug-ключ
                // Это позволяет собрать релизную версию локально для тестирования,
                // не требуя ручной настройки ключей.
                signingConfig = signingConfigs.getByName("debug")
            }
        }

    }

    packaging {
        resources {
            pickFirsts.add("graphml.xsd")
            pickFirsts.add("xlink.xsd")
            pickFirsts.add("viz.xsd")
            pickFirsts.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/versions/**")
            excludes.add("graphml.xsd")

            excludes += "gexf.xsd"


        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }


}

dependencies {

    implementation(libs.androidx.camera.core)
    val navVersion = "2.8.0"

    implementation(project(":data"))
    implementation(project(":domain"))

    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")



    // Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.core:core-splashscreen:1.2.0")

    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}