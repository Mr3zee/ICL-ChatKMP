plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

group = "org.jetbrains.chat.kmp"
version = "1.0.0"
application {
    mainClass.set("org.jetbrains.chat.kmp.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)

    implementation(libs.logback)
    implementation(libs.openai.client)
    implementation(libs.koin.ktor)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgres)

    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.serialization.json)
    implementation(libs.ktor.client.okhttp.legacy)
}

tasks.named<ProcessResources>("processResources") {
    from(projectDir) {
        include("local.properties")
    }
}
