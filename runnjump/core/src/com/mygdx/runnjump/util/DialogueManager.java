package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DialogueManager extends Manager implements Disposable {

    static DialogueManager dialogueManager;
    String currentDialogue;
    /**
     * A map containing a list of text messages under a name.
     */
    private HashMap<String, ArrayList<String>> dialogueMap;

    /**
     * this is the factory method for getting the music manager
     * @return
     */
    public static DialogueManager getManager(){
        if (dialogueManager == null){
            dialogueManager = new DialogueManager();
        }
        return dialogueManager;
    }
    /**
     * Instantiates a new MusicManager. Private so its only created once using the factory method.
     */
    private DialogueManager() {
        currentDialogue = null;
        dialogueMap = new HashMap<>();
    }

    /**
     * This method parses the dialogue files, they should have each 'dialogue screen' on one line.
     * The character limit for each screen is (todo figure out char limit)
     * @param name name of the conversation/dialogue to be stored
     * @param path path to the file containing the dialogue
     */
    @Override
    public void addAsset(String name, String path) {
        FileHandle handle = Gdx.files.internal("dialogue/" + path);
        String theText = handle.readString();
        ArrayList<String> textScreens = new ArrayList<String>(Arrays.asList(theText.split("\\r?\\n")));

        dialogueMap.put(name, textScreens);
    }

    public String getDialogue(String name, int screenNum){
        return dialogueMap.get(name).get(screenNum-1);
    }

    /**
     * Similar to addAsset(String name, String path) but instead takes in assets from multiple files.
     * @param name name of the conversation/dialogue to be stored
     * @param paths paths to the files containing the dialogue
     */
    @Override
    public void addAssetSet(String name, String[] paths) {
        //todo or delete maybe
    }

    @Override
    public void dispose() {

    }
}
