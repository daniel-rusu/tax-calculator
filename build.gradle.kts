import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin
    `jvm-test-suite`
}

group = "org.rusu.tax-calculator"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        allWarningsAsErrors.set(true)
    }
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    dependencies {
        implementation(libs.jol)
        implementation(libs.kotlinReflect)
        testImplementation(libs.strikt)
        testRuntimeOnly(libs.jUnitPlatformLauncher)
    }
    testing {
        suites {
            val test by getting(JvmTestSuite::class) {
                useJUnitJupiter(libs.versions.jUnit)
            }
        }
    }
}
