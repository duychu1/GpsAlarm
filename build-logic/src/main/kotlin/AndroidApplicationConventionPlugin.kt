import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.ruicomp.template.AppConfig
import com.ruicomp.template.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<BaseAppModuleExtension> {
                configureKotlinAndroid(this)
//                configureBuildType(this)
                defaultConfig.targetSdk = AppConfig.targetSdk
                buildFeatures {
                    buildConfig = true
                }
            }
        }
    }
}