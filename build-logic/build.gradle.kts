plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("templateApplicationConventionPlugin") {
            id = "template.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("templateLibraryConventionPlugin") {
            id = "template.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("templateApplicationComposeConventionPlugin") {
            id = "template.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("templateLibraryComposeConventionPlugin") {
            id = "template.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("templateHiltConventionPlugin") {
            id = "template.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("templateRoomConventionPlugin") {
            id = "template.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }

    }
}