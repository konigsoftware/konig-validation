dependencies {
    implementation(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "konig-validation-core"

            from(components["java"])

            artifact(tasks.named("javadocJar"))

            pom {
                name.set("Konig Validation Protobuf")
                description.set("Core Kotlin validation framework")
            }
        }
    }
}
