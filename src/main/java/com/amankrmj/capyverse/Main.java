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
                "                      .---.                           .---..\n" +
                        "                    ...  .-..   ............        ...    ...\n" +
                        "                    .-    ----...................  .-.      .-\n" +
                        "                       .--.                     .--..   ..  .-\n" +
                        "                     .-.                              .--.  ..\n" +
                        "                  ....                                 .  ...\n" +
                        "                ...                                     .--.\n" +
                        "               -.                                       ..-.\n" +
                        "             .-                          .---            .--.\n" +
                        "           ...  ..........              -++.--             -.\n" +
                        "          .-   .----------..            ------             ...\n" +
                        "         -.    ------------.             .--.               .-\n" +
                        "        ...    ------------.                                .-\n" +
                        "       .-       ...---....                                  .-.\n" +
                        "       .-          ---                                      .--.\n" +
                        "       .-.         ---                                      .--.\n" +
                        "       .-          ---                                      .--.\n" +
                        "       .-.         ---                                      .--.\n" +
                        "         ..        ----.                                    .--.\n" +
                        "         -.     .--.   .--                                  .--.\n" +
                        "          .-                                                .--.\n" +
                        "           ...                   ..                         .--.\n" +
                        "             .--.              .-.                           .-.\n" +
                        "              .----------------.                             .-..\n" +
                        "          ...--    .........                                  .--\n" +
                        "         ....--                                                .-\n" +
                        "       .-.   --                                                .--.\n" +
                        "     ...    -.                                    .-            .-.\n" +
                        "   ...    ...                                    ...             ...\n" +
                        "   .-    .--                                    .-.       .       --\n" +
                        "   .-  .----                                     -.      .-.      .--.\n" +
                        "   ...----.                                      -.     ...         -.\n" +
                        "     ....-.                                      --.    --          -.\n" +
                        "         -.                                       .- .--.           --.\n" +
                        "         -.                                       .---.              -.\n" +
                        "         -.                                                          ..\n" +
                        "         -.                                                          -.\n" +
                        "         --                                                          ..\n" +
                        "         .--                                                        .-.\n" +
                        "          .-.                                                       --.\n" +
                        "           .-.                                                    .--.\n" +
                        "             .-                                                   --\n" +
                        "             .--.                                               .--.\n" +
                        "               --....                                          ...\n" +
                        "               -. ...--.                                      ...\n" +
                        "               -.      .------------------------..           .-.\n" +
                        "              ...         -.                     -..         ...\n" +
                        "             .-          ...                     .--        .-\n" +
                        "  ---------------------------------------------------------------------.                         \n"
        );
        System.out.println("CapyVerse - Complete Development Environment Manager");
        System.out.println("Use --help for options or try these commands:");
        System.out.println("  path     - Manage environment PATH variables");
        System.out.println("  java     - Manage Java versions and installations");
        System.out.println("  native   - Manage native compilation and cross-platform builds");
    }
}
