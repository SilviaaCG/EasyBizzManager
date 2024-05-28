plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    // Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.silvia.easybizzmanager3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.silvia.easybizzmanager3"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.appcheck.ktx)
    implementation(libs.play.services.cast.framework)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.car.ui.lib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import Firebase Bom
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase Authentication Google
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    //Firebase Authentication Facebook
    implementation ("com.facebook.android:facebook-android-sdk:[8,9)")

    // Firebase Appcheck playIntegrity
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // Firebase RealTime Database
    implementation("com.google.firebase:firebase-database")

    // Picasso Imagenes
    implementation ("com.squareup.picasso:picasso:2.71828")
    //Imagen Picker GitHub
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("androidx.activity:activity-ktx:1.2.3")
    implementation ("androidx.fragment:fragment-ktx:1.3.3")

    // Fabs Buttons
    implementation("com.getbase:floatingactionbutton:1.10.1")

    // Material design
    implementation("com.google.android.material:material:1.1.0-alpha06")

    //PDF
    implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    implementation ("com.itextpdf:itext7-core:7.2.3")

    implementation("io.github.afreakyelf:Pdf-Viewer:2.1.1")

    //Splash screen
    implementation ("androidx.core:core-splashscreen:1.0.0-beta02")





}