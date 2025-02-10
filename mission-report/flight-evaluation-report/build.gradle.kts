import com.github.gradle.node.npm.task.NpxTask
import groovy.util.Node
import groovy.util.NodeList

plugins {
    id("java")
    signing
    `maven-publish`
    alias(libs.plugins.licensee.plugin)
    //noinspection SpellCheckingInspection
    alias(libs.plugins.lombok)
    alias(libs.plugins.node)
    alias(libs.plugins.shadow)
}

group = "${rootProject.group}.reports"

extra.apply {
    set("artifactDisplayName", "Flight Evaluation Report")
    set("artifactDescription", "Core reporting module of Abort Mission generating reports from telemetry data.")
}

dependencies {
    implementation(libs.json.schema.validator)
    implementation(libs.jackson.databind)
    implementation(libs.thymeleaf)
    implementation(libs.thymeleaf.extras.java8time)
    implementation(libs.bundles.logback.report)
    implementation(libs.findbugs.jsr305)
    annotationProcessor(libs.lombok)
    testImplementation(libs.jupiter.core)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.mockito.core)
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allow("EPL-1.0")
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

node {
    download.set(true)
    version.set("22.13.1")
    nodeProjectDir.set(file("${project.projectDir}/node"))
}

tasks.register<NpxTask>("javascriptTest") {
    inputs.files(fileTree("node/src/") {
        include("**.js")
    })
    inputs.files(fileTree("node/test/") {
        include("**.test.js")
    })
    command = "npm"
    group = "build"
    args.set(listOf("test"))
    dependsOn(tasks.named("nodeSetup"))
    dependsOn(tasks.named("npmSetup"))
    dependsOn(tasks.named("npmInstall"))
}

tasks.register<Delete>("cleanTemplates") {
    delete(layout.buildDirectory.dir("html-view").get().asFileTree)
    delete(layout.buildDirectory.dir("resources/main/templates/html").get().asFileTree)
}

tasks.register<NpxTask>("processTemplates") {
    inputs.files(fileTree("node/src/") {
        include("**.js")
    })
    inputs.files(fileTree("node/css/") {
        include("**.css")
    })
    outputs.dir(layout.buildDirectory.dir("html-view").get().asFile)
    outputs.dir(layout.buildDirectory.dir("resources/main/templates/html").get().asFile)
    command = "grunt"
    group = "build"
    args.set(listOf())
    dependsOn(tasks.named("javascriptTest"))
}

tasks.shadowJar {
    manifest.attributes["Main-Class"] = "com.github.nagyesta.abortmission.reporting.AbortMissionFlightEvaluationReportApp"
    append("META-INF/LICENSE")
    append("META-INF/LICENSE.txt")
    append("META-INF/NOTICE")
    append("META-INF/NOTICE.txt")
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    archiveClassifier.set("")
}
tasks.build.get().finalizedBy(tasks.shadowJar)

tasks.processResources.get().finalizedBy(tasks.named("processTemplates"))
tasks.jar.get().dependsOn(tasks.named("processTemplates"))
tasks.shadowJar.get().dependsOn(tasks.named("processTemplates"))
tasks.checkstyleMain.get().dependsOn(tasks.named("processTemplates"))
tasks.compileTestJava.get().dependsOn(tasks.named("processTemplates"))
tasks.javadoc.get().dependsOn(tasks.named("processTemplates"))

tasks.test {
    useJUnitPlatform()
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
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            artifact(tasks["shadowJar"])
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
            }
            pom.withXml {
                asNode().apply {
                    (get("dependencies") as NodeList).forEach { depsNode ->
                        ((depsNode as Node).get("dependency") as NodeList).forEach { depNode ->
                            depsNode.remove(depNode as Node)
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
