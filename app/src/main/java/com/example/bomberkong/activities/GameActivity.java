package com.example.bomberkong.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.example.bomberkong.MusicService;
import com.example.bomberkong.World;

// credits for framework: John Horton

/**
 * This activity starts the main game grid
 */
public class GameActivity extends Activity {
    /**
     * The world stores instances such as the main game grid, players, and soundPools. It acts as the
     * main game loop, where the run method keeps track of updates and the onTouchEvent handles player
     * inputs.
     */
    private World mWorld;
    private boolean mIsBound = false;
    private MusicService mServ;

    /**
     * Instantiates the game grid Activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets the player number that is being controlled - 1 or 2.
        String playerNumControlled = getIntent().getStringExtra("playerID");

        // To get the display and display dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Log.e("GameActivity",getApplicationContext().toString());
        // Instantiates the World
        mWorld = new World(getApplicationContext(), size.x, size.y, playerNumControlled);
        setContentView(mWorld);

        // Music
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

    }

    // Playing music
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    // Resume and Pause
    /**
     * Resumes the game
     */

    @Override
    protected void onResume() {
        super.onResume();
        mWorld.resume();

    }

    /**
     * Pauses the game
     */
    @Override
    protected void onPause() {
        super.onPause();
        mWorld.pause();
    }
}