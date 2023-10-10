package com.example.myapplication.Model.gameStatesLogic;

import android.view.MotionEvent;

import com.example.myapplication.Model.entities.GameCharacters;
import com.example.myapplication.Model.entities.Player.Player;
import com.example.myapplication.Model.loopVideo.VideoFrame;

public class ConfigLogic {
    //check if player put in a in valid name
    public boolean isNameValid(String name) {
        if (name == null) {
            return false;
        }
        if (name.equals("")) {
            return false;
        }
        boolean r = false;
        for (int i = 0; i < name.length(); i++) {
            if (!(name.charAt(i) == ' ')) {
                r = true;
            }
        }
        if (name.charAt(0) == ' ') {
            r = false;
        }
        return r;
    }
    public boolean ableStart(int characterChoice, int difficultyChoice, boolean validName) {
        return characterChoice > 0 && difficultyChoice > 0 && validName;
    }
    public boolean isInCharacter(MotionEvent e, VideoFrame b) {
        return b.getHitbox().contains(e.getX(), e.getY());
    }
    public void initPlayerCharacter(int characterChoice) {
        if (characterChoice == 1) {
            //game.getPlaying().setPlayer(new Player(GameCharacters.TERESA));
            Player.getInstance().setCharacterChoice(GameCharacters.TERESA);
        } else if (characterChoice == 2) {
            //game.getPlaying().setPlayer(new Player(GameCharacters.WITCH));
            Player.getInstance().setCharacterChoice(GameCharacters.WITCH);
        } else if (characterChoice == 3) {
            //game.getPlaying().setPlayer(new Player(GameCharacters.WARRIOR));
            Player.getInstance().setCharacterChoice(GameCharacters.WARRIOR);
        }
    }
    public int loopDifficultyChoice(int difficultyChoice) {
        if (difficultyChoice < 3) {
            return difficultyChoice + 1;
        }
        return 1;
    }


}
