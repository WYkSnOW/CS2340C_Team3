package com.example.myapplication.Model.environments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.example.myapplication.Model.coreLogic.Game;
import com.example.myapplication.Model.entities.GameCharacters;
import com.example.myapplication.Model.entities.Items.Item;
import com.example.myapplication.Model.entities.Items.Items;
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
                c.drawBitmap(
                        i.getItemType().getItemImg(),
                        i.getPos().x + cameraX,
                        i.getPos().y + cameraY,
                        null
                );
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

    public void changeMap(Doorway doorwayTarget, Game game) {

        this.currentMap = doorwayTarget.getGameMapLocatedIn();

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
        int[][] mapOneArray = initMapOne();
        int[][] mapTwoArray = initMapTwo();
        int[][] mapThreeArray = initMapThree();



        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.add(new Item(
                new PointF(2 * GameConstants.Sprite.SIZE, 2 * GameConstants.Sprite.SIZE),
                Items.CHEST_ONE)
        );

        //OUTSIDE here is the map asset
        mapOne = new GameMap(mapOneArray, Floor.OUTSIDE, itemArrayList);
        mapTwo = new GameMap(mapTwoArray, Floor.OUTSIDE, null);
        mapThree = new GameMap(mapThreeArray, Floor.OUTSIDE, null);

        initMobList();

        initDoorway();

        currentMap = mapOne;
    }

    private void initMobList() {
        mapOne.addMobsToList(HelpMethods.getMobRandomized(1, mapOne, GameCharacters.ZOMBIE));
        mapOne.addMobsToList(HelpMethods.getMobRandomized(1, mapOne, GameCharacters.CHEST_MOB));
        mapOne.addMobsToList(HelpMethods.getMobRandomized(1, mapOne, GameCharacters.CROW_MAN));
        mapOne.addMobsToList(HelpMethods.getMobRandomized(1, mapOne, GameCharacters.STEEL_GOLEM));

        mapTwo.addMobsToList(HelpMethods.getMobRandomized(3, mapTwo, GameCharacters.ZOMBIE));
        mapThree.addMobsToList(HelpMethods.getMobRandomized(4, mapThree, GameCharacters.ZOMBIE));
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

    private int[][] initMapTwo() {
        return new int[][]{
                {2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 3, 3, 3, 3, 3, 3, 6},
                {2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 67, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56,
                    56, 70, 16, 16, 67, 56, 56, 56, 70, 124, 125, 6, 0,
                    0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 3, 3, 3, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 6, 0, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 6, 2, 16, 16,
                    58, 0, 0, 0, 28, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0, 2,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 6, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 2,
                    16, 16, 58, 0, 0, 0, 28, 124, 125, 6, 0, 0, 0, 0, 0, 0,
                    0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 6, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 2,
                    16, 16, 58, 0, 0, 0, 28, 124, 125, 6, 0, 0, 0, 0, 0, 0,
                    0, 0, 2, 135, 136, 137, 67, 81, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 3, 3, 3, 16, 16, 16, 16, 16, 16, 16, 16, 16, 3, 3, 16,
                    16, 58, 0, 0, 0, 28, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0,
                    2, 135, 136, 137, 6, 2, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 58, 0, 0, 0, 28, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0,
                    2, 135, 136, 137, 6, 2, 16, 16, 16, 16, 16, 16, 16, 16, 6},
                {2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 58, 2, 3, 3, 3, 124, 125, 3, 3, 3, 45, 0, 0, 0, 0,
                    0, 2, 135, 136, 137, 6, 54, 56, 56, 56, 56, 56, 56, 56, 56, 58},
                {54, 56, 56, 56, 56, 70, 16, 16, 16, 16, 16, 16, 16, 16, 16, 67, 57,
                    57, 57, 58, 2, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0, 0,
                    0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0,
                    2, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0, 0, 0, 0, 2,
                    135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0,
                    2, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0, 0, 0, 0, 2, 135,
                    136, 137, 6, 0, 2, 3, 3, 3, 3, 3, 45, 0, 0},
                {0, 0, 0, 0, 0, 54, 56, 56, 70, 135, 136, 137, 67, 56, 56, 58, 0, 0, 0,
                    0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 3, 3, 3, 3, 3, 3, 3, 16,
                    16, 16, 3, 3, 3, 16, 16, 16, 16, 16, 45, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 2, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16,
                    16, 16, 16, 16, 16, 16, 67, 56, 56, 56, 56, 56, 56, 70, 135, 136, 137,
                    67, 70, 16, 16, 16, 16, 16, 45, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16,
                    16, 16, 16, 16, 16, 16, 58, 0, 0, 0, 0, 0, 0, 2, 135, 136, 137, 6,
                    54, 56, 56, 56, 56, 56, 58, 0, 0},
                {0, 0, 0, 0, 0, 2, 3, 3, 3, 135, 136, 137, 3, 3, 3, 6, 0, 0, 0, 0, 2, 16,
                    16, 16, 16, 16, 16, 16, 16, 58, 0, 0, 0, 0, 0, 0, 2, 135, 136,
                    137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0,
                    54, 56, 81, 135, 136, 136, 137, 67, 56, 58, 0, 0, 0, 0, 0,
                    0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0,
                    0, 0, 0, 2, 135, 136, 136, 137, 6, 0, 0, 0, 0, 0, 0,
                    0, 0, 2, 135, 136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 3, 3, 3, 3, 3, 16, 16, 16, 16, 16, 16, 16, 16, 16, 3, 3, 3,
                    3, 3, 3, 3, 3, 135, 136, 136, 137, 6, 0, 0, 0, 0, 0,
                    0, 2, 3, 3, 135, 136, 137, 3, 3, 6, 0, 0, 0, 0, 0, 0, 0},
                {16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0,
                    0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 0, 0, 0},
                {16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0,
                    0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 3, 3, 3, 3, 3, 6, 0, 0},
                {16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 0, 0, 2, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0},
                {56, 56, 56, 56, 56, 56, 16, 16, 16, 16, 16, 16, 16, 16, 16, 56,
                    56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 58, 0, 0, 0, 0,
                    0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16, 16,
                    16, 16, 16, 16, 67, 56, 70, 124, 125, 6, 0, 0},
                {0, 0, 0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 16, 16, 16,
                    16, 16, 16, 16, 58, 0, 54, 124, 125, 6, 0, 0},
                {0, 0, 0, 0, 0, 54, 56, 56, 56, 56, 56, 56, 56, 56, 56, 58, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 54, 56, 56,
                    6, 56, 56, 56, 56, 58, 0, 54, 124, 125, 6, 0, 0}
        };
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
        mapOne.clearMobList();
        mapTwo.clearMobList();
        mapThree.clearMobList();
        initMobList();
    }
    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void changeCurrentMap(int mapNumber) {
        if (mapNumber == 1 && currentMap == mapOne) {
            currentMap = mapTwo;
        } else if (mapNumber == 2 && currentMap == mapTwo) {
            currentMap = mapThree;
        }
    }

    private int[][] initMapOne() {
        return new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 3, 3},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 67, 55},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 45, 0},
                {0, 54, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 58, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    private int[][] initMapThree() {
        return new int[][]{
                {0, 0, 2, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 6},

                {0, 0, 2, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {0, 0, 2, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {0, 0, 2, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {0, 0, 2, 124, 125, 6, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 3,
                    3, 6, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 3, 3, 124, 125, 3, 3, 6, 2, 3, 3, 3, 3, 3, 16, 16,
                    16, 16, 3, 3, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 2, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 2, 16, 16, 16, 16, 16, 16, 16,
                    16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 2, 135, 136, 137, 67, 56, 16,
                    16, 16, 16, 67, 56, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 2, 135, 136, 137, 6, 0, 57, 124,
                    125, 67, 58, 2, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 2, 135, 136, 137, 6, 0, 0, 124,
                    125, 6, 0, 0, 56, 56, 56, 56, 70, 124, 125, 67, 56, 58},

                {2, 16, 16, 16, 16, 16, 16, 45, 2, 135, 136, 137, 3, 6, 0, 124,
                    125, 6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {54, 56, 70, 124, 125, 67, 56, 58, 2, 16, 16, 16, 16, 6, 0, 124,
                    125, 6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {0, 0, 2, 124, 125, 6, 0, 0, 2, 16, 16, 16, 16, 6, 0, 124, 125,
                    6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {0, 0, 2, 124, 125, 6, 0, 0, 2, 16, 16, 16, 16, 6, 0, 124, 125,
                    6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {0, 0, 2, 124, 125, 6, 0, 0, 2, 16, 16, 16, 16, 6, 0, 124, 125,
                    6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {0, 0, 2, 124, 125, 6, 0, 0, 2, 16, 16, 16, 16, 6, 0, 124, 125,
                    6, 0, 0, 0, 0, 0, 0, 2, 124, 125, 6, 0, 0},

                {2, 3, 3, 124, 125, 3, 3, 6, 54, 56, 56, 56, 56, 3, 3,
                    124, 125, 3, 3, 6, 0, 0, 2, 5, 5, 124, 125, 5, 5, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 2, 16, 16, 16,
                    16, 16, 16, 6, 0, 0, 2, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 3, 3, 3, 3, 3, 3, 16, 16, 16,
                    16, 16, 16, 3, 3, 3, 3, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 114, 114, 114, 114, 114, 114,
                    16, 16, 16, 16, 16, 16, 113, 113, 113, 113, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 127, 127, 127, 127, 127, 127,
                    16, 16, 16, 16, 16, 16, 126, 126, 126, 126, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 67, 55, 55, 55, 55, 70, 16,
                    16, 16, 16, 16, 16, 67, 57, 57, 70, 16, 16, 16, 16, 16, 16, 6},

                {2, 16, 16, 16, 16, 16, 16, 6, 0, 0, 0, 0, 2, 16, 16,
                    16, 16, 16, 16, 6, 0, 0, 2, 16, 16, 16, 16, 16, 16, 6},

                {54, 56, 56, 56, 56, 56, 56, 58, 0, 0, 0, 0, 0, 56, 135,
                    136, 136, 137, 67, 58, 0, 0, 54, 56, 56, 56, 56, 56, 56, 58},

                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136, 136,
                    137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136,
                    136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136,
                    136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 135, 136,
                    136, 137, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 54, 135,
                    136, 136, 137, 58, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }


}
