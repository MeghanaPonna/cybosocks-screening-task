package com.cybosocks.runtime.parser;

import com.cybosocks.runtime.model.Block;
import com.cybosocks.runtime.model.Program;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses and validates a Cybosocks program JSON file into a {@link Program}.
 * Explicit validation is preferred over relying solely on Jackson exceptions.
 */
public final class ProgramParser {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Read and parse a program from the given file path.
     *
     * @throws ProgramParseException on any validation or I/O failure
     */
    public Program parseFile(Path path) {
        if (path == null) {
            throw new ProgramParseException("Error: input file path is null");
        }
        if (!Files.exists(path)) {
            throw new ProgramParseException("Error: input file not found: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new ProgramParseException("Error: path is not a regular file: " + path);
        }

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new ProgramParseException("Error: cannot read file: " + path);
        }

        return parseJson(content);
    }

    /**
     * Parse a JSON string into a validated Program.
     */
    public Program parseJson(String json) {
        if (json == null || json.isBlank()) {
            throw new ProgramParseException("Error: empty or missing JSON input");
        }

        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (IOException e) {
            throw new ProgramParseException("Error: malformed JSON");
        }

        if (root == null || !root.isObject()) {
            throw new ProgramParseException("Error: root must be a JSON object");
        }

        if (!root.has("blocks")) {
            throw new ProgramParseException("Error: missing \"blocks\"");
        }

        JsonNode blocksNode = root.get("blocks");
        if (blocksNode == null || blocksNode.isNull()) {
            throw new ProgramParseException("Error: \"blocks\" must not be null");
        }
        if (!blocksNode.isArray()) {
            throw new ProgramParseException("Error: \"blocks\" is not an array");
        }

        List<Block> blocks = parseBlockList(blocksNode, "blocks");
        return new Program(blocks);
    }

    private List<Block> parseBlockList(JsonNode arrayNode, String context) {
        List<Block> result = new ArrayList<>();
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode node = arrayNode.get(i);
            if (node == null || node.isNull()) {
                throw new ProgramParseException(
                        "Error: null block at index " + i + " in " + context);
            }
            result.add(parseBlock(node, context + "[" + i + "]"));
        }
        return result;
    }

    private Block parseBlock(JsonNode node, String context) {
        if (!node.isObject()) {
            throw new ProgramParseException("Error: block must be an object at " + context);
        }

        if (!node.has("type") || node.get("type").isNull()) {
            throw new ProgramParseException("Error: block is missing type at " + context);
        }

        String type = node.get("type").asText();
        if (type == null || type.isBlank()) {
            throw new ProgramParseException("Error: block type is empty at " + context);
        }

        return switch (type) {
            case "move" -> parseMove(node, context);
            case "turn" -> parseTurn(node, context);
            case "say" -> parseSay(node, context);
            case "repeat" -> parseRepeat(node, context);
            default -> throw new ProgramParseException("Error: unknown block type: " + type);
        };
    }

    private Block.Move parseMove(JsonNode node, String context) {
        if (!node.has("steps") || node.get("steps").isNull()) {
            throw new ProgramParseException("Error: move block is missing steps");
        }
        JsonNode stepsNode = node.get("steps");
        if (!stepsNode.isNumber()) {
            throw new ProgramParseException("Error: move steps must be a number");
        }
        // Reject non-integers and negative values
        double d = stepsNode.asDouble();
        if (d != Math.floor(d) || Double.isInfinite(d) || Double.isNaN(d)) {
            throw new ProgramParseException("Error: move steps must be an integer");
        }
        int steps = stepsNode.asInt();
        if (steps < 0) {
            throw new ProgramParseException("Error: move steps cannot be negative");
        }
        return new Block.Move(steps);
    }

    private Block.Turn parseTurn(JsonNode node, String context) {
        if (!node.has("direction") || node.get("direction").isNull()) {
            throw new ProgramParseException("Error: turn block is missing direction");
        }
        String dir = node.get("direction").asText();
        if ("left".equalsIgnoreCase(dir)) {
            return new Block.Turn(Block.Turn.Direction.LEFT);
        }
        if ("right".equalsIgnoreCase(dir)) {
            return new Block.Turn(Block.Turn.Direction.RIGHT);
        }
        throw new ProgramParseException("Error: turn direction must be left or right");
    }

    private Block.Say parseSay(JsonNode node, String context) {
        if (!node.has("text") || node.get("text").isNull()) {
            throw new ProgramParseException("Error: say block is missing text");
        }
        // Accept any string (including empty)
        String text = node.get("text").asText();
        return new Block.Say(text);
    }

    private Block.Repeat parseRepeat(JsonNode node, String context) {
        if (!node.has("times") || node.get("times").isNull()) {
            throw new ProgramParseException("Error: repeat block is missing times");
        }
        JsonNode timesNode = node.get("times");
        if (!timesNode.isNumber()) {
            throw new ProgramParseException("Error: repeat times must be a number");
        }
        double d = timesNode.asDouble();
        if (d != Math.floor(d) || Double.isInfinite(d) || Double.isNaN(d)) {
            throw new ProgramParseException("Error: repeat times must be an integer");
        }
        int times = timesNode.asInt();
        if (times < 0) {
            throw new ProgramParseException("Error: repeat times cannot be negative");
        }

        if (!node.has("body") || node.get("body").isNull()) {
            throw new ProgramParseException("Error: repeat block is missing body");
        }
        JsonNode bodyNode = node.get("body");
        if (!bodyNode.isArray()) {
            throw new ProgramParseException("Error: repeat body must be an array");
        }

        List<Block> body = parseBlockList(bodyNode, context + ".body");
        return new Block.Repeat(times, body);
    }
}
