package com.example.myapplication;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer timer;
    private boolean isZombieFlip;
    /** @noinspection checkstyle:VisibilityModifier, checkstyle:VisibilityModifier */
    private static MediaPlayer openBGM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set and start background music
        Uri uriBGM = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.open_background_bgm);
        openBGM = MediaPlayer.create(this, uriBGM);
        openBGM.start();

        //set up background video
        VideoView backVideo = (VideoView) findViewById(R.id.MainBackground);
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.knight_backgroung;
        backVideo.setVideoURI(Uri.parse(uri));
        backVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start(); //
            }
        });

        //Animation of a running zombie
        ImageView zombie = findViewById(R.id.runningCharacter);
        AnimationDrawable runningZombie = (AnimationDrawable) zombie.getBackground();
        runningZombie.start();

        //A timer that fit the character every 10 second(10000ms)
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isZombieFlip) {
                    zombie.setScaleX(1f);
                    isZombieFlip = false;
                } else {
                    zombie.setScaleX(-1f);
                    isZombieFlip = true;
                }
            }
        }, 10000, 10000);

        //Create animation of moving imageView from 0f to 2000f and 2000f to 0f
        ObjectAnimator moveForward = ObjectAnimator.ofFloat(zombie, "translationX", 0f, 2000f); //move from 0f to 2000f in x
        moveForward.setDuration(10000); // take 10000ms (10 second to do)
        ObjectAnimator moveBackward = ObjectAnimator.ofFloat(zombie, "translationX", 2000f, 0f); // move from 2000f to 0f in x
        moveBackward.setDuration(10000); // take 10000ms (10 second to do)

        // A Animatorset that contain two of the previous animation
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveForward, moveBackward);

        //loop those two animation(replay when it's end)
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.start(); //restart
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) { }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) { }
        });
        animatorSet.start(); //start animation

        //Button that take user to next activity
        Button button = findViewById(R.id.goes_to_congifScreen);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ConfigScreen.class);
                startActivity(intent);
                timer.cancel();
                finish();
            }
        });

        //Button that close the current activity and left the game.
        Button exitBtn = findViewById(R.id.Exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Function that can be call in other class to end the background music
    public static void endMusic() {
        if (openBGM != null && openBGM.isPlaying()) {
            openBGM.stop();
            openBGM.release();
        }
    }
}