group = "com.konigsoftware"
version = "1.0.0" // CURRENT KONIG VALIATION VERSION

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("com.adarshr.test-logger") version "3.2.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    `maven-publish`
}

repositories {
    mavenCentral()
}

// Applied to all subprojects
subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.adarshr.test-logger")
        plugin("java")
        plugin("maven-publish")
        plugin("signing")
    }

    repositories {
        mavenCentral()
    }

    extensions.getByType<PublishingExtension>().publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group as String
            version = rootProject.version as String

            pom {
                url.set("https://github.com/konigsoftware/konig-validation")

                scm {
                    connection.set("scm:git:https://github.com/konigsoftware/konig-validation.git")
                    developerConnection.set("scm:git:git@github.com:konigsoftware/konig-validation.git")
                    url.set("https://github.com/konigsoftware/konig-validation")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("konigsoftware.com")
                        name.set("Konig Validation Contributors")
                        email.set("reidbuzby@gmail.com")
                        url.set("https://konigsoftware.com")
                        organization.set("Konig Software")
                        organizationUrl.set("https://konigsoftware.com")
                    }
                }
            }
        }
    }

    extensions.getByType<SigningExtension>().sign(extensions.getByType<PublishingExtension>().publications.named("maven").get())
    extensions.getByType<SigningExtension>().useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSPHRASE"))

    tasks.withType<Sign> {
        onlyIf { System.getenv("GPG_PRIVATE_KEY") != null }
    }

    tasks.test {
        useJUnitPlatform()
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}
