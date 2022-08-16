buildscript {
    dependencies {
        classpath(Google.Dagger.Hilt.Android.gradlePlugin)
    }
}
plugins {
    id("com.android.application") apply false version "7.2.2"
    id("com.android.library") apply false version "7.2.2"
    id("org.jetbrains.kotlin.android") apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}