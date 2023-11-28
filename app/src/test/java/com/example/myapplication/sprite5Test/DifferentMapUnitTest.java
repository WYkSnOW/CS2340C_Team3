package com.example.myapplication.sprite5Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.Model.environments.InitMap;
import com.example.myapplication.Model.helper.GameConstants;
import com.example.myapplication.Model.helper.HelpMethods;
import com.example.myapplication.Model.ui.playingUI.PlayingUI;

import org.junit.Test;


public class DifferentMapUnitTest {

    //Tests if different maps are used in the game and that they are changed when player moves - Nikhil
    @Test
    public void differentMapUnitTest() {

        assertNotEquals(InitMap.initMapOne(), InitMap.initMapThree());
        assertNotEquals(InitMap.initMapOne(), InitMap.initMapTwo());
        assertNotEquals(InitMap.initMapTwo(), InitMap.initMapThree());
    }
}
