import groovy.util.Node
import groovy.xml.XmlNodePrinter
import groovy.xml.XmlParser
import org.cyclonedx.Version
import org.sonarqube.gradle.SonarTask
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

plugins {
    id("java")
    jacoco
    checkstyle
    alias(libs.plugins.sonar.qube)
    alias(libs.plugins.versioner)
    alias(libs.plugins.owasp.dependencycheck)
    alias(libs.plugins.nexus.publish.plugin)
    alias(libs.plugins.cyclonedx.bom)
}

group = "com.github.nagyesta.abort-mission"

buildscript {
    fun optionalPropertyString(name: String): String {
        return if (project.hasProperty(name)) {
            project.property(name) as String
        } else {
            ""
        }
    }

    // Define versions in a single place
    extra.apply {
        set("gitToken", optionalPropertyString("githubToken"))
        set("gitUser", optionalPropertyString("githubUser"))
        set("ossrhUser", optionalPropertyString("ossrhUsername"))
        set("ossrhPass", optionalPropertyString("ossrhPassword"))
        set("nvdApiKey", optionalPropertyString("nvdApiKey"))
        set("suppressedCveId", optionalPropertyString("suppressedCveId"))
        set("suppressedCveArtifact", optionalPropertyString("suppressedCveArtifact"))
        set("suppressedCveReason", optionalPropertyString("suppressedCveReason"))
        set("ossIndexUser", optionalPropertyString("ossIndexUsername"))
        set("ossIndexPass", optionalPropertyString("ossIndexPassword"))
        set("repoUrl", "https://github.com/nagyesta/abort-mission")
        set("licenseName", "MIT License")
        set("licenseUrl", "https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE")
        set("maintainerId", "nagyesta")
        set("maintainerName", "Istvan Zoltan Nagy")
        set("maintainerUrl", "https://github.com/nagyesta/")
        set("scmConnection", "scm:git:https://github.com/nagyesta/abort-mission.git")
        set("scmProjectUrl", "https://github.com/nagyesta/abort-mission/")
        set("githubMavenRepoUrl", "https://maven.pkg.github.com/nagyesta/abort-mission")
        set("ossrhMavenRepoUrl", "https://ossrh-staging-api.central.sonatype.com/service/local/")
        set("ossrhSnapshotRepoUrl", "https://central.sonatype.com/repository/maven-snapshots/")
        set("sonarOrganization", "nagyesta")
        set("sonarProjectKey", "nagyesta_abort-mission")
    }
}

versioner {
    startFrom {
        major = 1
        minor = 1
        patch = 1
    }
    match {
        major = "{major}"
        minor = "{minor}"
        patch = "{patch}"
    }
    pattern {
        pattern = "%M.%m.%p"
    }
    git {
        authentication {
            https {
                token = project.extra.get("gitToken").toString()
            }
        }
    }
    tag {
        prefix = "v"
        useCommitMessage = true
    }
}

versioner.apply()

sonar {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/report.xml")
        property("sonar.javascript.lcov.reportPaths", project.file("node/build/coverage/lcov.info"))
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.sources", "src/main/java")
        property("sonar.exclusions", "**/*.md,.github/**,.idea/**")
        property("sonar.organization", rootProject.extra.get("sonarOrganization") as String)
        property("sonar.projectKey", rootProject.extra.get("sonarProjectKey") as String)
    }
}

