plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply {
    plugin("kotlin-android")
}

// TODO when moving all gradle files to kts, this cann go back to rootproject and be by extra
val composeVersion = "1.1.1"

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 21
        targetSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion as String
    }
}

configurations {
    create("testDependencies") {
        extendsFrom(configurations.testImplementation.get())
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")

    api("androidx.activity:activity-compose:1.4.0")
    api("androidx.compose.ui:ui:${composeVersion}")
    api("androidx.compose.material:material:${composeVersion}")
    api("androidx.compose.ui:ui-tooling:${composeVersion}")
    api("androidx.compose.runtime:runtime-livedata:${composeVersion}")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
}

