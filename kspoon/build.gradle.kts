import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "dev.burnoo.kspoon"
version = "0.1.0-SNAPSHOT"

kotlin {
    explicitApi()

    jvm()

    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    linuxX64()
    linuxArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    mingwX64()
}

apiValidation {
    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}

tasks.withType<Test> { useJUnitPlatform() }

dependencies {
    commonMainApi(libs.ksoup.lite)
    commonMainImplementation(libs.kotlinx.serialization.core)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(libs.kotlinx.datetime)
    commonTestImplementation(libs.kotest.assertions)
}

// Publishing configuration below
val currentProperties = rootProject.file("local.properties")
    .run { if (exists()) Properties().apply { load(reader()) } else properties }

val isRelease: Boolean
    get() = currentProperties["isRelease"]?.toString()?.toBoolean() == true

tasks.withType<DokkaTask>().configureEach {
    if (isRelease) {
        moduleVersion = moduleVersion.get().replace("-SNAPSHOT", "")
    }
}

extensions.findByType<PublishingExtension>()?.apply {
    repositories {
        maven {
            name = "sonatype"
            val repositoryId = currentProperties["sonatypeStagingRepositoryId"]?.toString()
            url = uri(
                if (repositoryId != null) {
                    "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                },
            )
            credentials {
                username = currentProperties["sonatypeUsername"]?.toString()
                password = currentProperties["sonatypePassword"]?.toString()
            }
        }
        maven {
            name = "sonatypeSnapshot"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = currentProperties["sonatypeUsername"]?.toString()
                password = currentProperties["sonatypePassword"]?.toString()
            }
        }
    }
    publications.withType<MavenPublication>().configureEach {
        val publication = this
        val dokkaJar = project.tasks.register("${publication.name}DokkaJar", Jar::class) {
            archiveClassifier.set("javadoc")
            from(tasks.named("dokkaHtml"))
            archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
        }
        artifact(dokkaJar)
        pom {
            description = "Annotation based HTML to Kotlin class parser with KMP support, jspoon successor"
            url = "https://github.com/burnoo/kspoon"
            if (isRelease) {
                version = version.replace("-SNAPSHOT", "")
            }

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }

            developers {
                developer {
                    id = "burnoo"
                    name = "Bruno Wieczorek"
                    url = "https://burnoo.dev"
                    email = "bruno.wieczorek@gmail.com"
                }
            }

            scm {
                connection = "scm:git:git@github.com:burnoo/kspoon.git"
                url = "https://github.com/burnoo/kspoon"
                tag = "HEAD"
            }
        }
    }
}

signing {
    val key = currentProperties["signingKey"]?.toString()?.replace("\\n", "\n")
    val password = currentProperties["signingPassword"]?.toString()
    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}
