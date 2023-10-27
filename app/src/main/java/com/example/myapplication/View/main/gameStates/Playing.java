package com.example.myapplication.View.main.gameStates;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.example.myapplication.Model.entities.Character;
import com.example.myapplication.Model.entities.Player.Player;
import com.example.myapplication.Model.entities.Player.playerStates.PlayerStates;
import com.example.myapplication.Model.entities.enemies.AbstractEnemy;
import com.example.myapplication.Model.environments.Doorways.Doorway;
import com.example.myapplication.Model.environments.MapManager;
import com.example.myapplication.Model.helper.GameConstants;
import com.example.myapplication.Model.helper.interfaces.GameStateInterFace;
import com.example.myapplication.Model.helper.playerMoveStartegy.PlayerDash;
import com.example.myapplication.Model.helper.playerMoveStartegy.PlayerIdle;
import com.example.myapplication.Model.helper.playerMoveStartegy.PlayerMoveStrategy;
import com.example.myapplication.Model.helper.playerMoveStartegy.PlayerRun;
import com.example.myapplication.Model.leaderBoard.Leaderboard;
import com.example.myapplication.Model.loopVideo.GameVideos;
import com.example.myapplication.Model.loopVideo.GameAnimation;
import com.example.myapplication.Model.coreLogic.Game;
import com.example.myapplication.Model.ui.PlayingUI;
import com.example.myapplication.ViewModel.gameStatesVideoModel.PlayingViewModel;

import java.util.Random;

public class Playing extends BaseState implements GameStateInterFace {
    private Random rand = new Random();
    private Paint paint = new Paint();
    private MapManager mapManager;
    private float cameraX;
    private float cameraY;
    private boolean movePlayer;
    private boolean playerAbleMove;
    private PointF lastTouchDiff;

    private final PlayingUI playingUI;
    private final Paint hitBoxPaint;
    private boolean doorwayJustPassed;
    private PlayingViewModel viewModel;
    private PlayerMoveStrategy playerMoveStrategy;
    private PlayerMoveStrategy playerRun;
    private PlayerMoveStrategy playerIdle;
    private PlayerMoveStrategy playerDash;
    private float xSpeed;
    private float ySpeed;






