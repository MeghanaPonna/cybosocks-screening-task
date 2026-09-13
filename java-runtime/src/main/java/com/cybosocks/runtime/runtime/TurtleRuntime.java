package com.cybosocks.runtime.runtime;

import com.cybosocks.runtime.model.Block;
import com.cybosocks.runtime.model.Program;

import java.io.PrintStream;
import java.util.List;

/**
 * Executes a validated {@link Program} against a {@link Turtle}.
 * Repeat is handled recursively so arbitrary nesting is supported
 * within normal JVM stack limits.
 */
public final class TurtleRuntime {

    private final Turtle turtle;
    private final PrintStream out;

    public TurtleRuntime() {
        this(new Turtle(), System.out);
    }

    public TurtleRuntime(Turtle turtle, PrintStream out) {
        this.turtle = turtle;
        this.out = out;
    }

    public Turtle getTurtle() {
        return turtle;
    }

    /**
     * Run the entire program, then print the final turtle state.
     */
    public void execute(Program program) {
        executeBlocks(program.getBlocks());
        out.println(turtle.toString());
    }

    private void executeBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            executeBlock(block);
        }
    }

    private void executeBlock(Block block) {
        switch (block.getType()) {
            case MOVE -> {
                Block.Move move = (Block.Move) block;
                turtle.move(move.getSteps());
            }
            case TURN -> {
                Block.Turn turn = (Block.Turn) block;
                if (turn.getDirection() == Block.Turn.Direction.RIGHT) {
                    turtle.turnRight();
                } else {
                    turtle.turnLeft();
                }
            }
            case SAY -> {
                Block.Say say = (Block.Say) block;
                out.println(say.getText());
            }
            case REPEAT -> {
                Block.Repeat repeat = (Block.Repeat) block;
                // times == 0 → body never runs (by design)
                for (int i = 0; i < repeat.getTimes(); i++) {
                    executeBlocks(repeat.getBody());
                }
            }
        }
    }
}
