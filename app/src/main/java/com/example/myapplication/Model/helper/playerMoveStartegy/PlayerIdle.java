package com.example.myapplication.Model.helper.playerMoveStartegy;

import android.graphics.PointF;
import com.example.myapplication.Model.helper.HelpMethods;

public class PlayerIdle implements PlayerMoveStrategy {
    @Override
    public void setPlayerAnim(float xSpeed, float ySpeed, PointF lastTouchDiff) {
    }

    @Override
    public PointF playerMovement(float xSpeed, float ySpeed, float baseSpeed) {
        return HelpMethods.playerMovementIdle();
    }
}
