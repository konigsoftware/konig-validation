dependencies {
    implementation(project(":konig-validation-core"))

    implementation(kotlin("reflect"))

    implementation("com.google.protobuf:protobuf-kotlin:3.24.4")
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
