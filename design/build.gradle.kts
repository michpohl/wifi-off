plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply {
    plugin("kotlin-android")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
    }
}

configurations {
    create("testDependencies") {
        extendsFrom(configurations.testImplementation.get())
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")

    api("androidx.activity:activity-compose:1.6.1")
    api("androidx.compose.ui:ui:${rootProject.extra["composeVersion"]}")
    api("androidx.compose.material:material:${rootProject.extra["composeVersion"]}")
    api("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")
    api("androidx.compose.runtime:runtime-livedata:${rootProject.extra["composeVersion"]}")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
}
