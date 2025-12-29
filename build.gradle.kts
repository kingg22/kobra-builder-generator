import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("org.jetbrains.intellij.platform")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
}

group = "io.github.kingg22"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
        languageVersion.set(apiVersion)
        jvmTarget.set(JvmTarget.JVM_17)
        jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
        extraWarnings.set(true)
        allWarningsAsErrors.set(true)
        explicitApi()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2023.3") {
            type.set(IntelliJPlatformType.IntellijIdeaCommunity)
        }
        bundledPlugin("com.intellij.java")
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.JUnit5)
    }
    // https://youtrack.jetbrains.com/issue/IJPL-159134
    // https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-faq.html?from=jetbrains.org#junit5-test-framework-refers-to-junit4
    testImplementation("junit:junit:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.14.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

intellijPlatform {
    pluginConfiguration {
        version.set(project.version.toString())
        ideaVersion {
            sinceBuild.set("233.11799.241")
        }
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders +=
                    CommandLineArgumentProvider {
                        listOf(
                            "-Drobot-server.port=8082",
                            "-Dide.mac.message.dialogs.as.sheets=false",
                            "-Djb.privacy.policy.text=<!--999.999-->",
                            "-Djb.consents.confirmation.enabled=false",
                        )
                    }
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

ktlint {
    version.set(libs.versions.ktlint.pinterest)
}
