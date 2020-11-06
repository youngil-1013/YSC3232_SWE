package com.example.bomberkong;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.bomberkong.model.Bomb;
import com.example.bomberkong.model.CellStatus;
import com.example.bomberkong.model.Heading;
import com.example.bomberkong.model.Fire;
import com.example.bomberkong.model.Food;
import com.example.bomberkong.model.Grid;
import com.example.bomberkong.model.Player;

import com.example.bomberkong.util.Int2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The world stores instances such as the main game grid, players, and soundPools. It acts as the
 * main game loop, where the run method keeps track of updates and the onTouchEvent handles player
 * inputs.
 * credits for framework: John Horton
 */
public class World extends SurfaceView implements Runnable {

    /**
     * The number of the player being controlled
     * TODO: Convert it into an int
     */
    private final String playerNumControlled;

    /**
     * Instance of firebase to communicate with it
     */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * SurfaceHolder to get the surface to draw on
     */
    private SurfaceHolder mSurfaceHolder;

    /**
     * Canvas to draw objects on
     */
    private Canvas mCanvas;

    /**
     * Paint to draw the borders of the objects
     */
    private Paint mPaint;

    /**
     * Font color for the score
     */
    private Paint scorePaint;

    /**
     * Required to get the bitmaps out of
     */
    private Context context;

    /**
     * Main game grid where all movements are recorded and tracked. Passed into some objects when
     * the objects need to detect surrounding cells
     */
    private Grid grid;

    /**
     * Instance of the player controlled by the first user
     */
    private Player playerOne;

    /**
     * Instance of the player controlled by the second user
     */
    private Player playerTwo;

    /**
     * Instance of the food that can be collected by players
     */
    private Food food;

    /**
     * List of all available fires. Updated in the main game loop, run().
     */
    private ArrayList<Fire> fireList = new ArrayList<Fire>();

    /**
     * Number of width-wise cells in the grid
     */
    private int numCellsWide;

    /**
     * Number of height-wise cells in the grid
     */
    private int numCellsHigh;

    /**
     * Pixel-wise resolution of each cell
     */
    public Int2 cellResolution;

    /**
     * The frames per second the game is run at
     */
    private long mFPS;

    /**
     * How many milliseconds are in one second. Used to determine the FPS of the game.
     */
    private final int MILLI_IN_SECONDS = 1000;

    /**
     * Vertical pixel-wise size of the screen
     */
    public int mScreenX;

    /**
     * Horizontal pixel-wise size of the screen
     */
    public int mScreenY;

    /**
     * Font size of the scores
     */
    public int mFontSize;

    /**
     * Margin between each text
     */
    public int mFontMargin;

    /**
     * Player score for player 1
     */
    public int mScoreP1 = 0;

    /**
     * Player score for player 2
     */
    public int mScoreP2 = 0;

    /**
     * Instance of SoundPool. Will be modified as necessary
     */
    private SoundPool mSP;

    /**
     * Sound to be called when the fire is despawned
     */
    private int mSpawn_ID = -1; // sound when fire is gone

    /**
     * Sound to be called when the food is pickedup
     */
    private int mEat_ID = -1; // sound when fruit is picked

    /**
     * Sound to be called when bomb explodes
     */
    private int mBombID = -1; // sound when bomb explodes

    /**
     * Sound to be called when the player dies
     */
    private int mDeathID = -1; // sound when player dies

    /**
     * Sound to be called when player walks (alternate between left and right sounds)
     */
    private int mLWalkID = -1;
    private int mRWalkID = -1;

    /**
     * Sound to be called when player places a bomb
     */



    /**
     * Background music
     */
    private int mBGMID = -1;

    /**
     * Thread to control the main game loop
     */
    private Thread mGameThread = null;

    /**
     * Whether game is being played or not
     */
    private volatile boolean mPlaying = true;

    /**
     * Whether game is being played or not
     */
    private boolean mPaused = false;

    /**
     * Whether player 1 wins the game
     */
    private boolean playerOneWin = false;

    /**
     * Whether player 2 wins the game
     */
    private boolean playerTwoWin = false;

    /**
     * Called to determine the time between the current frame and the next
     */
    private long mNextFrameTime;

