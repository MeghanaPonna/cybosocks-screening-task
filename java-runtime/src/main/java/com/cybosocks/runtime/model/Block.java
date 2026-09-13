package com.cybosocks.runtime.model;

import java.util.Collections;
import java.util.List;

/**
 * Immutable representation of a single program block after validation.
 * Use the static factory methods to construct typed instances.
 */
public abstract class Block {

    public enum Type {
        MOVE, TURN, SAY, REPEAT
    }

    private final Type type;

    protected Block(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    // ---- Concrete subtypes ----

    public static final class Move extends Block {
        private final int steps;

        public Move(int steps) {
            super(Type.MOVE);
            this.steps = steps;
        }

        public int getSteps() {
            return steps;
        }
    }

    public static final class Turn extends Block {
        public enum Direction {
            LEFT, RIGHT
        }

        private final Direction direction;

        public Turn(Direction direction) {
            super(Type.TURN);
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public static final class Say extends Block {
        private final String text;

        public Say(String text) {
            super(Type.SAY);
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static final class Repeat extends Block {
        private final int times;
        private final List<Block> body;

        public Repeat(int times, List<Block> body) {
            super(Type.REPEAT);
            this.times = times;
            this.body = Collections.unmodifiableList(body);
        }

        public int getTimes() {
            return times;
        }

        public List<Block> getBody() {
            return body;
        }
    }
}
