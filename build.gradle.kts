plugins {
    id("zenithproxy.plugin.dev") version "1.0.1-SNAPSHOT"
    id("org.openapi.generator") version "7.24.0"
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
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")
    shade("org.openapitools:jackson-databind-nullable:0.2.11")
    /** to include dependencies into your plugin jar **/
//    shade("com.github.ben-manes.caffeine:caffeine:3.2.0")
}

tasks {
    shadowJar {
        /**
         * relocate shaded dependencies to avoid conflicts with other plugins
         * transitive dependencies should also be relocated or removed (with exclude)
         * build and examine your plugin jar contents to check
         * https://gradleup.com/shadow/configuration/relocation/
         */
//        val basePackage = "${project.group}.shadow"
//        relocate("com.github.benmanes.caffeine", "$basePackage.caffeine")

        /**
         * remove unneeded transitive dependencies
         * https://gradleup.com/shadow/configuration/dependencies/#filtering-dependencies
         */
//        dependencies {
//            exclude(dependency(":error_prone_annotations:.*"))
//            exclude(dependency(":jspecify:.*"))
//        }
    }
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
        "useJackson3" to "true"
    )
}
