package com.mygdx.runnjump.game;

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


    public int getLives(){
        return hearts;
    }

    public int getScore(){
        return score;
    }

    public void restart(){
        this.hearts = STARTING_HEARTS;
        score = 0;
        hud.setScore(score);
        hud.setLives(hearts);
        createPowerUps();
    }

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
        if(debug == false) {
            powerUps.put("gravity", 0);
            powerUps.put("speed", 0);
            powerUps.put("invincibility", 0);
            powerUps.put("ghostwalk", 0);
            powerUps.put("rocks", 0);
        } else {
            powerUps.put("gravity", 1);
            powerUps.put("speed", 1);
            powerUps.put("invincibility", 1);
            powerUps.put("ghostwalk", 2);
            powerUps.put("rocks", 55);
        }

        hud.updatePowerUpIndicator(getPowerUps());

    }


    /**
     * Creates the power up inventory.
     * @param debug
     */
    public Inventory(Hud hud, boolean debug) {
        powerUps = new HashMap<>();
        this.hud = hud;
        this.debug = debug;
        //createPowerUps();
        restart();
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
        hud.changed(null, null);
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
}
