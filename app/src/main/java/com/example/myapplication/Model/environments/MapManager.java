package com.example.myapplication.Model.environments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.example.myapplication.Model.entities.GameCharacters;
import com.example.myapplication.Model.entities.Items.Item;
import com.example.myapplication.Model.entities.Items.Items;
import com.example.myapplication.Model.entities.Player.Player;
import com.example.myapplication.Model.entities.enemies.AbstractEnemy;
import com.example.myapplication.Model.environments.Doorways.Doorway;
import com.example.myapplication.Model.environments.Doorways.DoorwayType;
import com.example.myapplication.Model.helper.GameConstants;
import com.example.myapplication.Model.helper.HelpMethods;
import com.example.myapplication.View.main.gameStates.Playing;

import java.util.ArrayList;

public class MapManager {
    private GameMap currentMap;
    private GameMap mapOne;
    private GameMap mapTwo;
    private GameMap mapThree;
    private float cameraX;
    private float cameraY;
    private final Playing playing;
    public MapManager(Playing playing) {
        this.playing = playing;
        initMap();
    }


    public void draw(Canvas c) {
        drawTiles(c);
        drawItems(c);
        drawDoorway(c);
    }


    public void drawTiles(Canvas c) {
        for (int j = 0; j < currentMap.getArrayHeight(); j++) {
            for (int i = 0; i < currentMap.getArrayWidth(); i++) {
                c.drawBitmap(
                        currentMap.getFloorType().getSprite(currentMap.getSpriteID(i, j)),
                        i * GameConstants.Sprite.SIZE + cameraX,
                        j * GameConstants.Sprite.SIZE + cameraY,
                        null
                ); //原像素16，放大6倍，即96
            }
        }
    }

    public void drawItems(Canvas c) {
        if (currentMap.getItemArrayList() != null) {
            for (Item i : currentMap.getItemArrayList()) {
                if (i.isActive()) {
                    i.drawItem(c, cameraX, cameraY);
                }

            }
        }
    }
    public void drawDoorway(Canvas c) {
        Paint hitBoxPaint = new Paint();
        hitBoxPaint.setStrokeWidth(1);
        hitBoxPaint.setStyle(Paint.Style.STROKE);
        hitBoxPaint.setColor(Color.RED);
        if (currentMap.getDoorwayArrayList() != null) {
            for (Doorway i : currentMap.getDoorwayArrayList()) {
                c.drawRect(
                        i.getHitbox().left + cameraX,
                        i.getHitbox().top + cameraY,
                        i.getHitbox().right + cameraX,
                        i.getHitbox().bottom + cameraY,
                        hitBoxPaint);
            }
        }
    }

    public void changeMap(Doorway doorwayTarget) {

        this.currentMap = doorwayTarget.getGameMapLocatedIn();
        Player.getInstance().setCurrentMap(currentMap);

        float cX = GameConstants.UiSize.GAME_WIDTH / 2 - doorwayTarget.getPosOfDoorway().x;
        float cY = GameConstants.UiSize.GAME_HEIGHT / 2 - doorwayTarget.getPosOfDoorway().y;

        playing.setCameraValues(new PointF(cX, cY));
        cameraX = cX;
        cameraY = cY;

        playing.setDoorwayJustPassed(true);
    }
    public Doorway isPlayerOnDoorway(RectF playerHitbox) {
        for (Doorway doorway : currentMap.getDoorwayArrayList()) {
            if (doorway.isPlayerInsideDoorway(playerHitbox, cameraX, cameraY)) {
                return doorway;
            }
        }
        return null;
    }
    private void initMap() {
        //2d array that use number to represent block in map asset
        int[][] mapOneArray = InitMap.initMapOne();
        int[][] mapTwoArray = InitMap.initMapTwo();
        int[][] mapThreeArray = InitMap.initMapThree();




        //OUTSIDE here is the map asset
        mapOne = new GameMap(mapOneArray, Floor.OUTSIDE);
        mapTwo = new GameMap(mapTwoArray, Floor.OUTSIDE);
        mapThree = new GameMap(mapThreeArray, Floor.OUTSIDE);

        initMobList();
        initItemList();

        initDoorway();

        currentMap = mapOne;
        Player.getInstance().setCurrentMap(currentMap);
    }


