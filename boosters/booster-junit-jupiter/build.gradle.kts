import groovy.util.Node
import groovy.util.NodeList

plugins {
    id("java")
    signing
    `maven-publish`
    alias(libs.plugins.licensee.plugin)
}

group = "${rootProject.group}.boosters"

extra.apply {
    set("artifactDisplayName", "Abort Mission - JUnit Jupiter Booster")
    set("artifactDescription", "Booster module of Abort Mission adding JUnit Jupiter bindings.")
}

dependencies {
    implementation(project(":mission-control"))
    implementation(libs.slf4j.api)
    implementation(libs.jupiter.api)
    implementation(libs.jupiter.core)
    implementation(libs.jupiter.platform.reporting)
    testImplementation(project(":boosters:testkit"))
    testImplementation(libs.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jupiter.platform.testkit)
    constraints {
        testImplementation(libs.bundles.spring.test)
    }
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://www.eclipse.org/legal/epl-v20.html")
}

val copyLegalDocs = tasks.register<Copy>("copyLegalDocs") {
    from(file("${project.rootProject.projectDir}/LICENSE"))
    from(layout.buildDirectory.file("reports/licensee/artifacts.json").get().asFile)
    from(layout.buildDirectory.file("reports/bom.json").get().asFile)
    into(layout.buildDirectory.dir("resources/main/META-INF").get().asFile)
    rename("artifacts.json", "dependency-licenses.json")
    rename("bom.json", "SBOM.json")
}.get()
copyLegalDocs.dependsOn(tasks.licensee)
copyLegalDocs.dependsOn(tasks.cyclonedxBom)
tasks.javadoc.get().dependsOn(copyLegalDocs)
tasks.compileJava.get().dependsOn(copyLegalDocs)
tasks.processResources.get().finalizedBy(copyLegalDocs)

tasks.test {
    useJUnitPlatform {
        systemProperty("abort-mission.report.directory", layout.buildDirectory.dir("reports/abort-mission/").get().asFile)
        includeTags("unit", "integration")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri(rootProject.extra.get("githubMavenRepoUrl").toString())
            credentials {
                username = rootProject.extra.get("gitUser").toString()
                password = rootProject.extra.get("gitToken").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "abort.${project.name}"
            pom {
                name.set(project.extra.get("artifactDisplayName").toString())
                description.set(project.extra.get("artifactDescription").toString())
                url.set(rootProject.extra.get("repoUrl").toString())
                packaging = "jar"
                licenses {
                    license {
                        name.set(rootProject.extra.get("licenseName").toString())
                        url.set(rootProject.extra.get("licenseUrl").toString())
                    }
                }
                developers {
                    developer {
                        id.set(rootProject.extra.get("maintainerId").toString())
                        name.set(rootProject.extra.get("maintainerName").toString())
                        email.set(rootProject.extra.get("maintainerUrl").toString())
                    }
                }
                scm {
                    connection.set(rootProject.extra.get("scmConnection").toString())
                    developerConnection.set(rootProject.extra.get("scmConnection").toString())
                    url.set(rootProject.extra.get("scmProjectUrl").toString())
                }
                pom.withXml {
                    asNode().apply {
                        (get("dependencies") as NodeList).forEach { depsNode ->
                            ((depsNode as Node).get("dependency") as NodeList).forEach { depNode ->
                                ((depNode as Node).get("scope") as NodeList).forEach { scope ->
                                    if (scope is Node && "runtime" == scope.text()) {
                                        scope.setValue("compile")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