    /**
     * Called to determine the time before next move register from player 1
     */
    private long p1NextMoveTime;

    /**
     * Called to determine the time before next move register from player 2
     */
    private long p2NextMoveTime;

    /**
     * Starting position for player 1
     */
    private final Int2 p1StartPos = new Int2(2, 2);

    /**
     * Starting position for player 2
     */
    private final Int2 p2StartPos = new Int2(17, 7);

    /**
     * This is the constructor method for World, which acts as the game engine
     *
     * @param c          Context is passed from GameActivity
     * @param actualViewWidth  represents the actual width of the entire view
     * @param actualViewHeight represents the actual height of the entire view
     * @param playerNumControlled is the number of the player that is currently being controlled
     */
    public World(Context c, int actualViewWidth, int actualViewHeight, String playerNumControlled) {
        super(c);
        this.context = c;
        //super(context);
        this.playerNumControlled = playerNumControlled;

        // Number of horizontal/vertical cells
        numCellsWide = 20;
        numCellsHigh = 10;

        // Initialize SoundPool
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes
                                    .CONTENT_TYPE_SONIFICATION)
                            .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            // Prepare sounds in memory
            // pickup food sound
            descriptor = assetManager.openFd("get_food.ogg");
            mEat_ID = mSP.load(descriptor, 0);
            // bomb explode sound
            descriptor = assetManager.openFd("bomb.ogg");
            mBombID = mSP.load(descriptor, 0);
            //death sound
            descriptor = assetManager.openFd("chimchar.ogg");
            mDeathID = mSP.load(descriptor, 0);
            // BGM
            descriptor = assetManager.openFd("BGM.ogg");
            mBGMID = mSP.load(descriptor, 0);
        } catch (IOException e) {
            // Error
        }

        // Initialize grid and players
        grid = new Grid(context, numCellsWide, numCellsHigh, actualViewWidth, actualViewHeight);
        cellResolution = new Int2(actualViewWidth / numCellsWide, actualViewHeight / numCellsHigh);
        playerOne = new  Player(context, grid, p1StartPos, 1, cellResolution, playerNumControlled);
        playerTwo = new Player(context, grid, p2StartPos, 2, cellResolution, playerNumControlled);
        food = new Food(context, new Int2(3, 3), cellResolution);
        fireList = new ArrayList<Fire>();
        grid.setCell(playerOne.getGridPosition(), CellStatus.PLAYER);
        grid.setCell(playerTwo.getGridPosition(), CellStatus.PLAYER);

        // Start playing BGM

        // Drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        scorePaint = new Paint();

        // Initialize with values passed in as params
        mScreenX = actualViewWidth;
        mScreenY = actualViewHeight;

        // 5% of width
        mFontSize = mScreenX / 21;
        // 1.5% of width
        mFontMargin = mScreenX / 75;

        // add database listeners to World to read from Firebase and update the world realtime
        addScoreListener();
        addFoodListener();
        addPlayerPositionListener();
        addPlayerHeadingListener();
        addPlayerDeathListener();
        addBombListener();
        startNewGame();
    }

    /**
     * Creates a listener for the game so that changes to game scores can be tracked
     */
    private void addScoreListener() {
        DatabaseReference _score1Ref = database.getReference("player1/score");
        _score1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("2")) {
                    mScoreP1 = snapshot.getValue(int.class);
                    mCanvas.drawText("Score P1: " + mScoreP1 + " Score P2: " + mScoreP2,
                            mFontMargin, mFontSize, scorePaint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference _score2Ref = database.getReference("player2/score");
        _score2Ref.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("1")) {
                    mScoreP2 = snapshot.getValue(int.class);
                    mCanvas.drawText("Score P1: " + mScoreP1 + " Score P2: " + mScoreP2,
                            mFontMargin, mFontSize, scorePaint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }

    /**
     * Creates a listener for the game so that new bombs can be tracked
     */
    private void addBombListener() {
        if (playerNumControlled.equals("2")) {
            DatabaseReference _bomb1Ref = database.getReference("player1/bomb/GridPosition");
            _bomb1Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Int2 bombOnePos = snapshot.getValue(Int2.class);
                    if (bombOnePos!=null) {
                        Bomb bombOne = new Bomb(context, bombOnePos, cellResolution);
                        grid.setCell(bombOnePos, CellStatus.BOMB);
                        playerOne.setBomb(bombOne);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (playerNumControlled.equals("1")) {
            DatabaseReference _bomb2Ref = database.getReference("player2/bomb/GridPosition");
            _bomb2Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Int2 bombTwoPos = snapshot.getValue(Int2.class);
                    if (bombTwoPos!=null){
                        Bomb bombTwo = new Bomb(context,bombTwoPos,cellResolution);
                        grid.setCell(bombTwoPos, CellStatus.BOMB);
                        playerTwo.setBomb(bombTwo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * Creates a listener for the game so that new food can be tracked
     */
    private void addFoodListener() {
        DatabaseReference _foodRef = database.getReference("food");
        _foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Int2 foodLoc = snapshot.getValue(Int2.class);
                food.setLocation(foodLoc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Creates a listener for the game so that player positions and movements can be tracked
     */
    private void addPlayerPositionListener() {
        DatabaseReference _pos1Ref = database.getReference("player1/position");
        _pos1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("2")) {
                    Int2 pos1 = snapshot.getValue(Int2.class);
                    playerOne.setGridPosition(pos1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference _pos2Ref = database.getReference("player2/position");
        _pos2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("1")) {
                    Int2 pos2 = snapshot.getValue(Int2.class);
                    playerTwo.setGridPosition(pos2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Creates a listener for the game so that player headings can be tracked
     */
    private void addPlayerHeadingListener() {
        DatabaseReference _head1Ref = database.getReference("player1/heading");
        _head1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("2")) {
                    Heading heading1 = snapshot.getValue(Heading.class);
                    playerOne.setHeading(heading1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference _head2Ref = database.getReference("player2/heading");
        _head2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (playerNumControlled.equals("1")) {
                    Heading heading2 = snapshot.getValue(Heading.class);
                    playerTwo.setHeading(heading2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Creates a listener for the game so that player deaths can be tracked
     */
    private void addPlayerDeathListener() {
        DatabaseReference _death1Ref = database.getReference("player1/death");
        _death1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean dead1 = snapshot.getValue(boolean.class);
                playerOne.setDead(dead1);
                playerOneWin = false;
                playerTwoWin = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference _death2Ref = database.getReference("player2/death");
        _death2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean dead2 = snapshot.getValue(boolean.class);
                playerTwo.setDead(dead2);
                playerTwoWin = false;
                playerOneWin = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     * The main gameloop for the game. Updates the game and renders changes every specified time
     * step.
     */
    @Override
    public void run() {
        while (mPlaying) {
            // What time is it at the start of the game loop?
            long frameStartTime = System.currentTimeMillis();

            // if game isn't paused, update 10 times a second
            if (!mPaused) {
                if (updateRequired()) {
                    update();
                }
            }

            // after update, we can draw
            draw();

            // How long was this frame?
            long timeThisFrame =
                    System.currentTimeMillis() - frameStartTime;

            // Dividing by 0 will crash game
            if (timeThisFrame > 0) {
                // Store the current frame in mFPS and pass it to
                // update methods of the grid next frame/loop
                mFPS = MILLI_IN_SECONDS / timeThisFrame;
            }
        }
    }

    /**
     * Start new game is called when we want to start a new game. We start a new game when the game
     * first boots, and also 5 seconds after the game has ended. This resets the grid, and resets
     * the positions of the players, and the score.
     */
    public void startNewGame() {
        // reset grid
        this.grid.reset();
        mPaused = false;
        playerOne.reset(p1StartPos);
        playerTwo.reset(p2StartPos);

        playerOneWin = false;
        playerTwoWin = false;
        playerOne.setDead(false);
        playerTwo.setDead(false);

        food.setLocation(new Int2(9,5));

        DatabaseReference _foodRef = database.getReference("food");
        _foodRef.setValue(food.getLocation());

        // Reset score
        mScoreP1 = 0;
        mScoreP2 = 0;
        DatabaseReference _score1Ref = database.getReference("player1/score");
        _score1Ref.setValue(mScoreP1);
        DatabaseReference _score2Ref = database.getReference("player2/score");
        _score2Ref.setValue(mScoreP2);

        // setup time
        mNextFrameTime = System.currentTimeMillis();
        p1NextMoveTime = System.currentTimeMillis();
        p2NextMoveTime = System.currentTimeMillis();
    }

    /**
     * The draw method draws all grids, game objects, and players
     */
    void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            // Lock Canvas to draw
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw Grid
            grid.draw(mCanvas);

            // Draw food, player
            food.draw(mCanvas, mPaint);

            playerOne.draw(mCanvas, mPaint);
            playerTwo.draw(mCanvas, mPaint);

            // Choose font size
            mPaint.setTextSize(mFontSize);

            // Paint Color for score
            scorePaint.setColor(Color.argb(255, 0, 0, 0));
            scorePaint.setTextSize(mFontSize);

            // Draw score
            mCanvas.drawText("Score P1: " + mScoreP1 + " Score P2: " + mScoreP2,
                    mFontMargin, mFontSize, scorePaint);

            if (mPaused) {
                mPaint.setColor(Color.argb(255, 0, 0, 0));
                mPaint.setTextSize(250);
                if (playerOneWin) {
                    mCanvas.drawText("Player One Wins!", 200, 700, mPaint);
                }
                if (playerTwoWin) {
                    mCanvas.drawText("Player Two Wins!", 200, 700, mPaint);
                }
            }

            // Display drawing on screen
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    /**
     * Update method checks if either of the players have picked up the food and whether
     * the players are dead.
     * it also ticks down the fires and the bombs, and causes the bomb explode into fire.
     */
    public void update() {
        // Did player eat food?
        if (playerOne.checkPickup(food.getLocation())) {
            mScoreP1 += 1;
            DatabaseReference _score1Ref = database.getReference("player1/score");
            _score1Ref.setValue(mScoreP1);
            ArrayList<Int2> emptyCells = grid.getEmpty();
            food.spawn(emptyCells, numCellsWide, numCellsHigh);
            DatabaseReference _foodRef = database.getReference("food");
            _foodRef.setValue(food.getLocation());
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        if (playerTwo.checkPickup(food.getLocation())) {
            mScoreP2 += 1;
            DatabaseReference _score2Ref = database.getReference("player2/score");
            _score2Ref.setValue(mScoreP2);
            ArrayList<Int2> emptyCells = grid.getEmpty();
            food.spawn(emptyCells, numCellsWide, numCellsHigh);
            DatabaseReference _foodRef = database.getReference("food");
            _foodRef.setValue(food.getLocation());
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did player die?
        if (playerOne.getGrid().getCellStatus(playerOne.getGridPosition()) == CellStatus.FIRE) {
            playerOne.setDead(true);
        }

        if (playerOne.getDead()) {
            mSP.play(mDeathID, 1, 1, 1, 0, 1);
            Log.d("World", "Player one dies");
            mPaused = true;
            playerTwoWin = true;
            DatabaseReference _death1Ref = database.getReference("player1/death");
            _death1Ref.setValue(playerOne.getDead());
            draw();
            // Say player 2 wins, timeout, then start new game in 5 secs
            int gameEndCounter = 5;
            while (gameEndCounter != 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameEndCounter -= 1;
            }

            startNewGame();
        }

        if (playerTwo.getGrid().getCellStatus(playerTwo.getGridPosition()) == CellStatus.FIRE) {
            playerTwo.setDead(true);
        }

        if (playerTwo.getDead()) {
            mSP.play(mDeathID, 1, 1, 1, 0, 1);
            Log.d("World", "Player two dies");
            mPaused = true;
            playerOneWin = true;
            DatabaseReference _death2Ref = database.getReference("player2/death");
            _death2Ref.setValue(playerTwo.getDead());
            draw();
            // Say player 1 wins, timeout, then start new game in 5 secs
            int gameEndCounter = 5;
            while (gameEndCounter != 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameEndCounter -= 1;
            }

            startNewGame();
        }


        /**
         * Iterators allow us to remove elements while iterating through a list.
         * In this case, we use iterators to handle both the ticking down of bombs and the ticking down of fires before removing them from the game.
         */

        if (playerOne.getBomb() != null) {
            Bomb bombOne = playerOne.getBomb();
            if (playerNumControlled.equals("1")) {
                Int2 bombOnePos = bombOne.getGridPosition();
                DatabaseReference _bomb1PosRef = database.getReference("player1/bomb/GridPosition");
                _bomb1PosRef.setValue(bombOnePos);
            }
            bombOne.ticksToExplode -= 1;
            if (bombOne.ticksToExplode == 0) {
                bombOne.explode(this, fireList);
                mSP.play(mBombID, 1, 1, 0, 0, 1);
                playerOne.resetBomb();
            }
        }

        if (playerTwo.getBomb() != null) {
            Bomb bombTwo = playerTwo.getBomb();
            if (playerNumControlled.equals("2")){
                Int2 bombTwoPos = bombTwo.getGridPosition();
                DatabaseReference _bomb2PosRef = database.getReference("player2/bomb/GridPosition");
                _bomb2PosRef.setValue(bombTwoPos);
            }
            bombTwo.ticksToExplode -= 1;
            if (bombTwo.ticksToExplode == 0) {
                bombTwo.explode(this, fireList);
                mSP.play(mBombID, 1, 1, 0, 0, 1);
                playerTwo.resetBomb();
            }
        }

        // todo: there seems to be a bug where the screen does not clear!

        Iterator<Fire> fitr = fireList.iterator();
        while (fitr.hasNext()) {
            Fire fire = fitr.next();
            fire.ticksToFade -= 1;
            if (fire.ticksToFade == 0) {
                // todo: something to do with drawing empty?
                grid.setCell(fire.getGridPosition(), CellStatus.EMPTY);
            }
        }
    }

    /**
     * Returns the grid from the world
     *
     * @return Grid world grid
     */
    public Grid getGrid(){
        return this.grid;
    }

    /**
     * Determines whether enough time has passed to call a new update
     *
     * @return Boolean whether update is required
     */
    public boolean updateRequired() {
        // Run at 10 fps
        final long TARGET_FPS = 10;
        // 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // are we due to update the frame?
        if (mNextFrameTime <= System.currentTimeMillis()) {
            // 1 tenth of a second has passed
            mNextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }
        return false;
    }

    /**
     * p1movementAllowed allows us to rate limit the amount of input we can take from player 1,
     * It returns a boolean, which when true, allows us to process the input of the player.
     *
     * @return Boolean movement allowed for player 1
     */
    public boolean p1MovementAllowed() {
        // Run at 4 moves per second
        final long MOVES_PER_SECOND = 4;
        // 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // are we due to update the frame?
        if (p1NextMoveTime <= System.currentTimeMillis()) {
            // 1/2 a second has passed
            p1NextMoveTime = System.currentTimeMillis() + MILLIS_PER_SECOND / MOVES_PER_SECOND;
            return true;
        }
        return false;
    }

    /**
     * p2movementAllowed allows us to rate limit the amount of input we can take from player 2,
     * It returns a boolean, which when true, allows us to process the input of the player.
     *
     * @return Boolean movement allowed for player 2
     */
    public boolean p2MovementAllowed() {
        // Run at 4 moves per second
        final long MOVES_PER_SECOND = 4;
        // 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // are we due to update the frame?
        if (p2NextMoveTime <= System.currentTimeMillis()) {
            // 1/2 a second has passed
            p2NextMoveTime = System.currentTimeMillis() + MILLIS_PER_SECOND / MOVES_PER_SECOND;
            return true;
        }
        return false;
    }

    /**
     * onTouchEvent handles the game inputs. In our case, it considers where in the grid the tap
     * is registered.
     *
     * @param motionEvent description of the tapping motion
     *
     * @return Boolean whether there has been a tap
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    startNewGame();
                    return true;
                }

                if (playerNumControlled.equals("1")){
                    if (p1MovementAllowed()) {
                        playerOne.switchHeading(motionEvent);
                    }
                } else {
                    if (p2MovementAllowed()) {
                        playerTwo.switchHeading(motionEvent);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Handles when the game is paused
     */
    public void pause() {
        mPlaying = false;
        try {
            // stop thread
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    /**
     * Handles resuming to game play
     */
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }
}

