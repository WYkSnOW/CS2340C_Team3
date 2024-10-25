package com.example.myapplication.Model.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.Model.entities.GameCharacters;
import com.example.myapplication.Model.entities.Items.Item;
import com.example.myapplication.Model.entities.Items.Items;
import com.example.myapplication.Model.entities.enemies.normalEnemies.AbstractEnemy;
import com.example.myapplication.Model.entities.enemies.normalEnemies.EnemyFactory;
import com.example.myapplication.Model.environments.Doorways.Doorway;
import com.example.myapplication.Model.environments.Doorways.DoorwayType;
import com.example.myapplication.Model.environments.GameMap;
import com.example.myapplication.Model.ui.CustomButton;

import java.util.ArrayList;

public class HelpMethods {


    public static Bitmap getScaledBitmap(double scale, Bitmap bitmap) {
        return Bitmap.createScaledBitmap(
                bitmap,
                (int) (bitmap.getWidth() * scale),
                (int) (bitmap.getHeight() * scale),
                false
        ); //将图片放大六倍（后期更换constant）
    }


    public static void connectTwoDoorways(
            GameMap gameMapOne, RectF hitboxOne, GameMap gameMapTwo, RectF hitboxTwo
    ) {
        Doorway doorwayOne = new Doorway(hitboxOne, gameMapOne);
        Doorway doorwayTwo = new Doorway(hitboxTwo, gameMapTwo);

        doorwayOne.connectDoorway(doorwayTwo);
        doorwayTwo.connectDoorway(doorwayOne);
    }
    public static RectF createHitboxForDoorway(int xTile, int yTile, DoorwayType doorwayType) {
        float x = xTile * GameConstants.Sprite.SIZE;
        float y = yTile * GameConstants.Sprite.SIZE;

        return new RectF(
                x,
                y - doorwayType.getOffsetY(),
                x + doorwayType.getDoorwayWidth(),
                y + doorwayType.getDoorwayHeight() - doorwayType.getOffsetY());
    }
    public static ArrayList<AbstractEnemy> getMobRandomized(
            int amount, GameMap gameMap, GameCharacters enemyType) {

        int width = (gameMap.getArrayWidth() - 1) * GameConstants.Sprite.SIZE;
        int height = (gameMap.getMapHeight() - 1) * GameConstants.Sprite.SIZE;

        ArrayList<AbstractEnemy> zombieArrayList = new ArrayList<>();
        int i = 0;
        while (i < amount) {
            PointF pos = generateRandomPos(width, height);
            if (
                    gameMap.canMoveHere(pos.x, pos.y, pos.y + (enemyType.getCharacterHeight()))
                            && gameMap.canMoveHere(pos.x + (enemyType.getCharacterWidth()),
                            pos.y, pos.y + (enemyType.getCharacterHeight()))
            ) {
                zombieArrayList.add(EnemyFactory.createEnemy(enemyType, pos));
                //zombieArrayList.add(new Zombie(new PointF(x, y)));
                i++;
            }
        }
        return zombieArrayList;
    }

    public static ArrayList<Item> getItemRandomized(
            int amount, GameMap gameMap, Items itemType) {

        int width = (gameMap.getArrayWidth() - 1) * GameConstants.Sprite.SIZE;
        int height = (gameMap.getMapHeight() - 1) * GameConstants.Sprite.SIZE;

        ArrayList<Item> itemArrayList = new ArrayList<>();
        int i = 0;
        while (i < amount) {
            PointF pos = generateRandomPos(width, height);
            if (
                    gameMap.canMoveHere(pos.x, pos.y, pos.y + (itemType.getHeight()))
                            && gameMap.canMoveHere(pos.x + (itemType.getWidth()),
                            pos.y, pos.y + (itemType.getHeight()))
            ) {
                itemArrayList.add(new Item(pos, itemType));
                i++;
            }
        }
        return itemArrayList;
    }

    public static PointF generateRandomPos(int width, int height) {
        int maxX = width / GameConstants.Sprite.SIZE;
        int maxY = height / GameConstants.Sprite.SIZE;

        int randomX = (int) (Math.random() * maxX);
        int randomY = (int) (Math.random() * maxY);

        float x = randomX * GameConstants.Sprite.SIZE;
        float y = randomY * GameConstants.Sprite.SIZE;

        return new PointF(x, y);
    }

    public static void cleanUi(Activity activity) {
        if (activity != null) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
    public static boolean isInBtn(MotionEvent e, CustomButton b) {
        return b.getHitbox().contains(e.getX(), e.getY());
    }

    public static PointF playerMovementIdle() {
        return new PointF(0, 0);
    }

    public static PointF playerMovementRun(float xSpeed, float ySpeed, float baseSpeed) {
        float deltaX = xSpeed * baseSpeed * -1; //移动镜头而不是角色
        float deltaY = ySpeed * baseSpeed * -1; //因镜头需与角色相反的方向移动，即乘以-1
        return new PointF(deltaX, deltaY);
    }


    public static double getScaleRatio(double width, double height, double gWidth, double gHeight) {
        double ratioX = gWidth / width;
        double ratioY = gHeight / height;
        return Math.max(ratioX, ratioY);
    }

    public static int getIdleAnimation(int currentDir) {
        return currentDir + 2;
    }

    public static boolean checkTimePass(long lastTime, int timeRangeInSec) {
        //unit of time is second
        long currentTime = System.currentTimeMillis();
        return currentTime - lastTime >= timeRangeInSec * 1000L;
    }

}