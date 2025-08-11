package com.amankrmj.capyverse.java.services;

import com.amankrmj.capyverse.common.services.FetchService;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.amankrmj.capyverse.common.utils.CliColor;

public class JavaVersionFetchService implements FetchService {
    private final List<String> versionsInstalled = new ArrayList<>();
    private final String userProfile = System.getenv("USERPROFILE");
    private final File javaDir = new File(userProfile + "\\AppData\\Local\\capyverse\\lang\\java");
    private final String url = "http://localhost:8080/javaversions/versions";


    @Override
    public void fetchAvailableVersionsList() {
        populateInstalledVersions();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String[] versions = response.body().split(";");
                for (String version : versions) {
                    if (versionsInstalled.contains(version.trim())) {
                        System.out.println(CliColor.GREEN + "\t\t" + version.trim() + " - installed" + CliColor.RESET);
                    } else if (!version.trim().isEmpty()) {
                        System.out.println(CliColor.BLUE + "\t\t" + version.trim() + CliColor.RESET);
                    }
                }
            } else {
                System.err.println("Failed to fetch versions. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching versions: " + e.getMessage());
        }
    }

    @Override
    public String getDownloadUrl(String version) {
        if (version == null || version.trim().isEmpty()) {
            System.err.println("Version must not be empty.");
            return null;
        }
        String endpoint = "http://localhost:8080/javaversions?version=" + version.trim();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String url = response.body().trim();
                if (url.isEmpty()) {
                    System.err.println("No download URL returned for version: " + version);
                    return null;
                }
                return url;
            } else {
                System.err.println("Failed to fetch JDK URL. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching JDK URL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void populateInstalledVersions() {
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

    @Override
    public void listLocalVersions(String path) {
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
}
