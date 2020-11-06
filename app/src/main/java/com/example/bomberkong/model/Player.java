package com.example.bomberkong.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.view.MotionEvent;

import com.example.bomberkong.R;
import com.example.bomberkong.util.Int2;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Represents the player the user can control.
 */
public class Player implements Cell
{
    /**
     * Represents which player the user is currently controlling.
     */
    private final String playerNumControlled;

    /**
     * The number of the player object
     */
    private final int playerNum;

    /**
     * Instance of the firebase to communicate with the other player
     */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * The pixel width of each cell
     */
    private int cellWidth;

    /**
     * The pixel height of each cell
     */
    private int cellHeight;

    /**
     * Passed in from the world to retrieve bitmap resources
     */
    private Context context;

    /**
     * The player has knowledge of the grid so that it can determine whether adjacent cells are
     * traversable or not
     */
    private Grid grid;

    /**
     * Each player can spawn one bomb
     */
    private Bomb bomb;

    /**
     * Int2 representation of the pixel-wise cellSize of player sprite
     */
    private Int2 cellSize;

    /**
     * The position the player is facing
     */
    private Heading heading = Heading.NEUTRAL;

    /**
     * Bitmap for player one when facing right
     */
    private Bitmap mBitmapHeadRightOne;

    /**
     * Bitmap for player one when facing left
     */
    private Bitmap mBitmapHeadLeftOne;

    /**
     * Bitmap for player one when facing up
     */
    private Bitmap mBitmapHeadUpOne;

    /**
     * Bitmap for player one when facing down
     */
    private Bitmap mBitmapHeadDownOne;

    /**
     * Bitmap for player two when facing right
     */
    private Bitmap mBitmapHeadRightTwo;

    /**
     * Bitmap for player two when facing left
     */
    private Bitmap mBitmapHeadLeftTwo;

    /**
     * Bitmap for player one when facing up
     */
    private Bitmap mBitmapHeadUpTwo;

    /**
     * Bitmap for player one when facing down
     */
    private Bitmap mBitmapHeadDownTwo;

    /**
     * Bitmap for players in their neutral position (during start game and when bomb is placed)
     * TODO: have different sprites for different players?
     */
    private Bitmap mBitmapNeutral;

    /**
     * Whether the player is dead or not
     */
    private boolean dead = false;

    /**
     * The grid position of the player
     */
    private Int2 gridPosition;

