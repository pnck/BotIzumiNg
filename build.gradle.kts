plugins {
    java
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.lightless.bot"
version = "1.0.2-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
}

dependencies {
//    testCompile("junit", "junit", "4.12")
//    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.mamoe:mirai-core-qqandroid:0.40.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:1.7.9")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.apache.logging.log4j:log4j-api:2.13.1")
    implementation("org.apache.logging.log4j:log4j-core:2.13.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.1")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.beust:klaxon:5.0.1")
    implementation("org.jetbrains.exposed:exposed-core:0.23.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.23.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.23.1")
    implementation("org.xerial:sqlite-jdbc:3.21.0.1")
    implementation("org.jetbrains.exposed", "exposed-jodatime", "0.23.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "me.lightless.bot.MainAppKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}