package com.example.bomberkong.util;

import androidx.annotation.NonNull;

/**
 * Represents a collection of two integer values. Can be used to store grid positions.
 */
public class Int2
{
    public int x;
    public int y;

    /**
     * Creates a collection of two integer values.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */

    public Int2() {}

    public Int2(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the Int2
     *
     * @return int x-coordinate
     */
    public int getX(){
        return this.x;
    }

    /**
     * Returns the y-coordinate of the Int2
     *
     * @return int y-coordinate
     */
    public int getY(){
        return this.y;
    }

    public void setInt2(Int2 location) {
        this.x = location.x;
        this.y = location.y;
    }


    /**
     * Adds one Int2 to another Int2 and returns the summed result
     *
     * @param other the other Int2 to be added to this Int2
     * @return Int2 The sum of two Int2
     */
    public Int2 addReturn(Int2 other){
        Int2 out = new Int2(this.x + other.getX(), this.y + other.getY());
        return out;
    }

    /**
     * Compares this Int2 with another object and considers whether the
     * object being compared to is of equal type and whether the x- and y-
     * coordinates of the two Int2 are equal.
     *
     * @param o the object to be compared to this Int2
     * @return Boolean Whether the the object is also and Int2 with same x- and y- coordiantes
     */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (o.getClass() != getClass())
            return false;

        Int2 e = (Int2) o;

        return this.getX() == e.getX() && this.getY() == e.getY();
    }

    /**
     * Returns the hashcode of this Int2
     *
     * @return int hashcode
     */
    @Override
    public int hashCode()
    {
        return x + y * 100;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + "," + Integer.toString(y);
    }
}
