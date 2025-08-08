package com.amankrmj.capyverse.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JavaVersionManagerUtils {

    public static Path getJavaVersionsDirectory() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".capyverse", "java-versions");
    }

    public static String getCurrentJavaVersion() {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) return null;

        Path javaVersionsDir = getJavaVersionsDirectory();
        Path javaHomePath = Paths.get(javaHome);

        if (javaHomePath.startsWith(javaVersionsDir)) {
            return javaVersionsDir.relativize(javaHomePath).toString();
        }

        return null;
    }

    public static void setJavaHome(String javaHome) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("reg", "add", "HKCU\\Environment",
                "/v", "JAVA_HOME", "/t", "REG_SZ", "/d", javaHome, "/f");
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String error = new String(process.getErrorStream().readAllBytes());
            throw new RuntimeException("Failed to set JAVA_HOME: " + error);
        }
    }

    public static void updatePathForJava(String javaBinPath) throws IOException, InterruptedException {
        // Get current user PATH
        ProcessBuilder pb = new ProcessBuilder("reg", "query", "HKCU\\Environment", "/v", "Path");
        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();

        String currentPath = "";
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Path")) {
                String[] parts = line.trim().split("\\s+", 3);
                if (parts.length >= 3) {
                    currentPath = parts[2].trim();
                    break;
                }
            }
        }

        // Remove any existing Java paths and add new one at the beginning
        String[] pathParts = currentPath.split(";");
        String newPath = javaBinPath + ";" + Arrays.stream(pathParts)
                .filter(part -> !part.toLowerCase().contains("java") || part.trim().isEmpty())
                .collect(Collectors.joining(";"));

        // Set new PATH
        pb = new ProcessBuilder("reg", "add", "HKCU\\Environment",
                "/v", "Path", "/t", "REG_EXPAND_SZ", "/d", newPath, "/f");
        process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String error = new String(process.getErrorStream().readAllBytes());
            throw new RuntimeException("Failed to update PATH: " + error);
        }
    }
}
