package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "which", description = "Show path to current Java installation")
public class WhichJavaVersionCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        try {
            ProcessBuilder pb = new ProcessBuilder("where", "java");
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Java executable locations:");
                System.out.println(output.trim());
            } else {
                System.out.println("Java not found in PATH");
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error finding Java: " + e.getMessage());
            return 1;
        }
    }
}
