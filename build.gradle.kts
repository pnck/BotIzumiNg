plugins {
    java
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.lightless.bot"
version = "1.0.4-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
}

dependencies {
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
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

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
