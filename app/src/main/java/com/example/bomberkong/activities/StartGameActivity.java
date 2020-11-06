package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bomberkong.R;

/**
 * This is the first screen of the game.
 * Contains the game logo and buttons to start the game.
 */
public class StartGameActivity extends AppCompatActivity {

    /**
     * This button allows you to create a game.
     */
    private Button single_player_game;
    /**
     * This button allows you to join a game.
     */
    private Button multi_player_game;
    /**
     * This button allows you to open the How To Play screen.
     */
    private Button how_to_play;

    /**
     * Creates the activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        // single_player_game is actually Create a Game
        // TODO: Change the variable name of this button here and in the XML file
        Button single_player_game = (Button) findViewById(R.id.single_player);
        // multi_player_game is actually Join a Game
        // TODO: Change the variable name of this button here and in the XML file
        Button multi_player_game = (Button) findViewById(R.id.multi_player);
        // How to Play button
        Button how_to_play = (Button) findViewById(R.id.howtoplay);
        
        single_player_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerActivity = new Intent(StartGameActivity.this, SelectPlayerActivity.class);
                playerActivity.putExtra("playerID", "1");
                startActivity(playerActivity);
            }
        });

        multi_player_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerActivity = new Intent(StartGameActivity.this, SelectPlayerActivity.class);
                playerActivity.putExtra("playerID", "1");
                startActivity(playerActivity);
            }
        });

        how_to_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerActivity = new Intent(StartGameActivity.this, SelectPlayerActivity.class);
                playerActivity.putExtra("playerID", "1");
                startActivity(playerActivity);
            }
        });
    }
}
