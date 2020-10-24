package com.example.bomberkong;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;

import com.example.bomberkong.model.Grid;

public class MainActivity extends AppCompatActivity {

    private  final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SurfaceView sv = null;
        Grid grid = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        /**
         * Gets the size of the display
         */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        /**
         * We pass the size of the display into world
         */
        World world = new World(this, size.x, size.y);

        grid = world.returnGrid();

        Renderer renderer = new Renderer(world, this);
        grid.addCallBack(renderer);

        sv = (SurfaceView) findViewById(R.id.surfaceView);
        sv.setWillNotDraw(false);
        sv.getHolder().addCallback(renderer);

        Log.d("TAG", "test");
        InputListener inputListener = new InputListener(20, 10, size.x, size.y);
        sv.setOnTouchListener(inputListener);

    }
}