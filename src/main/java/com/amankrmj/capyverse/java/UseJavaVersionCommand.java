package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "use", description = "Switch to a specific Java version")
public class UseJavaVersionCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Java version to use")
    private String version;

    @Override
    public Integer call() {
        try {
            Path javaVersionsDir = JavaVersionManagerUtils.getJavaVersionsDirectory();
            Path versionPath = javaVersionsDir.resolve(version);

            if (!Files.exists(versionPath)) {
                System.err.println("Java version not found: " + version);
                System.err.println("Available versions:");
                if (Files.exists(javaVersionsDir)) {
                    Files.list(javaVersionsDir)
                            .filter(Files::isDirectory)
                            .map(path -> path.getFileName().toString())
                            .forEach(v -> System.err.println("  " + v));
                }
                return 1;
            }

            // Set JAVA_HOME
            JavaVersionManagerUtils.setJavaHome(versionPath.toString());

            // Update PATH
            JavaVersionManagerUtils.updatePathForJava(versionPath.resolve("bin").toString());

            System.out.println("Switched to Java version: " + version);
            System.out.println("JAVA_HOME: " + versionPath);
            System.out.println("Note: Restart your terminal to see the changes.");

            return 0;
        } catch (Exception e) {
            System.err.println("Error switching Java version: " + e.getMessage());
            return 1;
        }
    }
}
