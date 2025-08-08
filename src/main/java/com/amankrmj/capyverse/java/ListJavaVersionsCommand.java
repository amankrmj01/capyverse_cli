package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "list", description = "List all installed Java versions")
public class ListJavaVersionsCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        try {
            System.out.println("=== Managed Java Versions ===");
            listManagedVersions();
            return 0;
        } catch (Exception e) {
            System.err.println("Error listing Java versions: " + e.getMessage());
            return 1;
        }
    }

    private void listManagedVersions() throws IOException {
        Path javaVersionsDir = JavaVersionManagerUtils.getJavaVersionsDirectory();
        if (!Files.exists(javaVersionsDir)) {
            System.out.println("No managed Java versions found.");
            System.out.println("Use 'capyverse java install <version>' to install Java versions.");
            return;
        }

        String currentVersion = JavaVersionManagerUtils.getCurrentJavaVersion();

        Files.list(javaVersionsDir)
                .filter(Files::isDirectory)
                .map(path -> path.getFileName().toString())
                .sorted()
                .forEach(version -> {
                    String marker = version.equals(currentVersion) ? " (current)" : "";
                    System.out.println("  " + version + marker);
                });
    }

    private void findAllJavaInstallations() {
        // Common Java installation paths
        String[] commonPaths = {
                "C:\\Program Files\\Java",
                "C:\\Program Files (x86)\\Java",
                "C:\\Program Files\\Eclipse Adoptium",
                "C:\\Program Files\\Microsoft",
                System.getProperty("user.home") + "\\.jdks"
        };

        System.out.println("Scanning common installation directories...");

        for (String basePath : commonPaths) {
            File baseDir = new File(basePath);
            if (baseDir.exists() && baseDir.isDirectory()) {
                System.out.println("\n" + basePath + ":");
                File[] javaInstalls = baseDir.listFiles(File::isDirectory);
                if (javaInstalls != null) {
                    for (File install : javaInstalls) {
                        String version = detectJavaVersion(install);
                        System.out.println("  " + install.getName() + " " + version);
                    }
                }
            }
        }

        // Also check JAVA_HOME
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null) {
            System.out.println("\nJAVA_HOME: " + javaHome);
            System.out.println("  Version: " + detectJavaVersion(new File(javaHome)));
        }
    }

    private String detectJavaVersion(File javaHome) {
        try {
            File javaBin = new File(javaHome, "bin/java.exe");
            if (!javaBin.exists()) {
                javaBin = new File(javaHome, "bin/java");
            }

            if (javaBin.exists()) {
                ProcessBuilder pb = new ProcessBuilder(javaBin.getAbsolutePath(), "-version");
                Process process = pb.start();
                String output = new String(process.getErrorStream().readAllBytes());
                process.waitFor();

                // Parse version from output
                String[] lines = output.split("\n");
                if (lines.length > 0) {
                    return lines[0].replaceAll(".*\"(.*)\".*", "($1)");
                }
            }
            return "(unknown)";
        } catch (Exception e) {
            return "(error: " + e.getMessage() + ")";
        }
    }
}
