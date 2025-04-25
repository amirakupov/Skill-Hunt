plugins {
    kotlin("jvm")
    id("io.ktor.plugin")        // adds run/fatJar tasks :contentReference[oaicite:1]{index=1}
    application
}
application {
    mainClass.set("com.project.backend.ApplicationKt")
}
val logbackVersion = "1.5.6"
dependencies {
    implementation(platform("io.ktor:ktor-bom:3.1.2"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))
}