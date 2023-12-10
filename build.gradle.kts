group = "com.konigsoftware"
version = "1.0.0" // CURRENT KONIG VALIDATION VERSION

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.dokka") version "1.9.0"
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
        plugin("org.jetbrains.dokka")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
        implementation("org.slf4j:slf4j-simple:2.0.9")

        testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
        testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
        testImplementation("io.mockk:mockk:1.12.7")
    }

    tasks.test {
        useJUnitPlatform()

        testLogging {
            outputs.upToDateWhen { false }
            showStandardStreams = true
        }
    }

    tasks.create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        includeEmptyDirs = false
        from(tasks.named("dokkaHtml"))
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

    extensions.getByType<SigningExtension>()
        .sign(extensions.getByType<PublishingExtension>().publications.named("maven").get())
    extensions.getByType<SigningExtension>()
        .useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSPHRASE"))

    tasks.withType<Sign> {
        onlyIf { System.getenv("GPG_PRIVATE_KEY") != null }
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
