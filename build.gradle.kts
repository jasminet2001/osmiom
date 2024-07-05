// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.2.0")
        classpath ("com.google.gms:google-services:4.3.8")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        //classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.6.0-1.0.0")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}