    private void initItemList() {
        mapOne.getItemArrayList().add(new Item(
                new PointF(2 * GameConstants.Sprite.SIZE, 2 * GameConstants.Sprite.SIZE),
                Items.CHEST_ONE));
        mapOne.addItemsToList(HelpMethods.getItemRandomized(
                2, mapOne, Items.READ_HEART));
        mapOne.addItemsToList(HelpMethods.getItemRandomized(
                2, mapOne, Items.BLUE_HEART));
        mapOne.addItemsToList(HelpMethods.getItemRandomized(
                2, mapOne, Items.YELLOW_HEART));

    }





    private void initMobList() {
        mapOne.addMobsToList(HelpMethods.getMobRandomized(
                2, mapOne, GameCharacters.CHEST_MOB));
        mapOne.addMobsToList(HelpMethods.getMobRandomized(
                3, mapOne, GameCharacters.STEEL_GOLEM));

        mapTwo.addMobsToList(HelpMethods.getMobRandomized(
                3, mapTwo, GameCharacters.ZOMBIE));
        mapTwo.addMobsToList(HelpMethods.getMobRandomized(
                5, mapTwo, GameCharacters.CROW_MAN));

        mapThree.addMobsToList(HelpMethods.getMobRandomized(
                4, mapThree, GameCharacters.CHEST_MOB));
        mapThree.addMobsToList(HelpMethods.getMobRandomized(
                5, mapThree, GameCharacters.STEEL_GOLEM));
    }

    private void initDoorway() {
        //HelpMethods.AddDoorwayToGameMap(mapOne, mapTwo, Doorways.DOORWAY_ONE);
        HelpMethods.connectTwoDoorways(
                mapOne,
                HelpMethods.createHitboxForDoorway(19, 9, DoorwayType.DOORWAY_ONE),
                mapTwo,
                HelpMethods.createHitboxForDoorway(0, 23, DoorwayType.DOORWAY_ONE)
        );

        HelpMethods.connectTwoDoorways(
                mapTwo,
                HelpMethods.createHitboxForDoorway(45, 29, DoorwayType.DOORWAY_TWO),
                mapThree,
                HelpMethods.createHitboxForDoorway(3, 0, DoorwayType.DOORWAY_Three)
        );

        Doorway endGameDoorway =
                new Doorway(HelpMethods.createHitboxForDoorway(
                        14, 29, DoorwayType.END_GAME_DOORWAY), mapThree);
        endGameDoorway.setEndGameDoorway(true);
    }


    public void setCameraValues(float cameraX, float cameraY) {
        this.cameraY = cameraY;
        this.cameraX = cameraX;
    }
    public void resetCameraValues() {
        this.cameraY = 0;
        this.cameraX = 0;
    }

    public int getMaxWidthCurrentMap() {
        return currentMap.getArrayWidth() * GameConstants.Sprite.SIZE;
    }
    public int getMaxHeightCurrentMap() {
        return currentMap.getArrayHeight() * GameConstants.Sprite.SIZE;
    }
    public void resetMap() {
        currentMap = mapOne;
        Player.getInstance().setCurrentMap(currentMap);
        mapOne.clearMobList();
        mapTwo.clearMobList();
        mapThree.clearMobList();
        initMobList();

    }
    public GameMap getCurrentMap() {
        return currentMap;
    }




    public void initEnemyHealthWithDiff() {
        ArrayList<GameMap> mapList = new ArrayList<>();
        mapList.add(mapOne);
        mapList.add(mapTwo);
        mapList.add(mapThree);
        for (GameMap currentMap : mapList) {
            for (AbstractEnemy enemy : currentMap.getMobArrayList()) {
                if (enemy.isActive()) {
                    enemy.initWithDiff(Player.getInstance().getDifficulty());
                }
            }
        }
    }


}