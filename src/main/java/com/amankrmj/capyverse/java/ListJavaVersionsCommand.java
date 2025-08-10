package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

@Command(name = "list", description = "List all installed Java versions")
public class ListJavaVersionsCommand implements Callable<Integer> {
    private List<String> versionsInstalled = new ArrayList<>();

    @Option(names = {"-i"}, description = "List managed Java versions from server")
    private boolean showManaged = false;

    private void populateInstalledVersions() {
        versionsInstalled.clear();
        String userProfile = System.getenv("USERPROFILE");
        File javaDir = new File(userProfile + "\\AppData\\Local\\capyverse\\lang\\java");
        File[] folders = javaDir.listFiles(File::isDirectory);
        if (folders != null && folders.length > 0) {
            for (File folder : folders) {
                String name = folder.getName();
                int dashIdx = name.indexOf('-');
                if (dashIdx != -1 && dashIdx < name.length() - 1) {
                    String version = name.substring(dashIdx + 1);
                    versionsInstalled.add(version.trim());
                }
            }
        }
    }

    private void listLocalJavaVersions() {
        populateInstalledVersions();
        if (versionsInstalled.size() > 0) {
            System.out.println("=== Locally Installed Java Versions ===");
            String GREEN = "\u001B[32m";
            String RESET = "\u001B[0m";
            for (String version : versionsInstalled) {
                System.out.println(GREEN + "\t\t" + version + RESET);
            }
        } else {
            System.out.println("No local Java versions found.");
        }
    }

    private void listManagedVersions() throws Exception {
        String urlStr = "http://localhost:8080/javaversions/versions";
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String[] versions = response.toString().split(";");
            for (String version : versions) {
                if (versionsInstalled.contains(version.trim())) {
                    System.out.println("\u001B[32m" + "\t\t" + version.trim() + " - installed" + "\u001B[0m");
                } else if (!version.trim().isEmpty()) {
                    System.out.println("\u001B[34m" + "\t\t" + version.trim() + "\u001B[0m");
                }
            }
        }
    }

    @Override
    public Integer call() {
        try {
            if (showManaged) {
                populateInstalledVersions();
                System.out.println("=== Managed Java Versions ===");
                listManagedVersions();
            } else {
                listLocalJavaVersions();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Error listing Java versions: " + e.getMessage());
            return 1;
        }
    }
}
