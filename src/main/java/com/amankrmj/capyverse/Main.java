package com.amankrmj.capyverse;

import com.amankrmj.capyverse.commands.PathCommand;
import com.amankrmj.capyverse.java.JavaVersionManagerCommand;
import com.amankrmj.capyverse.java.NativeCompilerCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "capy",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CapyVerse - Complete Java Development Environment Manager",
        subcommands = {
                PathCommand.class,
                JavaVersionManagerCommand.class,
                NativeCompilerCommand.class
        })
public class Main implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output")
    private boolean verbose = false;

    @Option(names = {"-m", "--mascot"}, description = "Show CapyVerse mascot")
    private boolean showMascot = false;

    @Option(names = {"-a", "--available"}, description = "Show available commands")
    private boolean availableCommands = false;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Main());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public void run() {
        System.out.println(
                """
                         _   _    ____                   __     __                   _   _\s
                        | | | |  / ___|__ _ _ __  _   _  \\ \\   / /__ _ __ ___  ___  | | | |
                        | | | | | |   / _` | '_ \\| | | |  \\ \\ / / _ \\ '__/ __|/ _ \\ | | | |
                        |_| |_| | |__| (_| | |_) | |_| |   \\ V /  __/ |  \\__ \\  __/ |_| |_|
                        (_) (_)  \\____\\__,_| .__/ \\__, |    \\_/ \\___|_|  |___/\\___| (_) (_)
                                           |_|    |___/                                   \s"""
        );
        System.out.println();
        if (showMascot) {
            capyMascot();
        }
        System.out.println("CapyVerse - Complete Development Environment Manager");
        System.out.println("Use --help for options or try these commands:");
        System.out.println("  java     - Manage Java versions and installations");

    }

    private void capyMascot() {
        System.out.println("""
                                                          .' '.                 -' '-                        \s
                                                         .-   ---..........-.  -     -                       \s
                                                          '..'               '.'..- -                       \s
                                                        .-'                     '. ..                        \s
                                                      .-                          --                         \s
                                                    .-                  --.        --                        \s
                                                   -   .-----.         -+--         -                        \s
                                                  -.  --------.        .--.          -                       \s
                                                 ..   .-------.         ''           -                       \s
                                                 -     ' --- '                       --                      \s
                                                 -       --                          --                      \s
                                                 -       -.                          --                      \s
                                                  -      --                          --                      \s
                                                  ..   .'  '.                        --                      \s
                                                   ..               .'               --                      \s
                                                     .'.         .-'                 --                      \s
                                                    .-  '-.....-'                     --                     \s
                                                  - .-                                 --                    \s
                                                -   -                         -         -                    \s
                                              -    -                         -           -                   \s
                                             -    --                         -     -     -.                  \s
                                             '..- -                          -    .       -                  \s
                                                  -                          '.  .        -.                 \s
                                                  -                            ''          -                 \s
                                                  -                                        -                 \s
                                                  -                                        -                 \s
                                                  .-                                      .-                 \s
                                                   .-                                    .-                  \s
                                                    .-.                                 .-                   \s
                                                    --                                  -                    \s
                                                     -   .--    ------------           -                     \s
                                                      -       .'            '.        -                      \s
                                                       '-   -'                '-    -'                       \s
                                             ................................................\
                """);
    }
}
