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
//            downloadFile(zipUrl, targetFile);
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
            // Set user PATH to JDK bin using PowerShell
            String binPath = installDir + "\\jdk-24.0.1\\bin";
            String psCmd = "powershell -Command \""
                    + "$oldPath = [Environment]::GetEnvironmentVariable('PATH', 'User');"
                    + "if ($oldPath -notlike '*" + binPath + "*') {"
                    + "  if ($oldPath -and $oldPath.Trim().Length -gt 0) {"
                    + "    [Environment]::SetEnvironmentVariable('PATH', $oldPath.TrimEnd(';') + ';" + binPath + "', 'User')"
                    + "  } else {"
                    + "    [Environment]::SetEnvironmentVariable('PATH', '" + binPath + "', 'User')"
                    + "  }"
                    + " }\"";
            Process setxProc = new ProcessBuilder("cmd", "/c", psCmd).inheritIO().start();
            int setxExit = setxProc.waitFor();
            if (setxExit == 0) {
                System.out.println("User PATH updated to include: " + binPath);
            } else {
                System.err.println("Failed to update PATH environment variable.");
            }
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

    private void showProgressBar(String message, int percent) {
        int totalBars = 10;
        int greenBars = percent / 10;
        int redBars = totalBars - greenBars;
        String GREEN = "\033[32m";
        String RED = "\033[31m";
        String RESET = "\033[0m";
        StringBuilder bar = new StringBuilder();
        bar.append(GREEN);
        for (int i = 0; i < greenBars; i++) bar.append('=');
        bar.append(RESET);
        bar.append(RED);
        for (int i = 0; i < redBars; i++) bar.append('-');
        bar.append(RESET);
        System.out.print("\r" + message + " " + bar + " : " + percent + "%");
    }

    private boolean silentInstallexe(String exePath, String installDir) {
        try {
            System.out.println("\nInstalling...");
            // Show a countdown instead of a progress bar
            for (int i = 5; i > 0; i--) {
                System.out.print("\rInstallation will complete in " + i + " seconds...");
                Thread.sleep(1000);
            }
            System.out.print("\rStarting installation...           \n");
            Process installProc = new ProcessBuilder("exeexec", "/i", exePath, "/qn", "INSTALLDIR=" + installDir)
                    .inheritIO().start();
            int exitCode = installProc.waitFor();
            if (exitCode == 0) {
                System.out.println("exe installed successfully to: " + installDir);
                // Set user PATH to JDK bin
                String binPath = installDir + "\\bin";
                // Append binPath to existing user PATH
                String currentPath = System.getenv("PATH");
                String newPath = currentPath;
                if (!currentPath.contains(binPath)) {
                    newPath = binPath + ";" + currentPath;
                }
                Process setxProc = new ProcessBuilder("setx", "PATH", newPath)
                        .inheritIO().start();
                int setxExit = setxProc.waitFor();
                if (setxExit == 0) {
                    System.out.println("User PATH updated to include: " + binPath);
                } else {
                    System.err.println("Failed to update PATH environment variable.");
                }
                return true;
            } else {
                System.err.println("exe installation failed with exit code: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Installation failed: " + e.getMessage());
            return false;
        }
    }
}

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
