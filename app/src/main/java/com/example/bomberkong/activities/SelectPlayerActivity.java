package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bomberkong.R;

/**
 * Starts the Select Player activity where you choose whether
 * you are Player 1 or Player 2 to join the game
 */
public class SelectPlayerActivity extends AppCompatActivity {
    /**
     * This button starts the user in the game activity as Player 1
     */
    private Button player1Button;
    /**
     * This button starts the user in the game activity as Player 2
     */
    private Button player2Button;

    /**
     * Creates the activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);

        //Button buttonClick = (Button)rootView.findViewById(R.id.button);
        //buttonClick.setOnClickListener(new View.OnClickListener() {
        player1Button = (Button)findViewById(R.id.buttonP1);
        player2Button = (Button)findViewById(R.id.buttonP2);
        final Intent playerActivity = new Intent(getApplicationContext(), GameActivity.class);

        player1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerActivity.putExtra("playerID", "1");
                startActivity(playerActivity);
            }
        });

        player2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerActivity.putExtra("playerID", "2");
                startActivity(playerActivity);
            }
        });

    }
}