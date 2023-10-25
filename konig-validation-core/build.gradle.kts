plugins {
    id("org.jetbrains.dokka") version "1.9.0"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    implementation("com.google.protobuf:protobuf-kotlin:3.24.4")
    implementation(kotlin("reflect"))

}

tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    includeEmptyDirs = false
    from(tasks.named("dokkaHtml"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "konig-validation-proto"

            from(components["java"])

            artifact(tasks.named("javadocJar"))

            pom {
                name.set("Konig Validation Protobuf")
                description.set("Kotlin validation framework for Protobuf requests")
            }
        }
    }
}