tasks.register<DefaultTask>("suppressCve") {
    group = "owasp depedency-check"
    description = "Adds a CVE suppression to the dependency-check suppression file."
    inputs.file(file("config/dependency-check/suppressions.xml"))
    outputs.file(file("config/dependency-check/suppressions.xml"))
    inputs.property("suppressedCveId", project.ext.get("suppressedCveId"))
    inputs.property("suppressedCveArtifact", project.ext.get("suppressedCveArtifact"))
    inputs.property("suppressedCveReason", project.ext.get("suppressedCveReason"))

    val suppressionFile = file("config/dependency-check/suppressions.xml")
    val suppressedCveId = project.ext.get("suppressedCveId") as String
    val suppressedArtifact = project.ext.get("suppressedCveArtifact") as String
    val suppressionReason = project.ext.get("suppressedCveReason") as String
    doLast {
        //Parse the suppression file as XML and add a new xml element to it
        val xmlParser = XmlParser()
        val root = xmlParser.parse(suppressionFile)

        //Create new suppression element
        val newSuppression = Node(null, "suppress")
        Node(newSuppression, "notes").setValue(suppressionReason)
        Node(newSuppression, "packageUrl", mapOf(Pair("regex", "true"))).setValue(suppressedArtifact)
        Node(newSuppression, "vulnerabilityName").setValue(suppressedCveId)

        //Add to root
        root.append(newSuppression)

        //Write back to file
        val xmlNodePrinter = XmlNodePrinter(PrintWriter(FileWriter(suppressionFile)))
        xmlNodePrinter.isPreserveWhitespace = true
        xmlNodePrinter.print(root)
    }
}

