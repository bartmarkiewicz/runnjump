package com.mygdx.runnjump.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.runnjump.entity.Player;
import com.mygdx.runnjump.util.ColorDrawable;

/**
 * This class represents an NPC shop.
 */
public class Shop extends Table {
    public Player player;
    Label storeLabel;
    Skin skin;
    public Shop(Skin skin) {
        super(skin);
        storeLabel = new Label("The Merchant's Store", skin);
        storeLabel.setFontScale(2f);
        this.add(storeLabel).colspan(3);
        this.row();
        this.skin = skin;
        this.player = null;

        addItemToStore("Lower Gravity", "3");
        addItemToStore("Rock-throwing", "6");
        addItemToStore("Super-speed", "3");
        addItemToStore("Ghost-walk", "15");
        addItemToStore("Invincibility", "3");
        addItemToStore("Silver Key", "8");

        row();
        TextButton closeBt = new TextButton("Close Store", skin);
        closeBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                hideShop();
            }
        });
        row();
        this.add(closeBt).colspan(3);
        setBackground(new ColorDrawable(55,55,55,0.3f));
    }

    /**
     * Sets the player reference
     * @param player
     */
    public void setPlayer(Player player){
        this.player = player;
    }

    /**
     * Shows the store
     */
    public void showShop(){
        this.setVisible(true);
    }

    /**
     * Hides the store
     */
    public void hideShop(){
        this.setVisible(false);
        player.setDialogueMode(false);
    }

    /**
     * Adds an item to the store.
     * @param name
     * @param price
     */
    public void addItemToStore(final String name, final String price){
        final Label powerUpLabel = new Label(name,skin);
        final TextButton buyBt = new TextButton("Buy for " + price + " points", skin);

        buyBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                buy(name,price);


            }
        });



        this.add(powerUpLabel).colspan(1);
        this.add(buyBt).colspan(2);
        row();



    }

    /**
     * This method handles the buying of an item from the store.
     * @param name
     * @param price
     */
    private void buy(String name, String price) {
        if(player.getInventory().getScore() >= Integer.parseInt(price)){
            if (name.equals("Lower Gravity")){
                player.getInventory().gainPowerUp("gravity");
                player.loseScore(Integer.parseInt(price));
            }
            if (name.equals("Rock-throwing")){
                player.getInventory().gainPowerUp("rocks");
                player.loseScore(Integer.parseInt(price));
            }
            if (name.equals("Super-speed")){
                player.getInventory().gainPowerUp("speed");
                player.loseScore(Integer.parseInt(price));
            }
            if (name.equals("Ghost-walk")){
                player.getInventory().gainPowerUp("ghostwalk");
                player.loseScore(Integer.parseInt(price));
            }
            if (name.equals("Invincibility")){
                player.getInventory().gainPowerUp("invincibility");
                player.loseScore(Integer.parseInt(price));
            }
            if(name.equals("Silver Key")){
                player.handleKey("silver_key");
                player.loseScore(Integer.parseInt(price));
            }
        }
    }


}
