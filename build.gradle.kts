import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.compose") version "1.6.0-rc03"
}

group = "com.dataxow"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("uk.co.caprica:vlcj:4.8.2")
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
//            modules("jdk.unsupported")
            modules(
                "java.compiler",
                "java.instrument",
                "java.prefs",
                "java.sql",
                "jdk.unsupported",
                "jdk.accessibility"
            )
//            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            copyright = "Copyright 2023 Paulo Coutinho"
            vendor = "Paulo Coutinho"
//            licenseFile.set(project.file("LICENSE"))
//            windows{
////                console = true
//                dirChooser = true
//                menuGroup = "幕境"
//                iconFile.set(project.file("src/jvmMain/resources/logo/logo.ico"))
//            }
//            macOS{
//                iconFile.set(project.file("src/jvmMain/resources/logo/logo.icns"))
//            }
//            linux {
//                iconFile.set(project.file("src/jvmMain/resources/logo/logo.png"))
//            }
        }
    }
}
