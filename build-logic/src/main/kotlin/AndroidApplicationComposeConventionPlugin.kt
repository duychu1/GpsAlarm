import com.android.build.api.dsl.ApplicationExtension
import com.ruicomp.template.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
            }

        }
    }

}
