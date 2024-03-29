plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp").version("1.6.10-1.0.4")
}

android {
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 29
        targetSdk = 33

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(mapOf("path" to ":design")))
    implementation(project(mapOf("path" to ":shared")))

    // Logging
    api("com.jakewharton.timber:timber:4.7.1")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")

    // Koin DI
    api("io.insert-koin:koin-android:3.1.4")
    api("io.insert-koin:koin-androidx-workmanager:3.0.1")
    api("io.insert-koin:koin-androidx-compose:3.1.4")

    // Moshi JSON handling
    api("com.squareup.moshi:moshi:1.13.0")
    api("com.squareup.moshi:moshi-adapters:1.12.0")
    api("dev.zacsweers.moshix:moshi-sealed-runtime:0.14.1")

    // Moshi annotation processing
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    ksp("dev.zacsweers.moshix:moshi-sealed-codegen:0.14.1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    testImplementation("io.fabric8:mockwebserver:0.1.8")
    implementation("androidx.core:core-ktx:1.9.0")
}
