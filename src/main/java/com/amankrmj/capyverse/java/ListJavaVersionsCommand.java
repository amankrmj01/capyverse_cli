package com.amankrmj.capyverse.java;

import com.amankrmj.capyverse.java.services.JavaVersionFetchService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "list", description = "List all installed Java versions")
public class ListJavaVersionsCommand implements Callable<Integer> {
    private List<String> versionsInstalled = new ArrayList<>();


    private JavaVersionFetchService versionFetchService;

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
        versionFetchService = new JavaVersionFetchService();
        versionFetchService.listLocalVersions(
                ""
        );

    }

    private void listManagedVersions() throws Exception {
        versionFetchService = new JavaVersionFetchService();
        versionFetchService.fetchAvailableVersionsList();
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
