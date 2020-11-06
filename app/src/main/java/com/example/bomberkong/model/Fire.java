package com.example.bomberkong.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.bomberkong.util.Int2;

import static com.example.bomberkong.R.drawable.fire;

/**
 * Represents fire that will kill the player upon contact
 */
public class Fire implements Cell
{
    /**
     * The position of the fire on the grid.
     */
    public Int2 position;

    /**
     * The context of the game (for rendering mostly), passed down in the constructor method.
     */
    private final Context context;

    /**
     * The grid where the cellStatus will be stored
     */
    private final Grid grid;

    /**
     * The absolute width of each fire. Calculated by dividing the width-wise resolution of the
     * screen with the amount of horizontal cells.
     */
    private int cellWidth;

    /**
     * The absolute height of each fire. Calculated by dividing the height-wise resolution of the
     * screen with the amount of vertical cells.
     */
    private int cellHeight;

    /**
     * The bitmap for the fire object
     */
    private Bitmap mBitmapFire;

    /**
     * Represents the number of ticks before the fire disappears. With the current game in 10 fps,
     * the fire will disappear after 1 second. ticksToFade ticks down by 1 every frame in the World.
     */
    public int ticksToFade = 10;

    /**
     * Constructor method for Fire. It requires context, grid, position, and cellSize. It initializes
     * the rendering size of the bomb sprite once a fire is created.
     * @param context
     * @param grid
     * @param position
     * @param cellSize
     */
    public Fire(Context context, Grid grid, Int2 position, Int2 cellSize) {
        this.grid = grid;
        this.context = context;
        this.position = position;
        cellWidth = cellSize.x;
        cellHeight = cellSize.y;

        mBitmapFire = BitmapFactory.decodeResource(context.getResources(), fire);
        // Resize the bitmap
        // todo: gridPosToAbsolute
        mBitmapFire = Bitmap.createScaledBitmap(mBitmapFire, cellWidth, cellHeight, false);
    }

    /**
     * Returns the grid position of the fire
     *
     * @return Int2 the fire position
     */
    public Int2 getGridPosition(){
        return this.position;
    }


    /**
     * Removes the cellStatus fire from the grid.
     */
    public void remove() {
        grid.setCell(getGridPosition(), CellStatus.EMPTY);
    }
}
