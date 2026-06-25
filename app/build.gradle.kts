import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gitversion)
}

androidGitVersion {
    commitHashLength = 8
    prefix = "v"
    format = "%tag%-%commit%-release"
    codeFormat = "MNNNPPP"
}

android {
    namespace = "moe.imoli.hyperaod"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "moe.imoli.hyperaod"
        minSdk = 35
        targetSdk = 37
        versionCode = androidGitVersion.code()
        versionName = androidGitVersion.name()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val signFile = rootProject.file("local.properties")
    var signStorePassword = System.getenv("KEYSTORE_PASSWORD")
    var signKeyAlias = System.getenv("KEY_ALIAS")
    var signKeyPassword = System.getenv("KEY_PASSWORD")
    var signStoreFile = "keystore.jks"
    if (signFile.exists()) {
        val signProp = Properties()
        signProp.load(signFile.inputStream())
        signStoreFile = signProp.getProperty("sign.store.file")
        signStorePassword = signProp.getProperty("sign.store.password")
        signKeyAlias = signProp.getProperty("sign.key.alias")
        signKeyPassword = signProp.getProperty("sign.key.password")
    }


    signingConfigs {
        create("release") {
            storeFile = file(signStoreFile)
            storePassword = signStorePassword
            keyAlias = signKeyAlias
            keyPassword = signKeyPassword

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["release"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            merges += "META-INF/xposed/*"
            excludes += "**"
        }
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    // LSPosed API 102
    implementation(libs.libxposed.service)
    compileOnly(libs.libxposed.api)

    // EzXHelper API 101
    implementation(libs.ezxhelper.xposed.core)
    implementation(libs.ezxhelper.xposed.api101)
    implementation(libs.ezxhelper.android.utils)

    // KavaRef
    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)

    // SuperLyric
    implementation(libs.superlyric.api)

    // Android Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}