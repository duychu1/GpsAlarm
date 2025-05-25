pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //CommonLib
        jcenter()
        maven("https://jitpack.io")
        maven("https://dl.openmediation.com/omcenter/")
        maven("https://artifact.bytedance.com/repository/pangle")
        maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven("https://dl-maven-android.mintegral.com/repository/se_sdk_for_android/")
        maven("https://developer.huawei.com/repo/")
        maven("https://developer.hihonor.com/repo")
        maven("https://maven.reyun.com/repository/solar-engine")
    }
}

rootProject.name = "GpsAlarm"
include(":app")
include(":CommonLib")