    /**
     * The Constructor method for the player. When the player is created, relevant bitmaps are
     * called and resized, and the player is set on the grid at the given position
     *
     * @param context passed from world for calling bitmaps
     * @param grid where relevant cellStatus is stored
     * @param gridPosition position of the player on the grid
     * @param playerNum number label of the player
     * @param cellSize the pixel-wise cellSize of each player
     * @param playerNumControlled the player number of the player being controlled by the current
     *                            user
     */
    public Player(Context context, Grid grid, Int2 gridPosition, int playerNum, Int2 cellSize, String playerNumControlled) {
        this.playerNum = playerNum;
        this.playerNumControlled = playerNumControlled;
        this.context = context;
        this.gridPosition = gridPosition;
        this.grid = grid;
        this.cellSize = cellSize;
        cellWidth = cellSize.x;
        cellHeight = cellSize.y;

        grid.setCell(gridPosition, CellStatus.PLAYER);

        mBitmapHeadUpOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.oneup);
        mBitmapHeadDownOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.onedown);
        mBitmapHeadLeftOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.oneleft);
        mBitmapHeadRightOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.oneright);
        mBitmapHeadUpTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.twoup);
        mBitmapHeadDownTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.twodown);
        mBitmapHeadLeftTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.twoleft);
        mBitmapHeadRightTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.tworight);
        mBitmapNeutral = BitmapFactory.decodeResource(context.getResources(), R.drawable.monkey);

        mBitmapHeadUpOne = Bitmap.createScaledBitmap(mBitmapHeadUpOne, cellWidth, cellHeight, false);
        mBitmapHeadDownOne = Bitmap.createScaledBitmap(mBitmapHeadDownOne, cellWidth, cellHeight, false);
        mBitmapHeadLeftOne = Bitmap.createScaledBitmap(mBitmapHeadLeftOne, cellWidth, cellHeight, false);
        mBitmapHeadRightOne = Bitmap.createScaledBitmap(mBitmapHeadRightOne, cellWidth, cellHeight, false);
        mBitmapHeadUpTwo = Bitmap.createScaledBitmap(mBitmapHeadUpTwo, cellWidth, cellHeight, false);
        mBitmapHeadDownTwo = Bitmap.createScaledBitmap(mBitmapHeadDownTwo, cellWidth, cellHeight, false);
        mBitmapHeadLeftTwo = Bitmap.createScaledBitmap(mBitmapHeadLeftTwo, cellWidth, cellHeight, false);
        mBitmapHeadRightTwo = Bitmap.createScaledBitmap(mBitmapHeadRightTwo, cellWidth, cellHeight, false);
        mBitmapNeutral = Bitmap.createScaledBitmap(mBitmapNeutral, cellWidth, cellHeight, false);
    }

    /**
     * Returns this player's bomb
     *
     * @return Bomb bomb
     */
    public Bomb getBomb(){
        return this.bomb;
    }

    /**
     * When the bomb explodes, the bomb is set as null, until it is called again
     */
    public void resetBomb(){
        this.bomb = null;
        DatabaseReference _bombRef = database.getReference("player" + this.playerNum + "/bomb/GridPosition");
        _bombRef.setValue(null);
    }

    /**
     * Sets the instance of the bomb declared at the world as the controllable bomb for the player
     *
     * @param bomb bomb this player will control
     */
    public void setBomb(Bomb bomb) { this.bomb = bomb; }

    /**
     * Resets the player (after death) at the give starting position. Resets bombs.
     *
     * @param startPos starting position of this player
     */
    public void reset(Int2 startPos) {
        // Reset Grid position
        grid.setCell(startPos, CellStatus.PLAYER);
        grid.setCell(gridPosition, CellStatus.EMPTY);
        gridPosition = startPos;
        // Reset Heading
        heading = heading.NEUTRAL;
        // Reset death
        dead = false;
        // Reset position values reflected on Firebase database
        DatabaseReference _positionRef = database.getReference("player" + playerNum + "/position");
        _positionRef.setValue(startPos);
        // Reset heading values reflected on Firebase database
        DatabaseReference _headingRef = database.getReference("player" + playerNum + "/heading");
        _headingRef.setValue(heading);
        // Reset death values reflected on Firebase database
        DatabaseReference _deathRef = database.getReference("player" + playerNum + "/death");
        _deathRef.setValue(dead);
    }

    /**
     * Handles movement of the player.
     */
    void move() {
        switch (heading) {
            case UP:
                moveUp(grid);
                break;

            case RIGHT:
                moveRight(grid);
                break;

            case DOWN:
                moveDown(grid);
                break;

            case LEFT:
                moveLeft(grid);
                break;

            case NEUTRAL:
                break;
        }
    }

    /**
     * Returns whether the player is dead or not
     *
     * @return Boolean dead
     */
    public boolean getDead() { return this.dead; }

    /**
     * Sets whether the player is dead or not (called when the player touches the fire after
     * explosion.
     *
     * @param death whether the player is dead or not
     */
    public void setDead(boolean death) { this.dead = death; }

    /**
     * During update() in the World, checks whether each player has the same coordinate as the food.
     *
     * @param foodPosition position of the food in the world
     *
     * @return boolean whether the player has picked up the food
     */
    public boolean checkPickup(Int2 foodPosition) {
        // Check if the food has the same coordinates as Player.
        // if so, increment score etc.
        if (gridPosition.x == foodPosition.x && gridPosition.y == foodPosition.y) {
            return true;
        }
        return false;
    }

    /**
     * Draws the player bitmap onto the canvas. Different player sprites are used depending on
     * the player number.
     *
     * @param canvas where the player is drawn
     * @param paint the color to paint the border of the player with
     */
    public void draw(Canvas canvas, Paint paint) {
        switch (heading) {
            case UP:
                if (playerNum == 1){
                    canvas.drawBitmap(mBitmapHeadUpOne, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                } else {
                    canvas.drawBitmap(mBitmapHeadUpTwo, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                }
                break;

            case DOWN:
                if (playerNum == 1){
                    canvas.drawBitmap(mBitmapHeadDownOne, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                } else {
                    canvas.drawBitmap(mBitmapHeadDownTwo, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                }
                break;

            case LEFT:
                if (playerNum == 1){
                    canvas.drawBitmap(mBitmapHeadLeftOne, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                } else {
                    canvas.drawBitmap(mBitmapHeadLeftTwo, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                }
                break;

            case RIGHT:
                if (playerNum == 1){
                    canvas.drawBitmap(mBitmapHeadRightOne, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                } else {
                    canvas.drawBitmap(mBitmapHeadRightTwo, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                }
                break;

            case NEUTRAL:
                canvas.drawBitmap(mBitmapNeutral, gridPosition.x * cellWidth, gridPosition.y * cellHeight, paint);
                break;
        }
    }

    /**
     * Handles changing direction given a movement
     *
     * @param motionEvent passed in when a touch happens on the view.
     */
    public void switchHeading(MotionEvent motionEvent) {
        float touch_x = motionEvent.getX();
        float touch_y = motionEvent.getY();

        Int2 touchGridPosition = grid.absoluteToGridPos(touch_x, touch_y, grid.getNumCellsWide(), grid.getNumCellsHigh(), grid.getActualViewWidth(), grid.getActualViewHigh());

        int touchGridPositionX = touchGridPosition.getX();
        int touchGridPositionY = touchGridPosition.getY();

        if (touchGridPositionX == this.gridPosition.getX() && touchGridPositionY == this.gridPosition.getY()){
            if (bomb == null){
                spawnBomb(context, grid, cellSize);
            }
            heading = Heading.NEUTRAL;
        }

        // consider between moving right or up or down depending on which is higher or lower displacement
        else if (touchGridPositionX >= this.gridPosition.getX()) {
            // moving towards bottom right
            if (touchGridPositionY >= this.gridPosition.getY() && (touchGridPositionX - this.gridPosition.getX() > touchGridPositionY - this.gridPosition.getY())) {
                heading = Heading.RIGHT;
                DatabaseReference _headingRef = database.getReference("player" + playerNum + "/heading");
                _headingRef.setValue(heading);
            }

            // moving towards bottom right
            else if (touchGridPositionY >= this.gridPosition.getY() && (touchGridPositionY - this.gridPosition.getY() > touchGridPositionX - this.gridPosition.getX())) {
                heading = Heading.DOWN;
            }

            // moving towards top right
            else if (this.gridPosition.getY() >= touchGridPositionY && (touchGridPositionX - this.gridPosition.getX() > touchGridPositionY - this.gridPosition.getY())) {
                heading = Heading.UP;
            }

            // moving towards top right
            else if (this.gridPosition.getY() >= touchGridPositionY && (touchGridPositionY - this.gridPosition.getY() > touchGridPositionX - this.gridPosition.getX())) {
                heading = Heading.RIGHT;
            }
        }

        else {
            // moving towards bottom left
            if (touchGridPositionY >= this.gridPosition.getY() && (this.gridPosition.getX() - touchGridPositionX > touchGridPositionY - this.gridPosition.getY())) {
                heading = Heading.LEFT;
            }

            // moving towards bottom left
            else if (touchGridPositionY >= this.gridPosition.getY() && (touchGridPositionY - this.gridPosition.getY() > this.gridPosition.getX() - touchGridPositionX)) {
                heading = Heading.DOWN;
            }

            // moving towards top left
            else if (this.gridPosition.getY() >= touchGridPositionY && (this.gridPosition.getX() - touchGridPositionX > touchGridPositionY - this.gridPosition.getY())) {
                heading = Heading.LEFT;
            }

            // moving towards top left
            if (this.gridPosition.getY() >= touchGridPositionY && (touchGridPositionY - this.gridPosition.getY() > this.gridPosition.getX() - touchGridPositionX)) {
                heading = Heading.UP;
            }
        }
        // send player heading data to Firebase database
        DatabaseReference _headingRef = database.getReference("player" + playerNum + "/heading");
        _headingRef.setValue(heading);
        // and then move after changing direction
        move();
    }

    /**
     * Returns the grid of the player
     *
     * @return Grid grid
     */
    public Grid getGrid() { return this.grid; }

    /**
     * Returns the grid position of the player
     *
     * @return Int2 gridPosition
     */
    public Int2 getGridPosition(){
        return this.gridPosition;
    }

    /**
     * Sets the grid position of the player
     *
     * @param position Int2 gridPosition
     */
    public void setGridPosition(Int2 position) { this.gridPosition = position; }

    /**
     * Sets the direction the player is facing at a given moment
     *
     * @param heading Heading
     */
    public void setHeading(Heading heading) { this.heading = heading; }

    /**
     * If the cell above is traversable, move up, changing the cellStatus as required.
     *
     * @param grid to consider whether the cell above is traversable
     *
     * @return Grid grid after traversal
     */
    public Grid moveUp(Grid grid){
        Int2 newpos = gridPosition.addReturn(new Int2(0, -1));
        if (grid.getCellStatus(newpos) == (CellStatus.EMPTY) ||
                grid.getCellStatus(newpos) == (CellStatus.FIRE) ||
                grid.getCellStatus(newpos) == (CellStatus.FOOD)
        )
        {
            DatabaseReference _positionRef = database.getReference("player" + playerNum + "/position");
            _positionRef.setValue(newpos);
            grid.setCell(gridPosition, CellStatus.EMPTY);
            gridPosition = newpos;
            grid.setCell(newpos, CellStatus.PLAYER);
        }
        return grid;
    }

    /**
     * If the cell below is traversable, move down, changing the cellStatus as required.
     *
     * @param grid to consider whether the cell below is traversable
     *
     * @return Grid grid after traversal
     */
    public Grid moveDown(Grid grid){
        Int2 newpos = gridPosition.addReturn(new Int2(0, 1));
        if (grid.getCellStatus(newpos) == (CellStatus.EMPTY) ||
                grid.getCellStatus(newpos) == (CellStatus.FIRE) ||
                grid.getCellStatus(newpos) == (CellStatus.FOOD)
        )
        {
            DatabaseReference _positionRef = database.getReference("player" + playerNum + "/position");
            _positionRef.setValue(newpos);
            grid.setCell(gridPosition, CellStatus.EMPTY);
            gridPosition = newpos;
            grid.setCell(newpos, CellStatus.PLAYER);
        }
        return grid;
    }

    /**
     * If the cell on the right is traversable, move right, changing the cellStatus as required.
     *
     * @param grid to consider whether the cell on the right is traversable
     *
     * @return Grid grid after traversal
     */
    public Grid moveRight(Grid grid){
        Int2 newpos = gridPosition.addReturn(new Int2(1, 0));
        if (grid.getCellStatus(newpos) == (CellStatus.EMPTY) ||
                grid.getCellStatus(newpos) == (CellStatus.FIRE) ||
                grid.getCellStatus(newpos) == (CellStatus.FOOD)
        )
        {
            DatabaseReference _positionRef = database.getReference("player" + playerNum + "/position");
            _positionRef.setValue(newpos);
            grid.setCell(gridPosition, CellStatus.EMPTY);
            gridPosition = newpos;
            grid.setCell(newpos, CellStatus.PLAYER);
        }
        return grid;
    }

    /**
     * If the cell on the left is traversable, move left, changing the cellStatus as required.
     *
     * @param grid to consider whether the cell on the left is traversable
     *
     * @return Grid grid after traversal
     */
    public Grid moveLeft(Grid grid){
        Int2 newpos = gridPosition.addReturn(new Int2(-1, 0));
        if (grid.getCellStatus(newpos) == (CellStatus.EMPTY) ||
                grid.getCellStatus(newpos) == (CellStatus.FIRE) ||
                grid.getCellStatus(newpos) == (CellStatus.FOOD)
        )
        {
            DatabaseReference _positionRef = database.getReference("player" + playerNum + "/position");
            _positionRef.setValue(newpos);
            grid.setCell(gridPosition, CellStatus.EMPTY);
            gridPosition = newpos;
            grid.setCell(newpos, CellStatus.PLAYER);
        }
        return grid;
    }

    /**
     * spawnBomb takes context, grid, cellSize, and the current bomb being controlled by the player
     * in the world.
     *
     * @param context passed to the bomb when created
     * @param grid grid of the world to determine whether a bomb can be spawned
     * @param cellSize passed to the bomb when created
     */
    public void spawnBomb(Context context, Grid grid, Int2 cellSize) {
        Int2 spawnpos = gridPosition;
        // switch based on position you're facing
        switch (heading) {
            case UP:
                spawnpos = gridPosition.addReturn(new Int2(0, -1));
                break;

            case DOWN:
                spawnpos = gridPosition.addReturn(new Int2(0, 1));
                break;

            case LEFT:
                spawnpos = gridPosition.addReturn(new Int2(-1, 0));
                break;

            case RIGHT:
                spawnpos = gridPosition.addReturn(new Int2(1, 0));
                break;
        }

        if (grid.getCellStatus(spawnpos) == (CellStatus.EMPTY)) {
            bomb = new Bomb(context, spawnpos, cellSize);
            grid.setCell(spawnpos, CellStatus.BOMB);
        }
    }
}
