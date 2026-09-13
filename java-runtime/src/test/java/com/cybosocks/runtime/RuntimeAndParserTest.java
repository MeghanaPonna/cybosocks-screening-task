package com.cybosocks.runtime;

import com.cybosocks.runtime.model.Program;
import com.cybosocks.runtime.parser.ProgramParseException;
import com.cybosocks.runtime.parser.ProgramParser;
import com.cybosocks.runtime.runtime.Turtle;
import com.cybosocks.runtime.runtime.TurtleRuntime;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeAndParserTest {

    private final ProgramParser parser = new ProgramParser();

    private String runAndCapture(String json) {
        Program program = parser.parseJson(json);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        TurtleRuntime runtime = new TurtleRuntime(new Turtle(), ps);
        runtime.execute(program);
        return baos.toString(StandardCharsets.UTF_8).trim();
    }

    @Test
    void sayOutput() {
        String out = runAndCapture("""
                {"blocks":[{"type":"say","text":"Hello"}]}
                """);
        assertTrue(out.startsWith("Hello"));
        assertTrue(out.contains("Final position: (0, 0), facing NORTH"));
    }

    @Test
    void simpleRepeat() {
        String out = runAndCapture("""
                {
                  "blocks": [
                    {
                      "type": "repeat",
                      "times": 3,
                      "body": [
                        {"type": "say", "text": "X"}
                      ]
                    }
                  ]
                }
                """);
        String[] lines = out.split("\\R");
        assertEquals("X", lines[0]);
        assertEquals("X", lines[1]);
        assertEquals("X", lines[2]);
        assertTrue(lines[3].startsWith("Final position"));
    }

    @Test
    void nestedRepeat() {
        String out = runAndCapture("""
                {
                  "blocks": [
                    {
                      "type": "repeat",
                      "times": 2,
                      "body": [
                        {"type": "say", "text": "A"},
                        {
                          "type": "repeat",
                          "times": 3,
                          "body": [
                            {"type": "say", "text": "B"}
                          ]
                        }
                      ]
                    }
                  ]
                }
                """);
        String[] lines = out.split("\\R");
        // A B B B A B B B + final
        assertEquals(9, lines.length);
        assertEquals("A", lines[0]);
        assertEquals("B", lines[1]);
        assertEquals("B", lines[2]);
        assertEquals("B", lines[3]);
        assertEquals("A", lines[4]);
        assertEquals("B", lines[5]);
        assertEquals("B", lines[6]);
        assertEquals("B", lines[7]);
        assertTrue(lines[8].startsWith("Final position"));
    }

    @Test
    void mixedNestedProgram() {
        // Same structure as examples/nested-program.json
        String out = runAndCapture("""
                {
                  "blocks": [
                    {
                      "type": "repeat",
                      "times": 2,
                      "body": [
                        {"type": "move", "steps": 1},
                        {
                          "type": "repeat",
                          "times": 2,
                          "body": [
                            {"type": "turn", "direction": "right"},
                            {"type": "move", "steps": 2}
                          ]
                        },
                        {"type": "say", "text": "done"}
                      ]
                    }
                  ]
                }
                """);
        String[] lines = out.split("\\R");
        assertEquals("done", lines[0]);
        assertEquals("done", lines[1]);
        // Trace:
        // start (0,0) N
        // move 1 → (0,1) N
        // turn R → E, move 2 → (2,1) E
        // turn R → S, move 2 → (2,-1) S
        // say done
        // move 1 → (2,-2) S
        // turn R → W, move 2 → (0,-2) W
        // turn R → N, move 2 → (0,0) N
        // say done
        assertTrue(lines[2].contains("Final position: (0, 0), facing NORTH"));
    }

    @Test
    void emptyProgram() {
        String out = runAndCapture("{\"blocks\":[]}");
        assertEquals("Final position: (0, 0), facing NORTH", out);
    }

    @Test
    void repeatZero() {
        String out = runAndCapture("""
                {
                  "blocks": [
                    {
                      "type": "repeat",
                      "times": 0,
                      "body": [
                        {"type": "say", "text": "never"}
                      ]
                    }
                  ]
                }
                """);
        assertFalse(out.contains("never"));
        assertTrue(out.contains("Final position: (0, 0), facing NORTH"));
    }

    @Test
    void emptyRepeatBody() {
        String out = runAndCapture("""
                {
                  "blocks": [
                    {
                      "type": "repeat",
                      "times": 5,
                      "body": []
                    }
                  ]
                }
                """);
        assertEquals("Final position: (0, 0), facing NORTH", out);
    }

    @Test
    void specialCharactersInSay() {
        String out = runAndCapture("""
                {"blocks":[{"type":"say","text":"Hello \\"world\\""}]}
                """);
        assertTrue(out.startsWith("Hello \"world\""));
    }

    // ---- Validation / error handling ----

    @Test
    void negativeRepeatRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("""
                        {"blocks":[{"type":"repeat","times":-1,"body":[]}]}
                        """));
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }

    @Test
    void unknownBlockRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("""
                        {"blocks":[{"type":"dance"}]}
                        """));
        assertTrue(ex.getMessage().contains("unknown block type: dance"));
    }

    @Test
    void invalidTurnRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("""
                        {"blocks":[{"type":"turn","direction":"up"}]}
                        """));
        assertTrue(ex.getMessage().contains("left or right"));
    }

    @Test
    void malformedJsonRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("{not json"));
        assertTrue(ex.getMessage().contains("malformed JSON"));
    }

    @Test
    void missingBlocksRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("{}"));
        assertTrue(ex.getMessage().contains("missing \"blocks\""));
    }

    @Test
    void missingStepsRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("""
                        {"blocks":[{"type":"move"}]}
                        """));
        assertTrue(ex.getMessage().contains("missing steps"));
    }

    @Test
    void blocksNotArrayRejected() {
        ProgramParseException ex = assertThrows(ProgramParseException.class, () ->
                parser.parseJson("{\"blocks\":{}}"));
        assertTrue(ex.getMessage().contains("not an array"));
    }
}
