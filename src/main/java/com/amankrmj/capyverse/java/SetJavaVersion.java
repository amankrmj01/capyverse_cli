package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.PrintWriter;

@Command(name = "set", description = "Set the active Java version")
public class SetJavaVersion implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Set globally")
    private boolean global;

    @Option(names = {"-t", "--temp"}, description = "Set temporarily")
    private boolean temp;

    @Parameters(index = "0", description = "Java version to set (e.g., 20, 24.0.1)")
    private String version;

    private void setGlobalJavaVersion(String version) {
        String userProfile = System.getenv("USERPROFILE");
        String javaDirPath = userProfile + "\\AppData\\Local\\capyverse\\lang\\java";
        File javaDir = new File(javaDirPath);
        File[] folders = javaDir.listFiles(File::isDirectory);
        if (folders == null || folders.length == 0) {
            System.err.println("No Java versions found in " + javaDirPath);
            return;
        }
        String selectedFolder = null;
        for (File folder : folders) {
            String name = folder.getName();
            int dashIdx = name.indexOf('-');
            if (dashIdx != -1 && dashIdx < name.length() - 1) {
                String folderVersion = name.substring(dashIdx + 1);
                if (folderVersion.equals(version)) {
                    selectedFolder = folder.getAbsolutePath();
                    break;
                }
            }
        }
        if (selectedFolder == null) {
            System.err.println("Version " + version + " not found in " + javaDirPath);
            return;
        }
        String binPath = selectedFolder;
        String configFilePath = userProfile + "\\AppData\\Local\\capyverse\\lang\\java\\java_config.txt";
        try (PrintWriter writer = new PrintWriter(configFilePath, "UTF-8")) {
            writer.println(binPath);
        } catch (Exception e) {
            System.err.println("Failed to write config: " + e.getMessage());
            return;
        }
        // Run setup_java.cmd after updating config
        String setupCmdPath = userProfile + "\\AppData\\Local\\capyverse\\lang\\java\\setup_java.cmd";
        try {
            Process process = new ProcessBuilder("cmd", "/c", setupCmdPath).inheritIO().start();
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Failed to run setup_java.cmd: " + e.getMessage());
        }
        System.out.println("✅ Set Java version to " + version + " globally.");
    }

    @Override
    public void run() {
        if (global) {
            setGlobalJavaVersion(version);
        } else if (temp) {
            System.out.println("Temporary set (-t) is not implemented yet.");
        } else {
            System.err.println("Please specify either -g (global) or -t (temp) flag.");
        }
    }
}
