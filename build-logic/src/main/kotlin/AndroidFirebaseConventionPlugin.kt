import com.ruicomp.template.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * This plugin applies common configurations for Android projects that use Firebase.
 */
class AndroidFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                add("implementation", libs.findLibrary("firebase-bom").get())
                add("implementation", libs.findLibrary("firebase-crashlytics").get())
                add("implementation", libs.findLibrary("firebase-analytics").get())
                add("implementation", libs.findLibrary("firebase-config").get())
                add("implementation", libs.findLibrary("firebase-messaging").get())
            }

        }
    }
}