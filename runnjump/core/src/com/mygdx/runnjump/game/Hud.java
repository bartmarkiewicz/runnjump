package com.mygdx.runnjump.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud implements Disposable {
    Stage stage;
    Viewport viewport;

    public Hud(){
        Stage stage = new Stage();
    }
    @Override
    public void dispose() {
        stage.dispose();

    }
}
