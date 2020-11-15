package com.mygdx.runnjump.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import javax.swing.event.ChangeEvent;

public class SoundHandler extends ChangeListener{
    //class for handling the input for the sound button
    ImageButton soundBt;
    public SoundHandler(ImageButton button){
        this.soundBt = button;
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
        //soundBt.reset();
        soundBt.invalidate();
    }
}
