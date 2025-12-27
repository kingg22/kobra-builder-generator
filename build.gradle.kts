import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    java
    id("org.jetbrains.intellij.platform")
    id("com.diffplug.spotless") version "8.1.0"
    id("net.ltgt.errorprone") version "4.3.0"
    id("net.ltgt.nullaway") version "2.3.0"
}

group = "pl.mjedynak"
version = "1.5.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    intellijPlatform {
        intellijIdea("2023.3") {
            type.set(IntelliJPlatformType.IntellijIdeaCommunity)
        }
        bundledPlugin("com.intellij.java")
    }
    errorprone("com.google.errorprone:error_prone_core:2.45.0")
    errorprone("com.uber.nullaway:nullaway:0.12.15")
    testImplementation(platform("org.junit:junit-bom:5.14.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

spotless {
    java {
        palantirJavaFormat()
    }
}

nullaway {
    annotatedPackages.add("pl.mjedynak.idea.plugins.builder")
    onlyNullMarked.set(false)
}

tasks.withType<JavaCompile> {
    options.errorprone {
        isEnabled.set(true)
        allSuggestionsAsWarnings.set(true)
        nullaway {
            error()
            suggestSuppressions.set(true)
            checkContracts.set(true)
            isExhaustiveOverride.set(true)
            knownInitializers.set(
                listOf(
                    "com.intellij.openapi.options.UnnamedConfigurable.createComponent",
                    "pl.mjedynak.idea.plugins.builder.gui.CreateBuilderDialog.show",
                    "pl.mjedynak.idea.plugins.builder.action.handler.AbstractBuilderActionHandler.doExecute",
                ),
            )
        }
    }
}
tasks.compileTestJava {
    options.errorprone.isEnabled.set(false)
}
