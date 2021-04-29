package com.mygdx.runnjump.util;

/**
 * This class represents an x,y coordinate on a grid.
 */
public class Position {
    private float x, y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
