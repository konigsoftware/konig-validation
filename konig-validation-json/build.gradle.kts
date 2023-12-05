dependencies {
    implementation(project(":konig-validation-core"))

    implementation(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "konig-validation-json"

            from(components["java"])

            artifact(tasks.named("javadocJar"))

            pom {
                name.set("Konig Validation JSON")
                description.set("Kotlin validation framework for JSON requests")
            }
        }
    }
}
