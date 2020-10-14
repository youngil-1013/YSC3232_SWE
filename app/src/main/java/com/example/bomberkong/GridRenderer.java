package com.example.bomberkong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;

import com.example.bomberkong.World;
import com.example.bomberkong.model.Grid;

public class GridRenderer implements Grid.Callback, SurfaceHolder.Callback{
    private Grid grid;

    private SurfaceHolder holder;

    public GridRenderer (Grid grid) {
        this.grid = grid;
    }

    // Draw game objects and HUD


    private void drawSurfaceView() {
        if (grid != null && holder != null) {
            Canvas canvas = holder.lockCanvas();
            this.drawGrid(grid, canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGrid(Grid grid, Canvas canvas){
        if (grid == null) return;
        if (canvas == null) return;

        canvas.drawARGB(255, 255, 255, 255);

        Paint gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gridPaint.setStrokeWidth(10);
        gridPaint.setARGB(135, 0, 0, 0);


        int xcount = grid.getW();
        int ycount = grid.getH();

        for (int n = 0; n < xcount; n++) {
            float xpos = n * canvas.getWidth() / xcount ;
            canvas.drawLine(canvas.getWidth() - xpos, 0,canvas.getWidth() - xpos,  canvas.getHeight(),gridPaint);
        }
        // Can you draw inside the grid? Draw BitMap
        // If CellStatus BitMap
        for (int n = 0; n < ycount; n++) {
            float ypos = n * canvas.getHeight() / ycount ;
            canvas.drawLine(canvas.getWidth() - 0, ypos,canvas.getWidth() - canvas.getWidth(), ypos,gridPaint);
        }
    }

    // Todo: draw buttons for player input, give them the coordinates <what are the bounds for up down left right, how to scale that?>
    // Drawing should be gray and semitransluscent so that player can click while still seeing what's happening on the game.

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.holder = holder;
        drawSurfaceView();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        drawSurfaceView();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        this.holder = null;
    }

    @Override
    public void gridChanged(Grid grid) {
        this.drawSurfaceView();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
