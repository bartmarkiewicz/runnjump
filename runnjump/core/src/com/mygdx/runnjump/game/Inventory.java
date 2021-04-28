package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.runnjump.screens.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * This class represents the player's inventory.
 */
public class Inventory {
    HashMap<String, Integer> powerUps;
    boolean debug;
    Hud hud;
    private int hearts = 0, score = 0;
    protected final int STARTING_HEARTS = 3;
    private boolean survival;


    public int getLives(){
        return hearts;
    }

    public int getScore(){
        return score;
    }

    /**
     * Restarts the inventory
     */
    public void restart(boolean survival){
        this.hearts = STARTING_HEARTS;
        score = 0;
        hud.setScore(score);
        hud.setLives(hearts);
        this.survival = survival;
        createPowerUps();

    }

    /**
     * Adds the specified amount of score to the player
     * @param amount
     */
    public void addScore(int amount){
        score = score + amount;
        hud.setScore(score);
    }

    public void addLives(int amount){
        hearts = hearts + amount;
        hud.setLives(hearts);

    }

    public void removeLife(){
        hearts = hearts -1;
        hud.setLives(hearts);

    }

    /**
     * Populates the hash map of power ups.
     */
    public void createPowerUps(){
        if(debug) {
            powerUps.put("gravity", 55);
            powerUps.put("speed", 55);
            powerUps.put("invincibility", 55);
            powerUps.put("ghostwalk", 55);
            powerUps.put("rocks", 55);
        } else if(survival){
            powerUps.put("gravity", 3);
            powerUps.put("speed", 3);
            powerUps.put("invincibility", 3);
            powerUps.put("ghostwalk", 3);
            powerUps.put("rocks", 3);
        } else {
            powerUps.put("gravity", 0);
            powerUps.put("speed", 0);
            powerUps.put("invincibility", 0);
            powerUps.put("ghostwalk", 0);
            powerUps.put("rocks", 0);
        }

        if(!survival){
            loadGameData();
        }



        hud.updatePowerUpIndicator(getPowerUps());

    }

    private void loadGameData() {
        Preferences prefs = Gdx.app.getPreferences("prefs");
        this.score = prefs.getInteger("score", score);
        this.hearts = prefs.getInteger("lives", getLives());
        for(String powerUp:  powerUps.keySet()){
            int powerUpCount = prefs.getInteger(powerUp, powerUps.get(powerUp));
            powerUps.put(powerUp,powerUpCount);
        }
    }


    /**
     * Creates the power up inventory.
     * @param debug
     */
    public Inventory(Hud hud, boolean debug, boolean survival) {
        powerUps = new HashMap<>();
        this.hud = hud;
        this.debug = debug;
        //createPowerUps();
        this.survival = survival;
        restart(survival);
    }

    /**
     * Gets the power ups currently posessed by the player.
     */
    public HashMap<String, Integer> getPowerUps(){
        HashMap<String,Integer> newMap = new HashMap<>();
        for(String each : powerUps.keySet()){
            if(powerUps.get(each) >= 0){
                newMap.put(each, powerUps.get(each));
            }
        }
        return newMap;
    }

    /**
     * Adds a single power up use to inventory.
     * @param name
     */
    public void gainPowerUp(String name){
        int previous = powerUps.get(name);
        powerUps.put(name, previous+1);
        hud.updatePowerUpIndicator(getPowerUps());
    }

    /**
     * Removes/uses a single power up from the inventory.
     * @param name
     */
    public void usePowerUp(String name){
        int previous = powerUps.get(name);
        if(previous > 0) {
            powerUps.put(name, previous - 1);
        }
        hud.updatePowerUpIndicator(getPowerUps());

    }

    /**
     * Checks if there is a power up with that name in the inventory.
     * @param name
     * @return
     */
    public boolean hasPowerUp(String name){
        if(powerUps.get(name) > 0){
            return true;
        } else {
            return false;
        }
    }

    public void loseScore(int i) {
        this.score -= i;
        hud.setScore(score);
    }
}
