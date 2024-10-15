package com.ruicomp.template

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureBuildType(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        signingConfigs {

        }
        buildTypes {

        }
    }
}