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

