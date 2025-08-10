package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Downloads, installs, and configures a Java JDK for Capyverse usage.
 */
@Command(
        name = "install",
        description = "Install a specific Java version."
)
public class InstallJavaVersionCommand implements Callable<Integer> {

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

    /**
     * Gets the JDK download URL for the given version from the local server.
     */
    private String fetchJdkUrlFromServer(String version) {
        try {
            String endpoint = "http://localhost:8080/javaversions?version=" + version;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body().trim(); // plain URL string
            } else {
                System.err.println("Failed to fetch JDK URL. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching JDK URL: " + e.getMessage());
        }
        return null;
    }

    private Integer installJavaVersion(String version) {
        final String userProfile = System.getenv("USERPROFILE");
        // Get the download URL from the local server
        String zipUrl = fetchJdkUrlFromServer(version);
        if (zipUrl == null || zipUrl.isEmpty()) {
            System.err.println("Could not get JDK download URL for version: " + version);
            return 1;
        }
        // Note: You can generalize the URLs and file-version logic as needed.
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
            System.out.println("⬇️  Downloading Java JDK...");
            downloadFileWithBar(zipUrl, targetFile);

            System.out.println("🗜️  Extracting...");
            unzip(targetFile, installDir);

            // Write configuration file
            File configFile = new File(installDir, "java_config.txt");
            if (configFile.exists()) {
                System.out.println("ℹ️  Config file already exists: " + configFile.getAbsolutePath());
            } else {
                writeJavaConfig(configFile, binPath);
            }

            // Generate batch setup script
            File setupCmdFile = new File(installDir, "setup_java.cmd");
            if (setupCmdFile.exists()) {
                System.out.println("ℹ️  Batch setup script already exists: " + setupCmdFile.getAbsolutePath());
            } else {
                writeSetupCmd(setupCmdFile, binPath);
            }

            // Generate launch wrappers in wrapperDir
//            generateJavaToolWrappers(binPath, wrapperDir);


            System.out.println("✅ Java installed successfully!");
            return 0;

        } catch (Exception e) {
            System.err.println("🔴 Installation failed: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Downloads a file from a given URL, saving it at filePath, with progress bar.
     */
    private void downloadFileWithBar(String urlStr, String filePath) throws IOException, InterruptedException {
        // Use HttpClient for better reliability and compatibility
        java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().build();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(urlStr))
                .GET()
                .build();
        java.net.http.HttpResponse<InputStream> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofInputStream());
        int fileSize = 0;
        if (response.headers().firstValue("Content-Length").isPresent()) {
            try {
                fileSize = Integer.parseInt(response.headers().firstValue("Content-Length").get());
            } catch (NumberFormatException ignored) {
            }
        }
        InputStream in = response.body();
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int totalRead = 0, lastPercent = -1;
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (fileSize > 0) {
                    int percent = (int) ((totalRead * 100L) / fileSize);
                    if (percent != lastPercent) {
                        printProgressBar(percent);
                        lastPercent = percent;
                    }
                }
            }
            printProgressBar(100);
            System.out.println();
        } finally {
            in.close();
        }
    }

    private void printProgressBar(int percent) {
        final int barLength = 50;
        int filled = (percent * barLength) / 100;
        System.out.print("\r[");
        String GREEN = "\u001B[32m";
        String RED = "\u001B[31m";
        String YELLOW = "\u001B[33m";
        String RESET = "\u001B[0m";
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                System.out.print(GREEN + "=" + RESET);
            } else {
                System.out.print(RED + "-" + RESET);
            }
        }
        System.out.print("] ");
        System.out.print(YELLOW + percent + "%" + RESET);
    }

    /**
     * Unzips an archive into a target directory.
     * Strips leading folder if needed.
     */
    private void unzip(String zipFilePath, String destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
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

//    private void generateJavaToolWrappers(String binPath, String wrapperDir) {
//        File binFolder = new File(binPath);
//        File[] exeFiles = binFolder.listFiles((dir, name) -> name.endsWith(".exe"));
//        if (exeFiles == null) return;
//
//        for (File exe : exeFiles) {
//            String toolName = exe.getName().replaceFirst("\\.exe$", "");
//            File wrapper = new File(wrapperDir, toolName + ".cmd");
//            try (PrintWriter pw = new PrintWriter(wrapper, "UTF-8")) {
//                pw.println("@echo off");
//                pw.println("\"" + exe.getAbsolutePath() + "\" %*");
//            } catch (Exception e) {
//                System.err.printf("Failed to create wrapper for %s: %s%n", exe.getName(), e.getMessage());
//            }
//            System.out.println("✅ Created wrapper: " + wrapper.getAbsolutePath());
//        }
//        System.out.println("🔗 All Java tool wrappers created in: " + wrapperDir);
//    }
}
