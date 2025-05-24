import org.jetbrains.kotlin.konan.properties.Properties
import kotlin.String

plugins {
    id("template.android.application")
    id("template.android.application.compose")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    id("template.android.room")
    id("template.android.firebase")
}

android {
    namespace = "com.ruicomp.gpsalarm"

    defaultConfig {
        applicationId = "com.ruicomp.gpsalarm"

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val mapsApiKey = properties.getProperty("MAP_K")?.toString() ?: ""
        manifestPlaceholders["mapk"] = mapsApiKey
        buildConfigField("String", "mapk", "\"$mapsApiKey\"")

    }

    signingConfigs {
        create("release") {
            storeFile = file("/<folder>/filename.jks") //ex: /signkey/release.jks
            storePassword = "strongpassword"
            keyAlias = "release"
            keyPassword = "strongpassword"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "crashlytic-rules.pro"
            )
        }
        firebaseCrashlytics {
            mappingFileUploadEnabled = true
        }
    }

    flavorDimensions += listOf("dimen")
    productFlavors {
        create("vOfficial_") {

            buildConfigField("Boolean", "TEST_AD", "false")
            buildConfigField("Boolean", "PURCHASED", "false")
            dimension = "dimen"

            // Function to add a constant to manifestPlaceholders and buildConfigField
            // 'this' refers to the ProductFlavor object within the create block
            val addConstantTo: (constantName: String, constantValue: String) -> Unit = { constantName, constantValue ->
                manifestPlaceholders[constantName] = constantValue
                buildConfigField("String", constantName, "\"$constantValue\"")
            }

            addConstantTo("MAP_ID", "AIzaSyA806DdFEC4yamltUWpMLDzMw0ddlBxYxE")
            addConstantTo("APP_ID", "ca-app-pub-3940256099942544~3347511713")
            // -----------------------------------------------------------------------------------
            addConstantTo("app_reopen", "ca-app-pub-3940256099942544/9257395921")
            addConstantTo("app_open_splash", "ca-app-pub-3940256099942544/9257395921")
            addConstantTo("native_splash_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("native_splash", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("lfo1_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("lfo1_native", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("lfo2_native_high", "ca-app-pub-3940256099942544/224766110")
            addConstantTo("lfo2_native_high1", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("lfo2_native_high2", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("lfo2_native", "ca-app-pub-3940256099942560/2247696110")
            addConstantTo("ob1_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob1_native", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob2_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob2_native", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob3_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob3_native_high1", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob3_native_high2", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob3_native", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob3_inter_high", "ca-app-pub-3940256099942544/1033173712")
            addConstantTo("ob3_inter", "ca-app-pub-3940256099942544/1033173712")
            addConstantTo("ob4_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob4_native", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob5_native_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("ob5_native", "ca-app-pub-3940256099942544/2247696110")
            // -----------------------------------------------------------------------------------
            addConstantTo("native_home", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("native_home_high", "ca-app-pub-3940256099942544/2247696110")
            addConstantTo("banner_home", "ca-app-pub-3940256099942544/6300978111")
            addConstantTo("banner_home_high", "ca-app-pub-3940256099942544/6300978111")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.services.maps)
    implementation(libs.maps.compose)

    implementation(libs.accompanist.permissions)

    implementation(libs.places.maps)
    implementation(libs.accompanist.flowlayout)

    implementation(libs.datastore.preferences)

    implementation(libs.androidx.material.icons.extend)

    implementation(project(":onboardmd"))
    implementation(project(":CommonLib"))

    //outer
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.google.playservices.ads)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.koin.android)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}