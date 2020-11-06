package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bomberkong.R;

/**
 * This activity contains a scrolling view that allows user to
 * understand how to play the game.
 */
public class HowToPlayActivity extends AppCompatActivity {

    /**
     * This button leads to the screen where you can Join a Game
     */
    private Button joinGame;
    /**
     * This button leads to the screen where you can Create a Game
     */
    private Button createGame;

    /**
     * Called when the How to Play activity is starting
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        Button joinGame = (Button) findViewById(R.id.howto_joingame);
        Button createGame = (Button) findViewById(R.id.howto_creategame);

        /*
          When the 'Join Game' button is clicked, the screen allowing you to join
          a game is created
         */
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeListOfRooms = new Intent(HowToPlayActivity.this, SelectPlayerActivity.class);
                startActivity(seeListOfRooms);
            }
        });

        /*
          When the 'Create Game' button is clicked, the screen that allows you to
          create a game is created
         */
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNewRoom = new Intent(HowToPlayActivity.this, SelectPlayerActivity.class);
                startActivity(createNewRoom);
            }
        });

    }
}