package com.amankrmj.capyverse.java;

import com.amankrmj.capyverse.java.services.JavaDownloadService;
import com.amankrmj.capyverse.java.services.JavaVersionFetchService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Downloads, installs, and configures a Java JDK for Capyverse usage.
 */
@Command(
        name = "install",
        description = "Install a specific Java version."
)
public class InstallJavaVersionCommand implements Callable<Integer> {
    private JavaVersionFetchService versionFetchService;
    @Option(
            names = {"-v"},
            description = "Download and install Oracle JDK exe for the specified version"
    )
    private boolean v = false;

    @Parameters(
            index = "0",
            description = "Java version to install (e.g., 17, 21, 11-graalvm)",
            arity = "0..1"
    )
    private String version;

    @Override
    public Integer call() {
        if (v) {
            return installJavaVersion(version);
        }
        System.err.println("Please use -v to download and install a Java version.");
        return 1;
    }


    private Integer installJavaVersion(String version) {
        final String userProfile = System.getenv("USERPROFILE");
        // Get the download URL from the local server using service
        versionFetchService = new JavaVersionFetchService();
        String zipUrl = versionFetchService.getDownloadUrl(version);
        if (zipUrl == null || zipUrl.isEmpty()) {
            System.err.println("Could not get JDK download URL for version: " + version);
            return 1;
        }
        final String targetDir = userProfile + "\\AppData\\Local\\capyverse\\cache\\java\\downloads";
        final String targetFile = targetDir + "\\jdk-" + version + ".zip";
        final String installDir = userProfile + "\\AppData\\Local\\capyverse\\lang\\java";
        final String binPath = installDir + "\\jdk-" + version + "\\bin";
        final String wrapperDir = userProfile + "\\AppData\\Local\\capyverse\\bin";

        // Ensure directories exist
        new File(targetDir).mkdirs();
        new File(installDir).mkdirs();
        new File(wrapperDir).mkdirs();

        try {
            // Print download start with Unicode fallback
            String downloadMsg = "\u2B07\uFE0F  Downloading Java JDK..."; // Unicode for ⬇️
            try {
                System.out.println(downloadMsg);
            } catch (Exception e) {
                System.out.println("Downloading Java JDK...");
            }
            JavaDownloadService downloadService = new JavaDownloadService(zipUrl, targetDir, installDir, "jdk-" + version + ".zip");
            downloadService.download();

            System.out.println("🗜️  Extracting...");
            downloadService.unzipFile();

            // Write configuration file
            File configFile = new File(installDir, "java_config.txt");
            if (configFile.exists()) {
                System.out.println("\u2B07\uFE0F  Config file already exists: " + configFile.getAbsolutePath());
            } else {
                writeJavaConfig(configFile, binPath);
            }

            // Generate batch setup script
            File setupCmdFile = new File(installDir, "setup_java.cmd");
            if (setupCmdFile.exists()) {
                System.out.println("\u2B07\uFE0F  Batch setup script already exists: " + setupCmdFile.getAbsolutePath());
            } else {
                writeSetupCmd(setupCmdFile, binPath);
            }

            System.out.println("\u2705 Java installed successfully!");
            return 0;

        } catch (Exception e) {
            System.err.println("🔴 Installation failed: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Writes a configuration text file for java path.
     */
    private void writeJavaConfig(File configFile, String binPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(configFile, "UTF-8")) {
            writer.println(binPath);
//            writer.println("installed_by=CapyverseInstaller");
        }
        System.out.println("📑 Wrote configuration: " + configFile.getAbsolutePath());
    }

    /**
     * Writes a batch script (setup_java.cmd) for setting JAVA_HOME and PATH.
     */
    private void writeSetupCmd(File scriptFile, String binPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(scriptFile, "UTF-8")) {
            writer.println("@echo off");
            writer.println("setlocal enabledelayedexpansion");
            writer.println();
            writer.println("set \"CONFIG_FILE=%~dp0java_config.txt\"");
            writer.println();
            writer.println(":: Read JAVA_HOME from config file");
            writer.println("if not exist \"%CONFIG_FILE%\" (");
            writer.println("    echo ❌ Config file not found at: \"%CONFIG_FILE%\"");
            writer.println("    exit /b 1");
            writer.println(")");
            writer.println();
            writer.println("set \"JAVA_HOME=\"");
            writer.println("for /f \"usebackq delims=\" %%i in (\"%CONFIG_FILE%\") do (");
            writer.println("    set \"JAVA_HOME=%%i\"");
            writer.println("    goto :found");
            writer.println(")");
            writer.println();
            writer.println(":found");
            writer.println("if not exist \"!JAVA_HOME!\\bin\" (");
            writer.println("    echo ❌ Java bin folder not found at \"!JAVA_HOME!\\bin\"");
            writer.println("    exit /b 1");
            writer.println(")");
            writer.println();
            writer.println(":: Resolve target folder: ../../bin relative to JAVA_HOME");
            writer.println("set \"TARGET_DIR=!JAVA_HOME!\\..\\..\\..\\bin\"");
            writer.println("for %%d in (\"!TARGET_DIR!\") do set \"TARGET_DIR=%%~fd\"");
            writer.println();
            writer.println(":: Create target directory if missing");
            writer.println("if not exist \"!TARGET_DIR!\" (");
            writer.println("    echo 📂 Creating target directory: \"!TARGET_DIR!\"");
            writer.println("    mkdir \"!TARGET_DIR!\" || (");
            writer.println("        echo ❌ Failed to create target directory.");
            writer.println("        exit /b 1");
            writer.println("    )");
            writer.println(")");
            writer.println();
            writer.println(":: Generate wrapper scripts in TARGET_DIR");
            writer.println("echo Creating command wrappers in \"!TARGET_DIR!\"...");
            writer.println("for %%f in (\"!JAVA_HOME!\\bin\\*.exe\") do (");
            writer.println("    >\"!TARGET_DIR!\\%%~nf.cmd\" (");
            writer.println("        echo @echo off");
            writer.println("        echo \"%JAVA_HOME%\\bin\\%%~nxf\" %%*");
            writer.println("    )");
            writer.println("    echo ✅ Created wrapper: %%~nf.cmd");
            writer.println(")");
            writer.println();
            writer.println("echo 🎯 Done — wrappers are in \"!TARGET_DIR!\"");
            writer.println("exit /b 0");
        }
        System.out.println("⚙️  Wrote batch script: " + scriptFile.getAbsolutePath());
    }

}
