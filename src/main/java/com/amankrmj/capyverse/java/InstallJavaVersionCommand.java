package com.amankrmj.capyverse.java;

import com.amankrmj.capyverse.java.model.OracleJavaVersionInfo;
import com.amankrmj.capyverse.java.services.JavaVersionAvailableFetchService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "install",
        description = "Install a specific Java version.",
        subcommands = {
                InstallJavaVersionCommand.ListAvailableCommand.class,
                InstallJavaVersionCommand.DownloadCommand.class,
                InstallJavaVersionCommand.FromFileCommand.class
        })
public class InstallJavaVersionCommand implements Callable<Integer> {

    @Option(names = {"-l", "--list"}, description = "List available Java versions")
    private boolean list = false;

    @Option(names = {"-f", "--force"}, description = "Force reinstall if already exists")
    private boolean force = false;

    @Parameters(index = "0", description = "Java version to install (e.g., 17, 21, 11-graalvm)", arity = "0..1")
    private String version;

    @Override
    public Integer call() throws Exception {
        if (list) {
            return new ListAvailableCommand().call();
        }

        if (version == null) {
            System.err.println("Please specify a version to install or use -l to list available versions");
            System.err.println("Usage: capy java install <version>");
            System.err.println("       capy java install -l");
            System.err.println("       capy java install list");
            System.err.println("       capy java install download <version>");
            System.err.println("       capy java install from-file <path>");
            return 1;
        }

        return installVersion(version);
    }

    private Integer installVersion(String version) {
        System.out.println("Installing Java " + version + "...");

        if (version.contains("graalvm")) {
            return installGraalVM(version);
        } else {
            return installOpenJDK(version);
        }
    }

    private Integer installGraalVM(String version) {
        System.out.println("GraalVM Installation Guide:");
        System.out.println("1. Download GraalVM from: https://www.graalvm.org/downloads/");
        System.out.println("2. Choose the appropriate version for your platform:");
        System.out.println("   - Windows: graalvm-community-jdk-<version>_windows-x64_bin.zip");
        System.out.println("   - macOS: graalvm-community-jdk-<version>_macos-x64_bin.tar.gz");
        System.out.println("   - Linux: graalvm-community-jdk-<version>_linux-x64_bin.tar.gz");
        System.out.println("3. Extract to: " + JavaVersionManagerUtils.getJavaVersionsDirectory()
                .resolve("graalvm-" + version.replace("-graalvm", "")));
        System.out.println("4. Run: capy java use graalvm-" + version.replace("-graalvm", ""));
        return 0;
    }

    private Integer installOpenJDK(String version) {
        System.out.println("OpenJDK Installation Guide:");
        System.out.println("1. Download from Eclipse Adoptium: https://adoptium.net/");
        System.out.println("2. Choose version " + version + " for your platform");
        System.out.println("3. Extract to: " + JavaVersionManagerUtils.getJavaVersionsDirectory()
                .resolve("jdk-" + version));
        System.out.println("4. Run: capy java use jdk-" + version);
        System.out.println();
        System.out.println("Alternative - Use package managers:");
        System.out.println("Windows (winget): winget install Microsoft.OpenJDK." + version);
        System.out.println("macOS (brew): brew install openjdk@" + version);
        System.out.println("Linux (apt): sudo apt install openjdk-" + version + "-jdk");
        return 0;
    }

    // Subcommands for install

    @Command(name = "list", description = "List all available Java versions for installation")
    static class ListAvailableCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            String url = "https://raw.githubusercontent.com/amankrmj01/capyverse_cli/main/.github/java_versions/java_version.json";
            JavaVersionAvailableFetchService fetchService = new JavaVersionAvailableFetchService();
            try {
                List<OracleJavaVersionInfo> versions = fetchService.fetchOracleJavaVersions(url);
                System.out.println("\n📦 Available Java Versions:");
                for (OracleJavaVersionInfo v : versions) {
                    System.out.printf("  • Version: %-10s | Distribution: %-10s\n    Description: %s\n    URL: %s\n\n",
                            v.getVersion(), v.getDistribution(), v.getDescription(), v.getUrl());
                }
                return 0;
            } catch (Exception e) {
                System.err.println("❌ Failed to fetch Java versions: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "download", description = "Download and install a specific Java version")
    static class DownloadCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Java version to download and install", arity = "1")
        private String version;

        @Option(names = {"-d", "--directory"}, description = "Custom installation directory")
        private String customDirectory;

        @Override
        public Integer call() throws Exception {
            System.out.println("🔄 Downloading Java " + version + "...");

            if (customDirectory != null) {
                System.out.println("📁 Installing to custom directory: " + customDirectory);
            }

            // TODO: Implement actual download logic here
            System.out.println("✅ Download functionality will be implemented here");
            return 0;
        }
    }

    @Command(name = "from-file", description = "Install Java from a local archive file")
    static class FromFileCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Path to Java archive file (.zip, .tar.gz)", arity = "1")
        private String filePath;

        @Option(names = {"-n", "--name"}, description = "Custom name for this Java installation")
        private String customName;

        @Override
        public Integer call() throws Exception {
            System.out.println("📦 Installing Java from file: " + filePath);

            if (customName != null) {
                System.out.println("🏷️  Using custom name: " + customName);
            }

            // TODO: Implement file installation logic here
            System.out.println("✅ File installation functionality will be implemented here");
            return 0;
        }
    }
}
