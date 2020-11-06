package com.example.bomberkong.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.bomberkong.R;
import com.example.bomberkong.World;
import com.example.bomberkong.util.Int2;

import java.util.ArrayList;

/**
 * Represents the bomb. Currently, it will explode after 15 ticks (1.5 seconds) and create fires
 * on its left, right, top, and bottom coordinates.
 * Each player can only have one bomb spawned at one time.
 */
public class Bomb implements Cell
{
    /**
     * The position of the bomb on the grid.
     */
    private Int2 position;

    /**
     * The context of the game (for rendering mostly), passed down in the constructor method.
     */
    private final Context context;

    /**
     * The absolute width of each bomb. Calculated by dividing the width-wise resolution of the
     * screen with the amount of horizontal cells.
     */
    private int cellWidth;

    /**
     * The absolute height of each bomb. Calculated by dividing the height-wise resolution of the
     * screen with the amount of vertical cells.
     */
    private int cellHeight;

    /**
     * The absolute size of each bomb. Simply cellWidth and cellHeight combined into one single
     * Int2.
     */
    private Int2 cellSize;

    /**
     * The bitmap for the bomb object.
     */
    private Bitmap mBitmapBomb;

    /**
     * Represents the number of ticks before the bomb explodes. With the current game in
     * 10 fps, the bomb will explode after 1.5 seconds. ticksToExplode ticks down by 1 every frame
     * in the world.
     */
    public int ticksToExplode = 15;


    private Bomb() {
        this.context = null;
    }

    /**
     * Constructor method for Bomb. It requires context, position, and cellSize. It initializes
     * the rendering size for the bomb sprite once a bomb is created.
     *
     * @param context passed from World for rendering
     * @param position gridPosition of the bomb on the grid
     * @param cellSize size of each cell
     */
    public Bomb(Context context, Int2 position, Int2 cellSize) {
        this.context = context;
        this.position = position;
        this.cellSize = cellSize;
        cellWidth = cellSize.x;
        cellHeight = cellSize.y;

        mBitmapBomb = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb);
        mBitmapBomb = Bitmap.createScaledBitmap(mBitmapBomb, cellWidth, cellHeight, false);
    }

    /**
     * Returns the grid position of the bomb
     *
     * @return Int2 the grid position
     */
    public Int2 getGridPosition(){
        return this.position;
    }

    /**
     * Called when the tickstoExplode is equal to zero. When the bomb explodes, the position
     * of the cell it currently inhabits is set as empty and if the left, right, top, or bottom
     * positions are empty or is a player, replaces the cellStatus with fire.
     *
     * @param world world where the grid is instantiated
     * @param fireList the list of fires in the world where fire will be added
     */
    public void explode(World world, ArrayList<Fire> fireList){
        Grid grid = world.getGrid();
        grid.setCell(getGridPosition(), CellStatus.EMPTY);

        // Getting the CellStatus of surroundings
        Int2 posLeft = position.addReturn(new Int2(-1, 0));
        Int2 posRight = position.addReturn(new Int2(1, 0));
        Int2 posUp = position.addReturn(new Int2(0, -1));
        Int2 posDown = position.addReturn(new Int2(0, 1));
        CellStatus left = grid.getCellStatus(posLeft);
        CellStatus right = grid.getCellStatus(posRight);
        CellStatus up = grid.getCellStatus(posUp);
        CellStatus down = grid.getCellStatus(posDown);

        ArrayList<Int2> fireInt2List = new ArrayList<Int2>();

        if (left == CellStatus.EMPTY || left == CellStatus.PLAYER){
            grid.setCell(posLeft, CellStatus.FIRE);
            fireInt2List.add(posLeft);
        }
        if (right == CellStatus.EMPTY || right == CellStatus.PLAYER){
            grid.setCell(posRight, CellStatus.FIRE);
            fireInt2List.add(posRight);
        }
        if (up == CellStatus.EMPTY || up == CellStatus.PLAYER){
            grid.setCell(posUp, CellStatus.FIRE);
            fireInt2List.add(posUp);
        }
        if (down == CellStatus.EMPTY || down == CellStatus.PLAYER){
            grid.setCell(posDown, CellStatus.FIRE);
            fireInt2List.add(posDown);
        }

        for (Int2 i : fireInt2List){
            Fire fire = new Fire(context, grid, i, cellSize);
            fireList.add(fire);
        }
    }
}
