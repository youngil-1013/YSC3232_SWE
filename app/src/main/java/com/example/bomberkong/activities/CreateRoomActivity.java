package com.example.bomberkong.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.example.bomberkong.R;

/**
 * This activity is created from the 'Create Room' button and can be used
 * to create a room by entering a room name.
 */
public class CreateRoomActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_MESSAGE = "RoomName";
    private Button button;
    private EditText editText;

    /**
     * Creates a Create Room activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Button button = (Button) findViewById(R.id.createRoomName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.RoomName);
                String nameOfRoom = editText.getText().toString();
                if(!nameOfRoom.equals("")) {
                    // button.setText("CREATING ROOM");
                    // button.setEnabled(false);
                    Intent intent = new Intent(getApplicationContext(), WaitingRoomActivity.class);
                    intent.putExtra(EXTRA_ROOM_MESSAGE, nameOfRoom);
                    startActivity(intent);
                }
            }
        });
    }
}