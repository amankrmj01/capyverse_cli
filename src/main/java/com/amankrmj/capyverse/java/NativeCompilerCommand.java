package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "native",
        description = "Manage native compilation tools and create platform-specific executables",
        subcommands = {
                NativeCompilerCommand.InfoCommand.class,
                NativeCompilerCommand.InstallCommand.class,
                NativeCompilerCommand.BuildCommand.class,
                NativeCompilerCommand.PackageCommand.class
        })
public class NativeCompilerCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("Native Compilation Manager - Create standalone executables");
        System.out.println("Available commands:");
        System.out.println("  info     - Show native compilation options for current platform");
        System.out.println("  install  - Install native compilation tools");
        System.out.println("  build    - Build native executable");
        System.out.println("  package  - Create platform-specific installers");
        return 0;
    }

    @Command(name = "info", description = "Show native compilation options for current platform")
    static class InfoCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch");

            System.out.println("=== Native Compilation Options ===");
            System.out.println("Platform: " + os + " (" + arch + ")");
            System.out.println();

            if (os.contains("windows")) {
                showWindowsOptions();
            } else if (os.contains("mac")) {
                showMacOptions();
            } else if (os.contains("linux")) {
                showLinuxOptions();
            } else {
                System.out.println("Unsupported platform: " + os);
                return 1;
            }

            return 0;
        }

        private void showWindowsOptions() {
            System.out.println("🪟 WINDOWS NATIVE COMPILATION OPTIONS:");
            System.out.println();

            System.out.println("1. GraalVM Native Image (Recommended)");
            System.out.println("   ✅ Creates true native executables (.exe)");
            System.out.println("   ✅ No JVM required at runtime");
            System.out.println("   ✅ Fast startup, low memory usage");
            System.out.println("   📦 Install: Download from graalvm.org");
            System.out.println("   🛠️  Requires: Visual Studio Build Tools");
            System.out.println();

            System.out.println("2. jpackage (Java 14+)");
            System.out.println("   ✅ Built into OpenJDK");
            System.out.println("   ⚠️  Bundles JVM (larger size)");
            System.out.println("   📦 Creates: .exe installer with bundled JRE");
            System.out.println("   🛠️  Command: jpackage --input ... --main-jar ...");
            System.out.println();

            System.out.println("3. Launch4j + Inno Setup");
            System.out.println("   ⚠️  Requires JVM on target machine");
            System.out.println("   📦 Creates: .exe wrapper + installer");
            System.out.println("   💾 Smaller download, JVM dependency");
            System.out.println();

            System.out.println("4. Native Image with Substrate VM");
            System.out.println("   🧪 Experimental alternative to GraalVM");
            System.out.println("   📦 Similar to GraalVM but different toolchain");
        }

        private void showMacOptions() {
            System.out.println("🍎 macOS NATIVE COMPILATION OPTIONS:");
            System.out.println();

            System.out.println("1. GraalVM Native Image (Recommended)");
            System.out.println("   ✅ Creates native Mach-O executables");
            System.out.println("   ✅ No JVM required at runtime");
            System.out.println("   📦 Install: brew install --cask graalvm/tap/graalvm-jdk<version>");
            System.out.println("   🛠️  Requires: Xcode Command Line Tools");
            System.out.println("   📱 Supports: Intel & Apple Silicon (M1/M2)");
            System.out.println();

            System.out.println("2. jpackage (Java 14+)");
            System.out.println("   ✅ Built into OpenJDK");
            System.out.println("   📦 Creates: .app bundles or .dmg installers");
            System.out.println("   ⚠️  Bundles JVM (larger size)");
            System.out.println("   🛠️  Command: jpackage --type app-image --input ...");
            System.out.println();

            System.out.println("3. Quarkus Native (for microservices)");
            System.out.println("   🚀 Fast startup, low memory");
            System.out.println("   📦 Uses GraalVM under the hood");
            System.out.println("   🔧 Framework-specific optimization");
            System.out.println();

            System.out.println("4. AppBundler + DMG Canvas");
            System.out.println("   📦 Traditional .app bundle creation");
            System.out.println("   ⚠️  Requires JVM on target machine");
            System.out.println();

            System.out.println("📋 Installation Steps:");
            System.out.println("   xcode-select --install");
            System.out.println("   brew install graalvm/tap/graalvm-jdk21");
            System.out.println("   export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-jdk-21/Contents/Home");
        }

        private void showLinuxOptions() {
            System.out.println("🐧 LINUX NATIVE COMPILATION OPTIONS:");
            System.out.println();

            System.out.println("1. GraalVM Native Image (Recommended)");
            System.out.println("   ✅ Creates native ELF executables");
            System.out.println("   ✅ No JVM required at runtime");
            System.out.println("   📦 Install: sdk install java 21.0.1-graal (using SDKMAN)");
            System.out.println("   🛠️  Requires: gcc, glibc-devel, zlib-devel");
            System.out.println();

            System.out.println("2. jpackage (Java 14+)");
            System.out.println("   ✅ Built into OpenJDK");
            System.out.println("   📦 Creates: .deb, .rpm packages or AppImage");
            System.out.println("   ⚠️  Bundles JVM (larger size)");
            System.out.println("   🛠️  Command: jpackage --type deb --input ...");
            System.out.println();

            System.out.println("3. Quarkus Native");
            System.out.println("   🚀 Optimized for cloud/containers");
            System.out.println("   📦 Excellent for microservices");
            System.out.println("   🐳 Great Docker support");
            System.out.println();

            System.out.println("4. Substrate VM (experimental)");
            System.out.println("   🧪 Alternative to GraalVM");
            System.out.println("   📦 Research-stage tool");
            System.out.println();

            System.out.println("5. AppImage Creation");
            System.out.println("   📦 Portable Linux apps");
            System.out.println("   ✅ Run anywhere on Linux");
            System.out.println("   🛠️  Tool: appimagetool");
            System.out.println();

            System.out.println("📋 Installation Steps (Ubuntu/Debian):");
            System.out.println("   sudo apt update");
            System.out.println("   sudo apt install build-essential zlib1g-dev");
            System.out.println("   curl -s 'https://get.sdkman.io' | bash");
            System.out.println("   sdk install java 21.0.1-graal");
        }
    }

    @Command(name = "install", description = "Install native compilation tools for current platform")
    static class InstallCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Tool to install: graalvm, build-tools, jpackage")
        private String tool;

        @Override
        public Integer call() throws Exception {
            String os = System.getProperty("os.name").toLowerCase();

            switch (tool.toLowerCase()) {
                case "graalvm":
                    return installGraalVM(os);
                case "build-tools":
                    return installBuildTools(os);
                case "jpackage":
                    return installJPackage(os);
                default:
                    System.err.println("Unknown tool: " + tool);
                    System.err.println("Available tools: graalvm, build-tools, jpackage");
                    return 1;
            }
        }

        private Integer installGraalVM(String os) {
            System.out.println("Installing GraalVM for " + os + "...");

            if (os.contains("windows")) {
                System.out.println("Windows GraalVM Installation:");
                System.out.println("1. Download from: https://github.com/graalvm/graalvm-ce-builds/releases");
                System.out.println("2. Or use Chocolatey: choco install graalvm");
                System.out.println("3. Set JAVA_HOME and add to PATH");
            } else if (os.contains("mac")) {
                System.out.println("macOS GraalVM Installation:");
                System.out.println("brew install --cask graalvm/tap/graalvm-jdk21");
                System.out.println("export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-jdk-21/Contents/Home");
            } else if (os.contains("linux")) {
                System.out.println("Linux GraalVM Installation:");
                System.out.println("curl -s 'https://get.sdkman.io' | bash");
                System.out.println("source ~/.sdkman/bin/sdkman-init.sh");
                System.out.println("sdk install java 21.0.1-graal");
                System.out.println("sdk use java 21.0.1-graal");
            }

            return 0;
        }

        private Integer installBuildTools(String os) {
            System.out.println("Installing build tools for " + os + "...");

            if (os.contains("windows")) {
                System.out.println("Windows Build Tools:");
                System.out.println("Download Visual Studio Build Tools 2022");
                System.out.println("Or Visual Studio Community with C++ workload");
            } else if (os.contains("mac")) {
                System.out.println("macOS Build Tools:");
                System.out.println("xcode-select --install");
            } else if (os.contains("linux")) {
                System.out.println("Linux Build Tools:");
                System.out.println("Ubuntu/Debian: sudo apt install build-essential zlib1g-dev");
                System.out.println("RHEL/CentOS: sudo yum groupinstall 'Development Tools' && sudo yum install zlib-devel");
                System.out.println("Arch: sudo pacman -S base-devel zlib");
            }

            return 0;
        }

        private Integer installJPackage(String os) {
            System.out.println("jpackage is included with OpenJDK 14+");
            System.out.println("Current Java version:");

            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-version");
                Process process = pb.start();
                String output = new String(process.getErrorStream().readAllBytes());
                System.out.println(output);

                pb = new ProcessBuilder("jpackage", "--help");
                process = pb.start();
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("✅ jpackage is available!");
                } else {
                    System.out.println("❌ jpackage not found. Update to Java 14+ or install a JDK that includes jpackage.");
                }
            } catch (Exception e) {
                System.err.println("Error checking jpackage: " + e.getMessage());
            }

            return 0;
        }
    }

    @Command(name = "build", description = "Build native executable using available tools")
    static class BuildCommand implements Callable<Integer> {

        @Option(names = {"-t", "--tool"}, description = "Tool to use: graalvm, jpackage")
        private String tool = "graalvm";

        @Option(names = {"-o", "--output"}, description = "Output file name")
        private String outputName;

        @Override
        public Integer call() throws Exception {
            if ("graalvm".equals(tool)) {
                return buildWithGraalVM();
            } else if ("jpackage".equals(tool)) {
                return buildWithJPackage();
            } else {
                System.err.println("Unknown tool: " + tool);
                return 1;
            }
        }

        private Integer buildWithGraalVM() {
            System.out.println("Building with GraalVM Native Image...");
            System.out.println("Use your existing Gradle configuration:");
            System.out.println("./gradlew nativeCompile");
            return 0;
        }

        private Integer buildWithJPackage() {
            System.out.println("Building with jpackage...");
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("windows")) {
                System.out.println("jpackage --input build/libs --main-jar capyverse-1.0.0.jar --main-class com.amankrmj.capyverse.Main --type exe --name capyverse");
            } else if (os.contains("mac")) {
                System.out.println("jpackage --input build/libs --main-jar capyverse-1.0.0.jar --main-class com.amankrmj.capyverse.Main --type dmg --name capyverse");
            } else if (os.contains("linux")) {
                System.out.println("jpackage --input build/libs --main-jar capyverse-1.0.0.jar --main-class com.amankrmj.capyverse.Main --type deb --name capyverse");
            }

            return 0;
        }
    }

    @Command(name = "package", description = "Create platform-specific installers")
    static class PackageCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            String os = System.getProperty("os.name").toLowerCase();
            System.out.println("Creating installer for " + os + "...");

            if (os.contains("windows")) {
                System.out.println("Windows installer options:");
                System.out.println("1. Inno Setup (current) - Creates .exe installer");
                System.out.println("2. WiX Toolset - Creates .msi installer");
                System.out.println("3. jpackage - Creates .exe with bundled JRE");
            } else if (os.contains("mac")) {
                System.out.println("macOS installer options:");
                System.out.println("1. jpackage --type dmg - Creates .dmg");
                System.out.println("2. jpackage --type pkg - Creates .pkg");
                System.out.println("3. create-dmg - Custom DMG creation");
                System.out.println("4. pkgbuild - Native macOS package creation");
            } else if (os.contains("linux")) {
                System.out.println("Linux installer options:");
                System.out.println("1. jpackage --type deb - Creates .deb package");
                System.out.println("2. jpackage --type rpm - Creates .rpm package");
                System.out.println("3. AppImage - Portable application");
                System.out.println("4. Flatpak - Universal Linux package");
                System.out.println("5. Snap - Ubuntu/cross-distro package");
            }

            return 0;
        }
    }
}
