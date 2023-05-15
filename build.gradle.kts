import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.0"
    id("java")
}

group = "com.mannix"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jcenter.bintray.com/")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Otter Terminal"
            packageVersion = "1.0.0"

            windows {
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "E54E747C-0A19-47E5-842D-8838A458CC28"
            }

            macOS {
                // Use -Pcompose.desktop.mac.sign=true to sign and notarize.
                bundleID = "com.github.mannixsuo.otter"
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

val testVersion = "5.5.0"
dependencies {
    implementation("com.formdev:flatlaf:2.5")
    implementation("org.jetbrains.pty4j:pty4j:0.12.9")
    implementation("ch.qos.logback:logback-classic:1.4.3")
    implementation("ch.qos.logback:logback-core:1.4.3")
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("com.jcraft:jsch:0.1.55")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    testImplementation("io.kotest:kotest-runner-junit5:${testVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${testVersion}")
    testImplementation("io.kotest:kotest-property:${testVersion}")
}
