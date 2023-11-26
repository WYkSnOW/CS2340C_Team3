package com.example.myapplication.Model.loopVideo;

import android.graphics.PointF;

public class GameAnimation extends VideoFrame {


    public GameAnimation(float locX, float locY, int width, int height, GameVideos anim) {
        super(new PointF(locX, locY), width, height, anim);

    }

    public void update() {
        updateAnimation();
    }



}
