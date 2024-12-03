
import com.android.build.gradle.internal.dsl.decorator.SupportedPropertyType.Collection.List.type
import org.apache.commons.io.filefilter.FalseFileFilter
import org.gradle.internal.impldep.org.bouncycastle.oer.OERDefinition.optional

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
//    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

//trying to fix javadoc
//    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
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


    buildFeatures{
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }


}
//android.applicationVariants.configureEach{ variant ->
//    tasks.register("generateJavadoc", Javadoc::class.java) {
//        // ... other configurations ...
//        val buildDir = layout.buildDirectory
//        //classpath = project.files("C://Users//Erins//AppData//Local//Android//Sdk//platforms//android-34//android.jar")
//        classpath += project.files("$buildDir/intermediates/javac/debug/classes") //
//    // ...
//    }
//}



dependencies {
//
//    implementation(files("C:/Users/Erins/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.tasks)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    //implementation(libs.play.services.vision)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.ext.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Things i tried to fix the JavaDoc error:
//    implementation(libs.kotlin.stdlib)
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
//    implementation(libs.jetbrains.kotlin.stdlib.jdk7)
//    implementation(libs.kotlin.stdlib.jdk8)
//    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
//    implementation(files("C:/Users/Erins/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))



    // Android test dependencies
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test:rules:1.0.2")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.2.0")
    androidTestImplementation ("androidx.test:core:1.4.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("androidx.test.ext:junit:1.1.3")
    testImplementation ("androidx.test:runner:1.4.0")
    testImplementation ("androidx.test:rules:1.4.0")



    // dependencies for Firebase Authentication and Cloud Firestore
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore")


    testImplementation("org.json:json:20180813")
    androidTestImplementation("androidx.test:rules:1.2.0")

    //for Mockito
    androidTestImplementation("junit:junit:4.12")
    androidTestImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("org.mockito:mockito-android:5.6.0")
    androidTestImplementation("org.mockito:mockito-android:5.6.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0") //For Circular Profile Picture

    implementation("com.journeyapps:zxing-android-embedded:4.3.0") {isTransitive=false}
    implementation(libs.zxing.core) // for QR code generation
    implementation(libs.qr.scanner)

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

fun <T> DomainObjectSet<T>.configureEach(function: T.(Any?) -> Unit) {

}
