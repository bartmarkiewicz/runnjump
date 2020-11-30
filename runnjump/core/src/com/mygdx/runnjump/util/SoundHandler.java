package com.mygdx.runnjump.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.runnjump.Runnjump;

import javax.swing.event.ChangeEvent;

public class SoundHandler extends ChangeListener{
    //class for handling the input for the sound button
    ImageButton soundBt;
    Runnjump theGame;
    public SoundHandler(Runnjump theGame){
        this.theGame = theGame;
        soundBt = theGame.soundBt;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        if (!soundBt.isChecked()) {
            soundBt.setChecked(false);
            System.out.println("Checked false");

        } else {
            soundBt.setChecked(true);
            System.out.println("Checked true");
        }
        theGame.soundManager.muteSound();

        soundBt.invalidate();
    }
}
