package com.example.myapplication.View.main;
import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import com.example.myapplication.Model.coreLogic.Game;
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private final Game game;
    private SurfaceHolder holder;

    public GamePanel(Context context) { //this is the surface view that hold game frames
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        game = new Game(holder); //create game
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return game.touchEvent(event);
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        game.startGameLoop();
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }
}