package com.amankrmj.capyverse;

import com.amankrmj.capyverse.java.JavaVersionManagerCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "capy",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CapyVerse - Complete Java Development Environment Manager",
        subcommands = {
                JavaVersionManagerCommand.class,
        })
public class Main implements Runnable {

    @Option(names = {"-m", "--mascot"}, description = "Show CapyVerse mascot")
    private boolean showMascot = false;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Main());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
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