subprojects {
    if (project.name != "boosters" && project.name != "mission-report") {
        apply(plugin = "java")
        apply(plugin = "org.gradle.jacoco")
        apply(plugin = "org.gradle.checkstyle")
        apply(plugin = "org.gradle.signing")
        apply(plugin = "org.owasp.dependencycheck")
        apply(plugin = "org.cyclonedx.bom")

        group = rootProject.group
        version = rootProject.version

        repositories {
            mavenCentral()
        }

        tasks.javadoc.configure {
            (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
            (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:-missing", true)
        }

        jacoco {
            toolVersion = rootProject.libs.versions.jacoco.get()
        }

        tasks.withType(SonarTask::class).forEach {
            it.dependsOn(tasks.jacocoTestReport)
            if (project.name == "flight-evaluation-report") {
                it.dependsOn(tasks.getByName(":javascriptTest"))
            }
        }

        tasks.jacocoTestReport {
            reports {
                xml.required.set(true)
                xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/report.xml"))
                csv.required.set(false)
                html.required.set(true)
                html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
            }
            dependsOn(tasks.test)
            finalizedBy(tasks.getByName("jacocoTestCoverageVerification"))
        }

        tasks.withType<JacocoCoverageVerification>().configureEach {
            inputs.file(layout.buildDirectory.file("reports/jacoco/report.xml"))

            violationRules {
                rule {
                    limit {
                        counter = "LINE"
                        value = "COVEREDRATIO"
                        minimum = BigDecimal.valueOf(0.8)
                    }
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = BigDecimal.valueOf(0.8)
                    }
                    excludes = mutableListOf(
                            "testkit",
                            "booster-junit4"
                    )
                }
                rule {
                    element = "CLASS"
                    limit {
                        counter = "LINE"
                        value = "COVEREDRATIO"
                        minimum = BigDecimal.valueOf(0.5)
                    }
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = BigDecimal.valueOf(0.5)
                    }
                    excludes = mutableListOf(
                            "com.github.nagyesta.abortmission.core.LaunchSequenceTemplate",
                            "com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger",
                            "com.github.nagyesta.abortmission.core.matcher.impl.CustomMatcher",
                            "com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets",
                            "com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets",
                            "com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets",
                            "com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets",
                            "com.github.nagyesta.abortmission.testkit.NoOpMatcher",
                            "com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher.1",
                            "com.github.nagyesta.abortmission.reporting.AbortMissionFlightEvaluationReportApp",
                            "org.springframework.boot.loader.JarLauncher"
                    )
                }
            }
        }
        java {
            withJavadocJar()
            withSourcesJar()
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.jar.configure {
            dependsOn(tasks.check)
        }

        tasks.withType<Checkstyle>().configureEach {
            configProperties = mutableMapOf<String, Any>(
                    "base_dir" to rootDir.absolutePath.toString(),
                    "cache_file" to layout.buildDirectory.file("checkstyle/cacheFile").get().asFile.absolutePath.toString()
            )
            checkstyle.toolVersion = rootProject.libs.versions.checkstyle.get()
            checkstyle.configFile = rootProject.file("config/checkstyle/checkstyle.xml")
            reports {
                xml.required.set(false)
                html.required.set(true)
                html.stylesheet = rootProject.resources.text.fromFile("config/checkstyle/checkstyle-stylesheet.xsl")
            }
        }

        //Disable metadata publishing and rely on Maven only
        tasks.withType<GenerateModuleMetadata>().configureEach {
            enabled = false
        }

        tasks.cyclonedxDirectBom {
            if (project.name.endsWith("flight-evaluation-report")) {
                projectType.set(org.cyclonedx.model.Component.Type.APPLICATION)
            } else {
                projectType.set(org.cyclonedx.model.Component.Type.LIBRARY)
            }
            schemaVersion.set(Version.VERSION_16)
            includeConfigs.set(listOf("runtimeClasspath"))
            skipConfigs.set(listOf("compileClasspath", "testCompileClasspath"))
            jsonOutput = project.layout.buildDirectory.file("reports/bom.json").get().asFile
            //noinspection UnnecessaryQualifiedReference
            val attachmentText = org.cyclonedx.model.AttachmentText()
            attachmentText.text = Base64.getEncoder().encodeToString(
                    file("${project.rootProject.projectDir}/LICENSE").readBytes()
            )
            attachmentText.encoding = "base64"
            attachmentText.contentType = "text/plain"
            //noinspection UnnecessaryQualifiedReference
            val license = org.cyclonedx.model.License()
            license.name = "MIT License"
            license.setLicenseText(attachmentText)
            license.url = "https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE"
            licenseChoice = org.cyclonedx.model.LicenseChoice().apply {
                addLicense(license)
            }
        }
    }
}

allprojects {
    if (project.name != "boosters" && project.name != "mission-report") {
        dependencyCheck {
            nvd.apiKey.set(rootProject.extra.get("nvdApiKey").toString())
            analyzers.ossIndex.enabled.set(true)
            analyzers.ossIndex.username = rootProject.extra.get("ossIndexUser").toString()
            analyzers.ossIndex.password = rootProject.extra.get("ossIndexPass").toString()
            analyzers.ossIndex.url = "https://api.guide.sonatype.com"
            cache.ossIndex.set(true)
            cache.central.set(true)
            cache.nodeAudit.set(true)
            failBuildOnCVSS.set(1.0f)
            failOnError.set(true)
            outputDirectory.set(layout.buildDirectory.dir("reports/dependency-check"))
            setSuppressionFile(rootProject.layout.projectDirectory.file("config/dependency-check/suppressions.xml").toString())
        }
    }
}

checkstyle {
    toolVersion = rootProject.libs.versions.checkstyle.get()
}

val writeVersion = tasks.register<DefaultTask>("writeVersion") {
    group = "versioning"
    description = "Writes project version to a file."
    outputs.file(layout.buildDirectory.file("version").get().asFile)
    inputs.property("version", project.version)

    val versionFile = file("build/version")
    val versionText = project.version.toString()
    doLast {
        versionFile.writeText("v${versionText}")
    }
    mustRunAfter(tasks.clean)
}.get()
tasks.build.get().dependsOn(writeVersion)

repositories {
    mavenCentral()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        html.required.set(false)
        csv.required.set(false)
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri(rootProject.extra.get("ossrhMavenRepoUrl").toString()))
            snapshotRepositoryUrl.set(uri(rootProject.extra.get("ossrhSnapshotRepoUrl").toString()))
            username = rootProject.extra.get("ossrhUser").toString()
            password = rootProject.extra.get("ossrhPass").toString()
        }
    }
}
