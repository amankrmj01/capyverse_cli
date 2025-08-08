package com.amankrmj.capyverse.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "path",
        description = "Manage environment PATH variables",
        subcommands = {
                PathCommand.AddCommand.class,
                PathCommand.RemoveCommand.class,
                PathCommand.ListCommand.class
        })
public class PathCommand implements Callable<Integer> {

    // Helper methods
    private static String getCurrentPath(String scope) throws IOException, InterruptedException {
        String hive = scope.equals("SYSTEM") ? "HKLM" : "HKCU";
        String key = scope.equals("SYSTEM") ?
                "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment" :
                "Environment";

        ProcessBuilder pb = new ProcessBuilder("reg", "query", hive + "\\" + key, "/v", "Path");
        Process process = pb.start();

        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();

        // Parse reg output to get PATH value
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Path")) {
                String[] parts = line.trim().split("\\s+", 3);
                if (parts.length >= 3) {
                    return parts[2].trim();
                }
            }
        }
        return "";
    }

    private static void setPathVariable(String scope, String newPath) throws IOException, InterruptedException {
        String hive = scope.equals("SYSTEM") ? "HKLM" : "HKCU";
        String key = scope.equals("SYSTEM") ?
                "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment" :
                "Environment";

        ProcessBuilder pb = new ProcessBuilder("reg", "add", hive + "\\" + key,
                "/v", "Path", "/t", "REG_EXPAND_SZ", "/d", newPath, "/f");
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String error = new String(process.getErrorStream().readAllBytes());
            throw new RuntimeException("Failed to update registry: " + error);
        }

        // Broadcast environment change
        broadcastEnvironmentChange();
    }

    private static boolean isPathAlreadyInPATH(String currentPath, String pathToCheck) {
        if (currentPath.isEmpty()) return false;

        List<String> paths = Arrays.asList(currentPath.toLowerCase().split(";"));
        return paths.contains(pathToCheck.toLowerCase().trim());
    }

    private static String removePathFromPATH(String currentPath, String pathToRemove) {
        String[] paths = currentPath.split(";");
        StringBuilder newPath = new StringBuilder();

        for (String path : paths) {
            String trimmedPath = path.trim();
            if (!trimmedPath.isEmpty() && !trimmedPath.equalsIgnoreCase(pathToRemove.trim())) {
                if (newPath.length() > 0) {
                    newPath.append(";");
                }
                newPath.append(trimmedPath);
            }
        }

        return newPath.toString();
    }

    private static void broadcastEnvironmentChange() {
        try {
            // Notify system of environment changes
            ProcessBuilder pb = new ProcessBuilder("powershell", "-Command",
                    "[System.Environment]::SetEnvironmentVariable('TEMP_REFRESH', [System.Guid]::NewGuid().ToString(), 'User'); " +
                            "[System.Environment]::SetEnvironmentVariable('TEMP_REFRESH', $null, 'User')");
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            // Silent fail - environment change broadcast is optional
        }
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Use 'path --help' to see available subcommands:");
        System.out.println("  add    - Add directory to PATH");
        System.out.println("  remove - Remove directory from PATH");
        System.out.println("  list   - List current PATH entries");
        return 0;
    }

    @Command(name = "add", description = "Add a directory to user PATH environment variable")
    static class AddCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Directory path to add to PATH")
        private String pathToAdd;

        @Option(names = {"-s", "--system"}, description = "Add to system PATH instead of user PATH")
        private boolean systemPath = false;

        @Option(names = {"-f", "--force"}, description = "Add even if path already exists")
        private boolean force = false;

        @Override
        public Integer call() throws Exception {
            try {
                String scope = systemPath ? "SYSTEM" : "USER";
                String currentPath = getCurrentPath(scope);

                // Check if path already exists
                if (!force && isPathAlreadyInPATH(currentPath, pathToAdd)) {
                    System.out.println("Path already exists in " + scope + " PATH: " + pathToAdd);
                    return 0;
                }

                // Add to PATH
                String newPath = currentPath.isEmpty() ? pathToAdd : currentPath + ";" + pathToAdd;
                setPathVariable(scope, newPath);

                System.out.println("Successfully added to " + scope + " PATH: " + pathToAdd);
                System.out.println("Note: Restart your terminal or IDE to see the changes.");
                return 0;

            } catch (Exception e) {
                System.err.println("Error adding to PATH: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "remove", description = "Remove a directory from user PATH environment variable")
    static class RemoveCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Directory path to remove from PATH")
        private String pathToRemove;

        @Option(names = {"-s", "--system"}, description = "Remove from system PATH instead of user PATH")
        private boolean systemPath = false;

        @Override
        public Integer call() throws Exception {
            try {
                String scope = systemPath ? "SYSTEM" : "USER";
                String currentPath = getCurrentPath(scope);

                if (!isPathAlreadyInPATH(currentPath, pathToRemove)) {
                    System.out.println("Path not found in " + scope + " PATH: " + pathToRemove);
                    return 0;
                }

                // Remove from PATH
                String newPath = removePathFromPATH(currentPath, pathToRemove);
                setPathVariable(scope, newPath);

                System.out.println("Successfully removed from " + scope + " PATH: " + pathToRemove);
                System.out.println("Note: Restart your terminal or IDE to see the changes.");
                return 0;

            } catch (Exception e) {
                System.err.println("Error removing from PATH: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "list", description = "List current PATH entries")
    static class ListCommand implements Callable<Integer> {

        @Option(names = {"-s", "--system"}, description = "Show system PATH instead of user PATH")
        private boolean systemPath = false;

        @Option(names = {"-a", "--all"}, description = "Show both user and system PATH")
        private boolean showAll = false;

        @Override
        public Integer call() throws Exception {
            try {
                if (showAll) {
                    System.out.println("=== USER PATH ===");
                    listPath("USER");
                    System.out.println("\n=== SYSTEM PATH ===");
                    listPath("SYSTEM");
                } else {
                    String scope = systemPath ? "SYSTEM" : "USER";
                    System.out.println("=== " + scope + " PATH ===");
                    listPath(scope);
                }
                return 0;

            } catch (Exception e) {
                System.err.println("Error listing PATH: " + e.getMessage());
                return 1;
            }
        }

        private void listPath(String scope) throws IOException, InterruptedException {
            String currentPath = getCurrentPath(scope);
            if (currentPath.isEmpty()) {
                System.out.println("No " + scope + " PATH entries found.");
                return;
            }

            String[] paths = currentPath.split(";");
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i].trim();
                if (!path.isEmpty()) {
                    System.out.println((i + 1) + ". " + path);
                }
            }
        }
    }
}
