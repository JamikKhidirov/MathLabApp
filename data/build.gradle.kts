plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.dagger.hilt.android")
    kotlin("kapt")

}

android {
    namespace = "com.example.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        compose = true
    }



}

dependencies {

    implementation(project(":domain"))



    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-compiler:2.57.1")

    implementation("androidx.compose.runtime:runtime:1.9.4")


    // ИЛИ для более сложной математики
    implementation("org.apache.commons:commons-math3:3.6.1")
// ИЛИ для символьных вычислений (более легковесная)
    implementation("com.github.haifengl:smile-core:3.0.1")


    // 2. Добавьте фасад SLF4J, который часто используется в больших библиотеках
    implementation("org.slf4j:slf4j-api:2.0.0") // Используйте актуальную версию


    // 4. Добавьте Android-совместимую привязку SLF4J к Logcat.
    // Теперь логи будут просто идти в стандартный Android Logcat.
    implementation("com.github.tony19:logback-android:3.0.0") // Logback-Android включает SLF4J binding

    // Предполагая, что вы используете Symja/matheclipse снова:
    implementation("org.matheclipse:matheclipse-core:2.0.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}