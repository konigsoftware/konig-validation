import com.google.protobuf.gradle.id

plugins {
    id("org.jetbrains.dokka") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    implementation(project(":konig-validation-proto"))
    implementation(project(":konig-validation-core"))
    implementation(project(":konig-validation-proto"))

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
