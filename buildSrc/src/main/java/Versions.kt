import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 21
    val compileSdk = 29
    val targetSdk = 29
    val buildTools = "29.0.2"
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.3.40"
    val androidGradle = "3.5.0"
    val axionRelease = "1.10.2"

    // Dependencies
    val androidAppcompat = "1.1.0"
    val androidMaterial = "1.1.0-alpha10"

    // Test
    val junit = "4.12"
    val assertk = "0.19"
    val mockito = "3.0.0"
    val mockitoKotlin = "2.2.0"
}

val versions = Versions
