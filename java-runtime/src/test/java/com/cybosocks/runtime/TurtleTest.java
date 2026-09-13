package com.cybosocks.runtime;

import com.cybosocks.runtime.runtime.Turtle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurtleTest {

    @Test
    void initialState() {
        Turtle t = new Turtle();
        assertEquals(0, t.getX());
        assertEquals(0, t.getY());
        assertEquals(Turtle.Direction.NORTH, t.getFacing());
    }

    @Test
    void moveNorth() {
        Turtle t = new Turtle();
        t.move(3);
        assertEquals(0, t.getX());
        assertEquals(3, t.getY());
        assertEquals(Turtle.Direction.NORTH, t.getFacing());
    }

    @Test
    void turnRight() {
        Turtle t = new Turtle();
        t.turnRight();
        assertEquals(Turtle.Direction.EAST, t.getFacing());
        t.turnRight();
        assertEquals(Turtle.Direction.SOUTH, t.getFacing());
        t.turnRight();
        assertEquals(Turtle.Direction.WEST, t.getFacing());
        t.turnRight();
        assertEquals(Turtle.Direction.NORTH, t.getFacing());
    }

    @Test
    void turnLeft() {
        Turtle t = new Turtle();
        t.turnLeft();
        assertEquals(Turtle.Direction.WEST, t.getFacing());
        t.turnLeft();
        assertEquals(Turtle.Direction.SOUTH, t.getFacing());
        t.turnLeft();
        assertEquals(Turtle.Direction.EAST, t.getFacing());
        t.turnLeft();
        assertEquals(Turtle.Direction.NORTH, t.getFacing());
    }

    @Test
    void moveEast() {
        Turtle t = new Turtle();
        t.turnRight(); // face EAST
        t.move(5);
        assertEquals(5, t.getX());
        assertEquals(0, t.getY());
    }

    @Test
    void moveSouth() {
        Turtle t = new Turtle();
        t.turnRight();
        t.turnRight(); // face SOUTH
        t.move(4);
        assertEquals(0, t.getX());
        assertEquals(-4, t.getY());
    }

    @Test
    void moveWest() {
        Turtle t = new Turtle();
        t.turnLeft(); // face WEST
        t.move(2);
        assertEquals(-2, t.getX());
        assertEquals(0, t.getY());
    }

    @Test
    void finalStateString() {
        Turtle t = new Turtle();
        t.move(1);
        t.turnRight();
        t.move(2);
        assertEquals("Final position: (2, 1), facing EAST", t.toString());
    }
}
