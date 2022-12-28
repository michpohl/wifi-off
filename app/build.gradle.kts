plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.michaelpohl.wifitool"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

//        debug {
//
//        }
        release {

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            kotlinOptions.freeCompilerArgs += listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=coil.annotation.ExperimentalCoilApi",
                "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi"
            )
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
        }
        packagingOptions {
            resources.excludes.add("META-INF/AL2.0")
            resources.excludes.add("META-INF/LGPL2.1")
        }
    }
}

dependencies {

    implementation(project(mapOf("path" to ":design")))
    implementation(project(mapOf("path" to ":service")))
    implementation(project(mapOf("path" to ":shared")))

    // LifeCycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Accompanist helpers
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.19.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.6.0-alpha04")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")

    implementation("androidx.compose.ui:ui:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["composeVersion"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["composeVersion"]}")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["composeVersion"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")
}
