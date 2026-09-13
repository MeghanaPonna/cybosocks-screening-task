package com.cybosocks.runtime.runtime;

/**
 * Simple turtle: position (x, y) and facing direction.
 * Coordinate system:
 *   North = +y, East = +x, South = -y, West = -x
 */
public final class Turtle {

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    private int x;
    private int y;
    private Direction facing;

    public Turtle() {
        this.x = 0;
        this.y = 0;
        this.facing = Direction.NORTH;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getFacing() {
        return facing;
    }

    public void move(int steps) {
        switch (facing) {
            case NORTH -> y += steps;
            case EAST -> x += steps;
            case SOUTH -> y -= steps;
            case WEST -> x -= steps;
        }
    }

    public void turnRight() {
        facing = switch (facing) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
        };
    }

    public void turnLeft() {
        facing = switch (facing) {
            case NORTH -> Direction.WEST;
            case WEST -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
        };
    }

    @Override
    public String toString() {
        return "Final position: (" + x + ", " + y + "), facing " + facing;
    }
}
