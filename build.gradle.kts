plugins {
    id("java")
    id("application")
    id("org.graalvm.buildtools.native") version "0.10.1"
}

group = "com.amankrmj.capyverse"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.5")
    annotationProcessor("info.picocli:picocli-codegen:4.7.5")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}

application {
    mainClass.set("com.amankrmj.capyverse.Main")
    applicationName = "capyverse_cli"
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("capy")
            mainClass.set("com.amankrmj.capyverse.Main")
            useFatJar.set(true)
            
            buildArgs.addAll(
                "--no-fallback",
                "--report-unsupported-elements-at-runtime",
                "-H:+ReportExceptionStackTraces",
                "-H:-CheckToolchain",
                "--enable-url-protocols=http"
            )
            // For PicoCLI
            buildArgs.add("-H:ReflectionConfigurationFiles=${project.layout.buildDirectory.get()}/resources/main/META-INF/native-image/reflect-config.json")
        }
    }
    
    agent {
        defaultMode.set("standard")
        builtinCallerFilter.set(true)
        builtinHeuristicFilter.set(true)
        enableExperimentalPredefinedClasses.set(false)
        trackReflectionMetadata.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Distribution tasks
tasks.register<Copy>("nativeInstallDist") {
    dependsOn("nativeCompile")
    group = "distribution"
    description = "Creates a distribution with the native executable"
    
    from("build/native/nativeCompile") {
        include("capy.exe")
        into("bin")
    }
    
    into("build/install/capyverse-native")
}

// Custom task to create installer with Inno Setup
tasks.register<Exec>("createInstaller") {
    dependsOn("nativeCompile")
    group = "distribution"
    description = "Create Windows installer using Inno Setup"
    
    doFirst {
        println("Creating installer with Inno Setup...")
    }
    
    commandLine(
        "cmd",
        "/c",
        "\"C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe\"",
        "installer/capyverse-installer.iss"
    )
    workingDir(projectDir)
    
    doLast {
        println("Installer created successfully!")
    }
}

// Optionally make nativeCompile automatically trigger installer creation
tasks.named("nativeCompile") {
    finalizedBy("createInstaller")
}

// Code quality and documentation
tasks.register<Javadoc>("javadocJar") {
    group = "documentation"
    description = "Generate Javadoc JAR"
}

tasks.register<Jar>("sourcesJar") {
    group = "documentation"
    description = "Generate sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.named<JavaExec>("run") {
    jvmArgs("-Dfile.encoding=UTF-8")
}
