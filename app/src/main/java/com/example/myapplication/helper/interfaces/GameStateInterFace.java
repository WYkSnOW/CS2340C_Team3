package com.example.myapplication.helper.interfaces;
import android.graphics.Canvas;
import android.view.MotionEvent;

public interface GameStateInterFace {
    void update(double delta);
    void render(Canvas c);
    void touchEvents(MotionEvent event);

}
