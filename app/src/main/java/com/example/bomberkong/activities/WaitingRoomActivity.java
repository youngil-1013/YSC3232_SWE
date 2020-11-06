package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bomberkong.R;

/**
 * This is a waiting room for the user who creates a room
 * while a second player a second player joins the room.
 */
public class WaitingRoomActivity extends AppCompatActivity {

    /**
     * Creates the Waiting Room activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // TODO: Add Event Listener... As second player enters room... Start the Game!

        // TODO: Intent from previous screen contains EXTRA_ROOM_MESSAGE plus the nameOfRoom
    }
}