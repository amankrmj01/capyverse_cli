package com.amankrmj.capyverse.java;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "java",
        description = "Manage Java versions and installations",
        subcommands = {
                ListJavaVersionsCommand.class,
                InstallJavaVersionCommand.class,
                UseJavaVersionCommand.class,
                CurrentJavaVersionCommand.class,
                WhichJavaVersionCommand.class,
                SetJavaVersion.class
        })
public class JavaVersionManagerCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Java Version Manager - Use 'java --help' for options:");
        System.out.println("  list     - List installed Java versions");
        System.out.println("  install  - Install a specific Java version");
        System.out.println("  use      - Switch to a specific Java version");
        System.out.println("  current  - Show current active Java version");
        System.out.println("  which    - Show path to current Java installation");
        return 0;
    }
}
