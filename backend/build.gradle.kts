
plugins {
    kotlin("jvm")                                    // this line already works
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"   // ← add version!
    application
    id("io.ktor.plugin")                             // keep as‑is
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
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("io.ktor:ktor-server-auth:2.3.4")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.4")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.41.1")


    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))
}