package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "install",
        description = "Install a specific Java version.")
public class InstallJavaVersionCommand implements Callable<Integer> {
    @Option(names = {"-d", "--download"}, description = "Download and install Oracle JDK exe for the specified version")
    private boolean download = false;

    @Parameters(index = "0", description = "Java version to install (e.g., 17, 21, 11-graalvm)", arity = "0..1")
    private String version;

    @Override
    public Integer call() throws Exception {
        if (download) {
            return installVersion(version);
        }
        System.err.println("Please use -d to download and install a Java version.");
        return 1;
    }

    private Integer installVersion(String version) {
        System.out.println("Installing Java " + version + "...");
        String userProfile = System.getenv("USERPROFILE");
        String zipUrl = "https://download.oracle.com/java/24/archive/jdk-24.0.1_windows-x64_bin.zip";
        String targetDir = userProfile + "\\AppData\\Local\\capyverse\\cache\\java\\downloads";
        String targetFile = targetDir + "\\jdk-24.0.1_windows-x64_bin.zip";
        String installDir = userProfile + "\\AppData\\Local\\capyverse\\lang\\java";
        new java.io.File(targetDir).mkdirs();
        new java.io.File(installDir).mkdirs();
        try {
            downloadFile(zipUrl, targetFile);
            System.out.println("Downloaded to: " + targetFile);
            // Extract zip to installDir
            java.nio.file.Path zipPath = java.nio.file.Paths.get(targetFile);
            java.nio.file.Path extractTo = java.nio.file.Paths.get(installDir);
            try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipPath.toFile()))) {
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    java.io.File outFile = new java.io.File(extractTo.toFile(), entry.getName());
                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        outFile.getParentFile().mkdirs();
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
            System.out.println("Extracted to: " + installDir);
            String binPath = installDir + "\\bin";
            // Generate wrapper scripts for Java tools
            generateJavaToolWrappers(binPath, userProfile + "\\AppData\\Local\\capyverse\\bin");
        } catch (Exception e) {
            System.err.println("Download or extraction failed: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private void downloadFile(String urlStr, String filePath) throws Exception {
        java.net.URL url = new java.net.URL(urlStr);
        java.net.URLConnection connection = url.openConnection();
        int fileSize = connection.getContentLength();
        System.out.println("Downloading");
        try (java.io.InputStream in = connection.getInputStream();
             java.io.FileOutputStream out = new java.io.FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalRead = 0;
            int lastPercent = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (fileSize > 0) {
                    int percent = (int) ((totalRead * 100L) / fileSize);
                    if (percent != lastPercent) {
                        StringBuilder bar = new StringBuilder();
                        int totalBars = 50;
                        int greenBars = percent / 2; // Each '=' represents 10%
                        int redBars = totalBars - greenBars;
                        String GREEN = "\033[32m";
                        String RED = "\033[31m";
                        String RESET = "\033[0m";
                        bar.append(GREEN);
                        for (int i = 0; i < greenBars; i++) bar.append('=');
                        bar.append(RESET);
                        bar.append(RED);
                        for (int i = 0; i < redBars; i++) bar.append('-');
                        bar.append(RESET);
                        System.out.print("\r" + bar + " : " + percent + "%");
                        lastPercent = percent;
                    }
                }
            }
            // Print final bar at 100%
            StringBuilder bar = new StringBuilder();
            String GREEN = "\u001B[32m";
            String RESET = "\u001B[0m";
            bar.append(GREEN);
            for (int i = 0; i < 100; i++) bar.append('=');
            bar.append(RESET);
            System.out.println("\r" + bar + " : 100%");
        }
    }

    private void generateJavaToolWrappers(String binPath, String wrapperDir) {
        java.io.File binFolder = new java.io.File(binPath);
        java.io.File outDir = new java.io.File(wrapperDir);
        outDir.mkdirs();
        java.io.File[] exeFiles = binFolder.listFiles((dir, name) -> name.endsWith(".exe"));
        if (exeFiles == null) return;
        for (java.io.File exe : exeFiles) {
            String toolName = exe.getName().replaceFirst("\\.exe$", "");
            java.io.File wrapper = new java.io.File(outDir, toolName + ".cmd");
            try (java.io.PrintWriter pw = new java.io.PrintWriter(wrapper)) {
                pw.println("@echo off");
                pw.println('"' + binPath + '\\' + exe.getName() + '" %*');
            } catch (Exception e) {
                System.err.println("Failed to create wrapper for " + exe.getName() + ": " + e.getMessage());
            }
            System.out.println("✅ Created wrapper: " + wrapper.getAbsolutePath());
        }
        System.out.println("Done generating Java tool wrappers.");
    }
}

//String psCmd = "powershell -Command \""
//        + "$oldPath = [Environment]::GetEnvironmentVariable('PATH', 'User');"
//        + "if ($oldPath -notlike '*" + binPath + "*') {"
//        + "  if ($oldPath -and $oldPath.Trim().Length -gt 0) {"
//        + "    [Environment]::SetEnvironmentVariable('PATH', $oldPath.TrimEnd(';') + ';" + binPath + "', 'User')"
//        + "  } else {"
//        + "    [Environment]::SetEnvironmentVariable('PATH', '" + binPath + "', 'User')"
//        + "  }"
//        + " }\"";
//Process setxProc = new ProcessBuilder("cmd", "/c", psCmd).inheritIO().start();

//installation at dir given
//Process installProc = new ProcessBuilder(
//        "powershell",
//        "-Command",
//        "Start-Process",
//        '"' + targetFile + '"',
//        "-ArgumentList",
//        "'/s', 'INSTALLDIR=\"" + installDir + "\"'",
//        "-Verb",
//        "RunAs"
//).inheritIO().start();
