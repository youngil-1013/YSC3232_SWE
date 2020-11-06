package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.bomberkong.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity creates a screen with a list of all available game rooms
 * By clicking on a room, you can join the game.
 */
public class JoinRoomActivity extends AppCompatActivity {

    /**
     * Displays the list of all available game rooms
     */
    ListView listview;

    /**
     * Contains the list of all available game rooms
     */
    List<String> listOfAvailableRooms;

    /**
     * Instantiates a string that will contain which room the player will join
     */
    String joiningRoom = "";

    /**
     * Creates the activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        ListView listView = (ListView) findViewById(R.id.listOfRooms);

        // instantiate list of available rooms
        // listOfAvailableRooms = new ArrayList<>();

        // Add function for clicking on list to join a room
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the name of room to join
                joiningRoom = listOfAvailableRooms.get(position);
                // Add yourself to room on Firebase
                // Join game as Player Two
            }
        });

        // Add function for retrieving list of available rooms from Firebase

    }
}