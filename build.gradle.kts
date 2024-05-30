import org.jetbrains.compose.desktop.application.dsl.TargetFormat

group = "com.dataxow"
version = "1.0.0"

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.compose") version "1.6.20-dev1646"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("uk.co.caprica:vlcj:4.8.2")
    implementation("io.ktor:ktor-server-core:2.3.11")
    implementation("io.ktor:ktor-server-netty:2.3.11")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-server-cors:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-client-apache5:2.3.11")
    implementation("com.google.zxing:core:3.5.3")
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")
    implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha06")
    implementation("media.kamel:kamel-image:0.9.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
    implementation("com.github.slugify:slugify:3.0.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.dataxow.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DataXow"
            packageVersion = version.toString()
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            copyright = "2024 Paulo Coutinho. All rights reserved."
            vendor = "Paulo Coutinho"
            licenseFile.set(project.file("LICENSE.txt"))
            modules(
                "jdk.unsupported"
            )
            windows {
                dirChooser = true
                menuGroup = "DataXow"
                iconFile.set(project.file("src/main/resources/icons/app.ico"))
            }
            macOS {
                iconFile.set(project.file("src/main/resources/icons/app.icns"))
                bundleID = "com.dataxow.app"

                signing {
                    val providers = project.providers
                    sign.set(true)
                    identity.set(providers.environmentVariable("SIGNING_IDENTITY"))
                }

                notarization {
                    val providers = project.providers
                    appleID.set(providers.environmentVariable("NOTARIZATION_APPLE_ID"))
                    teamID.set(providers.environmentVariable("NOTARIZATION_TEAM_ID"))
                    password.set(providers.environmentVariable("NOTARIZATION_PASSWORD"))
                }
            }
            linux {
                iconFile.set(project.file("src/main/resources/icons/app.png"))
            }
        }
    }
}
