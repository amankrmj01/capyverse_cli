package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "current", description = "Show current active Java version")
public class CurrentJavaVersionCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        try {
            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome == null) {
                System.out.println("JAVA_HOME not set");
            } else {
                System.out.println("JAVA_HOME: " + javaHome);
            }

            // Try to get version from java command
            ProcessBuilder pb = new ProcessBuilder("java", "-version");
            Process process = pb.start();
            String output = new String(process.getErrorStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Active Java version:");
                System.out.println(output.trim());
            } else {
                System.out.println("No Java found in PATH");
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error getting current Java version: " + e.getMessage());
            return 1;
        }
    }
}
