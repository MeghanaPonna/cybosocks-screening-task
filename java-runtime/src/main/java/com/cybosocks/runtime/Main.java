package com.cybosocks.runtime;

import com.cybosocks.runtime.model.Program;
import com.cybosocks.runtime.parser.ProgramParseException;
import com.cybosocks.runtime.parser.ProgramParser;
import com.cybosocks.runtime.runtime.TurtleRuntime;

import java.nio.file.Path;

/**
 * Entry point for the Cybosocks turtle runtime.
 *
 * Usage:
 *   java -jar target/java-runtime.jar path/to/program.json
 */
public final class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error: usage: java -jar java-runtime.jar <program.json>");
            System.exit(1);
        }

        Path path = Path.of(args[0]);
        ProgramParser parser = new ProgramParser();

        try {
            Program program = parser.parseFile(path);
            TurtleRuntime runtime = new TurtleRuntime();
            runtime.execute(program);
        } catch (ProgramParseException e) {
            // Already a clean one-line message
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            // Unexpected internal error – still avoid full stack trace for end users
            System.err.println("Error: unexpected failure: " + e.getMessage());
            System.exit(1);
        }
    }
}
