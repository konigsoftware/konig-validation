import com.google.protobuf.gradle.id

plugins {
    id("org.jetbrains.dokka") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(mapOf("path" to ":konig-validation-proto")))
    implementation(project(":konig-validation-core"))
    implementation(project(":konig-validation-proto"))

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")

    implementation("com.google.protobuf:protobuf-kotlin:3.24.4")
    protobuf(files("proto/"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.1"
    }

    generateProtoTasks {
        all().forEach {
            // https://github.com/google/protobuf-gradle-plugin/issues/331#issuecomment-543333726
            it.doFirst {
                delete(it.outputs)
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
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
