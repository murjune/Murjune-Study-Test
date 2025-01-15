import com.android.build.gradle.LibraryExtension
import com.murjune.practice.plugins.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class ComposeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.plugin.compose")

        val extension = extensions.getByType<LibraryExtension>()
        configureAndroidCompose(extension)
    }
}
