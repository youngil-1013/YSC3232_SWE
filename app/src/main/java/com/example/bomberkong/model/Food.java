package com.example.bomberkong.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.bomberkong.R;
import com.example.bomberkong.util.Int2;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents the Food a Player Object can pick up. The respective player's point increases by 1 when the food is picked up.
 */
public class Food {
    /**
     * The position of the food on the grid.
     */
    public static Int2 position;

    /**
     * The absolute width of each food. Calculated by dividing the width-wise resolution of the
     * screen with the amount of horizontal cells.
     */
    private int cellWidth;

    /**
     * The absolute height of each food. Calculated by dividing the height-wise resolution of the
     * screen with the amount of vertical cells.
     */
    private int cellHeight;

    /**
     * The bitmap for the food object
     */
    private Bitmap mBitmapFood;

    /**
     * Constructor method for Food. It requires context, position, cellSize to be passed from the
     * world. It initializes the rendering size for the food sprite once a food is created.
     *
     * @param context passed from World for rendering
     * @param position gridPosition of the food on the grid
     * @param cellSize size of each cell
     */
    public Food(Context context, Int2 position, Int2 cellSize) {
        this.position = position;
        cellWidth = cellSize.x;
        cellHeight = cellSize.y;

        // we can spawn food, and then move it around everytime player eats it.
        mBitmapFood = BitmapFactory.decodeResource(context.getResources(), R.drawable.banana);
        // Resize the bitmap
        mBitmapFood = Bitmap.createScaledBitmap(mBitmapFood, cellWidth, cellHeight, false);
    }

    /**
     * Takes a list of all empty cells in the grid as candidates and generates a new random
     * position for the food.
     *
     * @param empty ArrayList containing all empty spaces
     * @param numCellsWide number of horizontal cell sin the grid
     * @param nullCellsHigh number of vertical cells in the grid
     *
     * @return Int2 New location of fruit
     */
    public Int2 spawn(ArrayList<Int2> empty, int numCellsWide, int nullCellsHigh) {
        // Choose two random values, validate that its empty, then place the food
        Random random = new Random();

        int candidateX = random.nextInt(numCellsWide) + 1;
        int candidateY = random.nextInt(nullCellsHigh) + 1;

        while (!(empty.contains(new Int2(candidateX, candidateY)))) {
            candidateX = random.nextInt(numCellsWide) + 1;
            candidateY = random.nextInt(nullCellsHigh) + 1;
        }
        // if an empty cell is found:
        position.x = candidateX;
        position.y = candidateY;

        return position;
    }

    /**
     * Returns the grid position of the food
     *
     * @return Int2 the grid position
     */
    public Int2 getLocation() {
        return position;
    }

    /**
     * Sets the grid position of the food
     *
     * @param location grod position of the food
     */
    public void setLocation(Int2 location) { position = location; }

    /**
     * Draws the food on the world
     *
     * @param canvas where the food will be drawn
     * @param paint the color to draw the food's borders with
     */
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapFood,position.getX() * cellWidth, position.getY() * cellHeight, paint);
    }
}

