// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
}

buildscript {
    repositories {
        maven {
            setUrl("https://repo.eclipse.org/content/repositories/paho-snapshots/")
        }
    }
}