import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.diffplug.spotless") version "6.9.0"
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}
android {
    compileSdk = 32

    defaultConfig {
        applicationId = "io.simsim.demo.fetal"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(
                    mapOf(
                        "room.incremental" to "true",
                        "room.expandProjection" to "true",
                        "room.schemaLocation" to "$projectDir/schemas".toString()
                    )
                )
            }
        }
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
        freeCompilerArgs = freeCompilerArgs.plus("-opt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.Compose.ui)
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.biometric.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.savedState)
    implementation(AndroidX.core.splashscreen)
    implementation(AndroidX.dynamicAnimation.ktx)
    implementation(Google.android.material)

    implementation(Splitties.pack.androidMdcWithViewsDsl)

    implementation(files("./libs/EasyFloat-2.0.4.aar"))

    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    implementation(AndroidX.room.ktx)
    kapt(AndroidX.room.compiler)


    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)
    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
            .editorConfigOverride(mapOf("disabled_rules" to "no-wildcard-imports,filename"))
    }
}