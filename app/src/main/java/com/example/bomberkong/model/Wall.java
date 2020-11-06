package com.example.bomberkong.model;

import com.example.bomberkong.util.Int2;

/**
 * @deprecated no longer used as walls are simply handled inside the grid itself and are not
 * instantiated in World.
 *
 * Represents a wall in the world
 */
public class Wall implements Cell
{
    /**
     * Grid position of the wall
     */
    private Int2 position;

    /**
     * Constructor class for the wall.
     *
     * @param position sets the position of the wall
     */
    public Wall(Int2 position){
        this.position = position;
    }

    /**
     * Returns the grid position of the wall.
     *
     * @return Int2 grid position
     */
    public Int2 getGridPosition(){
        return this.position;
    }
}

