plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "uji.es.intermaps"
    compileSdk = 35

    defaultConfig {
        applicationId = "uji.es.intermaps"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
        }
    }

}

dependencies {
    implementation(libs.coil)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.play.services.maps)
    testImplementation(project(":app"))
    androidTestImplementation(libs.androidx.junit.ktx.v115)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.runtime.livedata)
    //Mapas
    implementation(libs.android)
    implementation(libs.maps.compose)

    //JUnit4
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)

    implementation(libs.okhttp.logging.interceptor)
    //implementation ("com.mapbox.maps:android:11.8.0")
    //implementation ("com.mapbox.extension:maps-compose:11.8.0")

    // Mockito core para pruebas unitarias

    // Mockito-Kotlin para simplificar el uso en Kotlin
    testImplementation(libs.mockito.kotlin)

    // Mockito para pruebas instrumentadas
    androidTestImplementation(libs.mockito.android)
    implementation ("androidx.work:work-runtime-ktx:2.10.0")


}