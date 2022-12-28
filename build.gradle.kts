buildscript {

    val composeVersion by extra("1.1.1")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        autoCorrect = true
        config = rootProject.files("config/detekt/detekt.yml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
