# Cybosocks Screening Task

## Overview

This repository contains two independently runnable parts:

1. **React Blockly editor** (`react-editor/`) – a child-friendly block programming UI that produces a simple JSON program.
2. **Java turtle runtime** (`java-runtime/`) – a console application that consumes the same JSON and executes the program with a turtle.

The editor generates JSON; the runtime executes that JSON. No other intermediate format is required.

## Project Structure

```
cybosocks-screening-task/
├── README.md
├── react-editor/          # Vite + React + Blockly
│   ├── src/
│   │   ├── blocks/        # Custom block definitions
│   │   ├── components/    # Workspace & JSON panel
│   │   ├── generator/     # Custom recursive JSON generator
│   │   ├── App.jsx
│   │   └── ...
│   └── package.json
└── java-runtime/          # Maven console app
    ├── pom.xml
    ├── examples/
    │   └── nested-program.json
    └── src/
        ├── main/java/com/cybosocks/runtime/
        │   ├── Main.java
        │   ├── model/
        │   ├── parser/
        │   └── runtime/
        └── test/java/...
```

## Requirements

- **Node.js** 18+ and npm
- **Java** 17+ (tested with OpenJDK 17)
- **Maven** 3.8+

## Run React

```bash
cd react-editor
npm install
npm run dev
```

Open the URL shown by Vite (typically http://localhost:5173).

Production build:

```bash
npm run build
npm run preview
```

## Run Java

```bash
cd java-runtime
mvn clean package
java -jar target/java-runtime.jar examples/nested-program.json
```

Run tests:

```bash
mvn test
```

## JSON Format

Top-level:

```json
{
  "blocks": [ ... ]
}
```

Supported block types:

| type   | fields                                      |
|--------|---------------------------------------------|
| move   | `steps` (non-negative integer)              |
| turn   | `direction` (`"left"` or `"right"`)         |
| say    | `text` (string)                             |
| repeat | `times` (non-negative integer), `body` (array of blocks) |

Nested example:

```json
{
  "blocks": [
    {
      "type": "repeat",
      "times": 2,
      "body": [
        { "type": "move", "steps": 1 },
        {
          "type": "repeat",
          "times": 2,
          "body": [
            { "type": "turn", "direction": "right" },
            { "type": "move", "steps": 2 }
          ]
        },
        { "type": "say", "text": "done" }
      ]
    }
  ]
}
```

## Architecture

- **Custom Blockly blocks** (`cybosocks_move`, `cybosocks_turn`, `cybosocks_say`, `cybosocks_repeat`) – fully custom; the repeat block does not use Blockly’s built-in loop.
- **Custom recursive JSON generator** – walks the workspace and statement inputs, emitting only the required schema (no Blockly IDs or coordinates).
- **Java parser** – Jackson + explicit validation; produces an immutable model.
- **Turtle runtime** – recursive execution of repeat bodies; prints `say` text immediately and the final position/direction at the end.

## Validation / Error Handling

Invalid input produces a single clear error line on stderr and a non-zero exit code. No stack traces are shown for ordinary user errors (missing file, malformed JSON, unknown block type, negative repeat, missing fields, invalid turn direction, etc.).

## AI Usage

AI assistance was used for:

- Blockly API exploration and scaffolding of the React workspace lifecycle
- Implementation assistance for the recursive JSON generator and Java parser/runtime
- Debugging and review of nested-repeat behaviour
- Generation of focused unit-test cases

### Actual AI mistake observed

While implementing the custom JSON generator, the first version correctly handled top-level blocks but only partially walked statement inputs. Nested `repeat` bodies were sometimes omitted from the generated JSON. This was discovered when testing a `repeat` inside another `repeat`; the outer body appeared but the inner body was empty. The generator was fixed to recursively follow `getInput("BODY").connection.targetBlock()` and then `getNextBlock()` for every statement stack.

### One decision I'm unsure about

**Recursive execution vs iterative stack for `repeat`.**  
The runtime uses straightforward recursion. This is clear and matches the natural nesting of the language, and it works for any practical depth. A pathological program with extremely deep nesting could overflow the JVM stack. I did not add an artificial depth limit. With another five hours I would add a configurable safety depth (or an iterative interpreter) and a clearer error message when that limit is hit, plus a few more UI polish items (keyboard shortcuts, better mobile layout).