    public Playing(Game game, Context context) {
        super(game);
        paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        hitBoxPaint = new Paint();
        hitBoxPaint.setStrokeWidth(1);
        hitBoxPaint.setStyle(Paint.Style.STROKE);
        hitBoxPaint.setColor(Color.RED);

        playerAbleMove = false;
        playerIdle = new PlayerIdle();
        playerRun = new PlayerRun();
        playerDash = new PlayerDash();
        xSpeed = 0;
        ySpeed = 0;

        mapManager = new MapManager(this);
        initCameraValue();
        //itemManager = new ItemManager();
        //mob1Pos = new PointF(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT));

        playingUI = new PlayingUI(this);

        //updateAttackHitbox();

        initPlaying();

        viewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(PlayingViewModel.class);

        viewModel.getLastTouchDiff().observe((LifecycleOwner) context, new Observer<PointF>() {
            @Override
            public void onChanged(PointF touchDiff) {
                lastTouchDiff = touchDiff;
            }
        });
        viewModel.getCameraX().observe((LifecycleOwner) context, new Observer<Float>() {
            @Override
            public void onChanged(Float x) {
                cameraX = x;
            }
        });
        viewModel.getCameraY().observe((LifecycleOwner) context, new Observer<Float>() {
            @Override
            public void onChanged(Float y) {
                cameraY = y;
            }
        });
        viewModel.getIsPlayerAbleMove().observe((LifecycleOwner) context, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean ableMove) {
                playerAbleMove = ableMove;
            }
        });
        viewModel.getCheckingPlayerEnemyCollision().observe((LifecycleOwner) context, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean checking) {
                viewModel.checkAttackByEnemies(Player.getInstance().getHitBox(), mapManager, cameraX, cameraY);
            }
        });
    }

    private void initCameraValue() {
        cameraX = GameConstants.UiSize.GAME_WIDTH / 2 - mapManager.getMaxWidthCurrentMap() / 2;
        cameraY = GameConstants.UiSize.GAME_HEIGHT / 2 - mapManager.getMaxHeightCurrentMap() / 2;
    }

    private void setPlayerMoveStrategy(PlayerMoveStrategy playerMoveStrategy) {
        this.playerMoveStrategy = playerMoveStrategy;
    }

    public void initPlaying() {
        //zombie = new Zombie(new PointF(100, 100));
        //zombie.setActive(true);

        initCameraValue();

    }

    @Override
    public void update(double delta) {
        if (game.getCurrentGameState() != Game.GameState.PLAYING) {
            return;
        }

        updatePlayerMoveInfo(delta);
        if (playerMoveStrategy != null) {
            playerMoveStrategy.setPlayerAnim(xSpeed, ySpeed, lastTouchDiff);
        }
        updatePlayerPosition(delta);

        Player.getInstance().update(delta);

        //updateAttackHitbox();
        mapManager.setCameraValues(cameraX, cameraY);
        checkForDoorway();
        //itemManager.setCameraValues(cameraX, cameraY);


        viewModel.checkAttack(Player.getInstance().isAttacking(), Player.getInstance().getAttackBox(), mapManager, cameraX, cameraY);
        //viewModel.checkAttackByEnemies(Player.getInstance().getHitBox(), mapManager, cameraX, cameraY);
        viewModel.checkingPlayerEnemyCollision();
        viewModel.updateZombies(mapManager, delta, cameraX, cameraY);


        if (Player.getInstance().getCurrentHealth() <= 0) {
            setGameStateToEnd();
        }
    }

    @Override
    public void touchEvents(MotionEvent event) {
        viewModel.playingUiTouchEvent(event, playingUI);
    }
    @Override
    public void render(Canvas c) {
        mapManager.draw(c);
        //itemManager.draw(c);
        Player.getInstance().drawPlayer(c);
        for (AbstractEnemy zombie : mapManager.getCurrentMap().getMobArrayList()) {
            if (zombie.isActive()) {
                drawCharacter(c, zombie);
            }
        }

        viewModel.playingUiDrawUi(c, playingUI);
        drawUi((c));
    }

    public void setCameraValues(PointF cameraPos) {
        this.cameraX = cameraPos.x;
        this.cameraY = cameraPos.y;
    }
    private void checkForDoorway() {
        Doorway doorwayPlayerIsOn = mapManager.isPlayerOnDoorway(Player.getInstance().getHitBox());
        if (doorwayPlayerIsOn != null) {
            if (!doorwayJustPassed) {
                if (doorwayPlayerIsOn.isEndGameDoorway()) {
                    Player.getInstance().setWinTheGame(true);
                    setGameStateToEnd();
                } else {
                    mapManager.changeMap(doorwayPlayerIsOn.getDoorwayConnectedTo(), game);
                }

            }
        } else {
            doorwayJustPassed = false;
        }
    }
    public void setDoorwayJustPassed(boolean doorwayJustPassed) {
        this.doorwayJustPassed = doorwayJustPassed;
    }

