package com.mygdx.runnjump.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

/**
 * This class allows me to create simple color backgrounds based on the RGBA value provided.
 */
public class ColorDrawable extends BaseDrawable {

    private float red, green, blue, alpha;
    private Color color = new Color();
    TextureRegion bg;

    public ColorDrawable(float red, float green, float blue, float alpha) {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        bg = new TextureRegion(new Texture("whitebg.png"));

    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        color.set(batch.getColor());
        batch.setColor(red,green,blue,alpha);

        batch.draw(bg, x, y, width ,height);

        batch.setColor(color);
    }
}
