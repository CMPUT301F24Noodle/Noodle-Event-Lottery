import org.gradle.internal.impldep.org.bouncycastle.oer.OERDefinition.optional

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version
            "4.4.2" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}





buildscript {
    //tried adding this to fix the javadoc error
//    repositories{
//        mavenCentral()
//        google()
//        jcenter()
//    }

    dependencies {
        //tried adding this to fix the javadoc error
//        classpath(libs.gradle)
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.google.services)

    }
}