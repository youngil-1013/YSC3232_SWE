package com.example.bomberkong.model;

import com.example.bomberkong.util.Int2;

/**
 * Represents each cell on the grid. All objects on the grid are cells. Currently only has one
 * implementable method.
 */
public interface Cell
{
    /**
     * All classes that implement Cell should have a method to return the Int2 position of the cell.
     *
     * @return Int2 position of this cell
     */
    Int2 getGridPosition();
}
