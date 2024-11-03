import org.apache.commons.io.filefilter.FalseFileFilter

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.tasks)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.ext.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Android test dependencies
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("androidx.test.ext:junit:1.1.3")
    testImplementation ("androidx.test:runner:1.4.0")
    testImplementation ("androidx.test:rules:1.4.0")



    // dependencies for Firebase Authentication and Cloud Firestore
    implementation("com.google.firebase:firebase-bom:32.7.1")
    implementation("com.google.firebase:firebase-firestore")


    testImplementation("org.json:json:20180813")
    androidTestImplementation("androidx.test:rules:1.2.0")

    //for Mockito
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.19.0")
    
    implementation ("de.hdodenhof:circleimageview:3.1.0") //For Circular Profile Picture

    implementation("com.journeyapps:zxing-android-embedded:4.3.0") {isTransitive=false}
    implementation("com.google.zxing:core:3.4.1") // for QR code generation

}