import org.gradle.internal.DefaultTaskExecutionRequest
import java.lang.Boolean.parseBoolean

plugins {
    id("zenithproxy.plugin.dev") version "1.0.1-SNAPSHOT"
    id("org.openapi.generator") version "7.25.0"
    id("io.freefair.lombok") version "9.5.0"
}

group = property("maven_group") as String
version = property("plugin_version") as String
val mc = property("mc") as String
val pluginId = property("plugin_id") as String

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

zenithProxyPlugin {
    templateProperties = mapOf(
        // variables in your BuildConstants.java template class
        "version" to project.version,
        "mc_version" to mc,
        "plugin_id" to pluginId,
        "maven_group" to group as String,
        "plugin_name" to project.name,
    )
    // the minimum supported java version for users of your plugin
    javaReleaseVersion = JavaLanguageVersion.of(21)
}

repositories {
//    maven("https://maven.2b2t.vc/snapshots") {
//        description = "ZenithProxy Prereleases"
//    }
    maven("https://maven.2b2t.vc/releases") {
        description = "ZenithProxy Releases"
    }
    maven("https://maven.2b2t.vc/remote") {
        description = "Dependencies used by ZenithProxy"
    }
}

dependencies {
    zenithProxy("com.zenith:ZenithProxy:$mc-SNAPSHOT")
    compileOnly("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

project.afterEvaluate {
    if (parseBoolean(System.getProperty("idea.sync.active"))) {
        val startParameter = project.gradle.startParameter
        val taskRequests = startParameter.taskRequests.toMutableList()
        taskRequests += DefaultTaskExecutionRequest(listOf(tasks.openApiGenerate.name))
        startParameter.setTaskRequests(taskRequests)
    }
}

tasks {
    withType(JavaCompile::class) {
        dependsOn(openApiGenerate)
    }
}

sourceSets.main {
    java.srcDir(project.layout.buildDirectory.dir("generated/sources/openapi/api.2b2t.vc/src/main/java"))
}

openApiGenerate {
    inputSpec = file(project.layout.projectDirectory.dir("openapi").file("api.2b2t.vc.openapi.json"))
    generatorName = "java"
    outputDir = project.layout.buildDirectory.dir("generated/sources/openapi/api.2b2t.vc")
    skipValidateSpec = false
    library = "native"
    val defaultPackage = "$group.openapi"
    apiPackage = "$defaultPackage.handler"
    modelPackage = "$defaultPackage.model"
    invokerPackage = "$defaultPackage.handler"
    generateApiTests = false
    generateModelTests = false
    configOptions = mapOf(
        "dateLibrary" to "java8",
        "useJackson3" to "true",
        "useJspecify" to "true",
        "openApiNullable" to "false",
        "hideGenerationTimestamp" to "true"
    )
}
