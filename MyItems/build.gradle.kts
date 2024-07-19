buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
//        classpath(libs.gradle)
        classpath(libs.firebase.crashlytics.gradle)
    }
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" ) }
        maven {
            url = rootProject.projectDir.toURI().resolve("libs")     }
        maven {
            url = uri("https://jitpack.io")
        }}
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    alias(libs.plugins.googleGmsGoogleServices) apply false

}