//    private void drawPlayer(Canvas c) {
//        c.drawBitmap(//在本游戏中，行数Y是不同形态，而列数X是该姿势中的不同帧。根据不同输入需要调换，其他怪物同理
//                viewModel.getPlayerSprite(Player.getInstance().isAttacking()),
//                viewModel.getPlayerLeft(),
//                viewModel.getPlayerTop(),
//                null
//        );
//        c.drawRect(viewModel.getPlayerHitbox(), hitBoxPaint);
//        if (Player.getInstance().isAttacking()) {
//            Player.getInstance().drawAtk(c);
//        }
//    }

    private void drawUi(Canvas c) {
        c.drawText("PlayerName: " + Player.getInstance().getPlayerName(), 200, 100, paint);
        c.drawText("Difficulty: " + Player.getInstance().getDifficulty(), 200, 150, paint);
        c.drawText("Health: " + Player.getInstance().getCurrentHealth(), 200, 200, paint);
        c.drawText("Game Score:" + Player.getInstance().getCurrentScore(), 200, 250, paint);
    }
    public void drawCharacter(Canvas canvas, Character character) {
        int offsetX = character.getHitBoxOffsetX();
        if (character.getDrawDir() == GameConstants.DrawDir.RIGHT) {
            offsetX = 0;
        }
        canvas.drawBitmap(
                character.getGameCharType().getSprite(
                        character.getDrawDir(),
                        character.getAniIndex()
                ),
                character.getHitBox().left + cameraX - offsetX,
                character.getHitBox().top + cameraY - character.getHitBoxOffSetY(),
                null
        );
        canvas.drawRect(
                character.getHitBox().left + cameraX,
                character.getHitBox().top + cameraY,
                character.getHitBox().right + cameraX,
                character.getHitBox().bottom + cameraY,
                hitBoxPaint); //draw mob's hitBox
    }



    private void updatePlayerMoveInfo(double delta) {
        if (!movePlayer) {
            setPlayerMoveStrategy(playerIdle);
            return;
        }
        setPlayerMoveStrategy(playerRun);


        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);
        double angle = Math.atan(ratio); //找到玩家滑动的角度

        xSpeed = (float) Math.cos(angle); //用角度与直线速度计算斜向速度
        ySpeed = (float) Math.sin(angle);


        if (lastTouchDiff.x < 0) {
            xSpeed *= -1;
        }
        if (lastTouchDiff.y < 0) {
            ySpeed *= -1;
        }
        int pWidth = (int) Player.getInstance().getHitBox().width(); //计算player的实际碰撞体积
        int pHeight = (int) Player.getInstance().getHitBox().height();

        if (xSpeed <= 0) { //当角色往左边或上边移动时，判定点为左上角，则将碰撞修正设置为0
            pWidth = 0;
        }
        //if (ySpeed <= 0) {
        //    pHeight = 0;
        //}
        float baseSpeed = (float) delta * Player.getInstance().getCurrentSpeed();
        float deltaX = xSpeed * baseSpeed * -1; //移动镜头而不是角色
        float deltaY = ySpeed * baseSpeed * -1; //因镜头需与角色相反的方向移动，即乘以-1


        viewModel.setIsPlayerAbleMove(
                viewModel.checkPlayerAbleMove(
                        Player.getInstance().isAttacking(),
                        mapManager,
                        pWidth,
                        pHeight,
                        new PointF(deltaX, deltaY),
                        new PointF(cameraX, cameraY)
                ));


    }

    private void updatePlayerPosition(double delta) {
        if (playerAbleMove) {
            float speed = Player.getInstance().getCurrentSpeed();
            cameraX += playerMoveStrategy.playerMovement(xSpeed, ySpeed, (float) delta * speed).x;
            cameraY += playerMoveStrategy.playerMovement(xSpeed, ySpeed, (float) delta * speed).y;
        }

    }

    public void setGameStateToMenu() {
        game.setCurrentGameState(Game.GameState.MENU);
    }
    public void setGameStateToEnd() {
        //Leaderboard.getInstance().addPlayerRecord(player.sumbitScore());
        Leaderboard.getInstance().addPlayerRecord(
                Player.getInstance().sumbitScore(),
                Player.getInstance().isWinTheGame()
        );

        movePlayer = false;
        mapManager.resetMap();
        game.setCurrentGameState(Game.GameState.END);
    }

    public void setPlayerMoveTrue(PointF lastTouchDiff) { //查看是否应该移动角色（触发移动后没有松开光标）
        movePlayer = true;
        viewModel.setLastTouchDiff(lastTouchDiff);
    }
    public void setPlayerMoveFalse() {
        movePlayer = false; //在操作板class中，松开光标/键盘后将角色移动设置为false，即停止角色移动
        Player.getInstance().backToIdleState();
        Player.getInstance().resetAnimation();
    }


    public MapManager getMapManager() {
        return mapManager;
    }

}