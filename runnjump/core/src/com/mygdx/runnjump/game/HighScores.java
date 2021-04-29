package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class is used to save, load and get highscores.
 */
public class HighScores {
    public HashMap<String, ArrayList<HighScore>> highScores; //HashMap<PLAYERNAME, scores>()

    public HighScores() {
        highScores = new HashMap<>();
        loadHighScores();
        System.out.println(highScores);
    }


    /**
     * Adds a high score to the highscore HashMap and saves it to preferences(android) or CSV file on Windows
     * @param name
     * @param survival
     * @param score
     */
    public void addHighScore(String name, boolean survival, int score){
        HashMap<String, Integer> playerScores = new HashMap<>();
        String gameMode;
        if(survival){
            gameMode = "survival";
        } else {
            gameMode = "campaign";
        }
        boolean highestScore = true;
        if (highScores.containsKey(name)) {

            for(int i = 0; i < highScores.get(name).size(); i++){
                if (highScores.get(name).get(i).gameMode.equals(gameMode) && highScores.get(name).get(i).score > score){
                    highestScore = false;
                }
            }

            if(highestScore) {
                highScores.get(name).add(new HighScore(name,gameMode, score));
                saveHighScores();

            }
        } else {
            ArrayList<HighScore> listOfScores = new ArrayList<>();
            listOfScores.add(new HighScore(name,gameMode,score));
            highScores.put(name, listOfScores);
            saveHighScores();
        }

    }

    /**
     * Saves the highscores from the HashMap onto a csv file (windows) or a preferences file
     * on Android.
     */
    private void saveHighScores() {
        String fileStr = "";
        FileHandle file = null;
        Preferences prefs = Gdx.app.getPreferences("prefs");

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            fileStr = prefs.getString("highscoreData", "");
        } else {
            file = Gdx.files.local("highscores.csv");
            fileStr = file.readString();
        }

        String outputStr = fileStr;
        for(String player: highScores.keySet()){
            for(int i = 0; i < highScores.get(player).size(); i++) {
                if(fileStr.contains(player + "," + highScores.get(player).get(i).gameMode + "," + highScores.get(player).get(i).score+"\n")){
                    continue;
                }else {
                    if (Gdx.app.getType() == Application.ApplicationType.Android) {
                        outputStr += player + "," + highScores.get(player).get(i).gameMode + "," + highScores.get(player).get(i).score + "\n";
                    } else {
                        file.writeString(player + "," + highScores.get(player).get(i).gameMode + "," + highScores.get(player).get(i).score + "\n", true);
                    }
                }
            }
        }
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            prefs.putString("highscoreData", outputStr);
            prefs.flush();
        }


       // System.out.println(Gdx.files.getLocalStoragePath());

        //System.out.println(file.readString());
    }

    /**
     * Gets the scores for a particular gamemode.(in descending order - like a leaderboard)
     * @param gamemode
     * @return
     */
    public ArrayList<HighScore> getHighScores(String gamemode){
        ArrayList<HighScore> sortedHighscores = new ArrayList<>();
        for(String name: highScores.keySet()){
            for(int i = 0; i < highScores.get(name).size(); i++){
                if(highScores.get(name).get(i).gameMode.equals(gamemode)){
                    sortedHighscores.add(highScores.get(name).get(i));
                }
            }
        }

        Collections.sort(sortedHighscores);
        return sortedHighscores;
    }

    /**
     * Loads the highscores from a CSV file or preferences.
     */
    public void loadHighScores() {
        String fileStr = "";
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Preferences prefs = Gdx.app.getPreferences("prefs");
            fileStr = prefs.getString("highscoreData", "");
        } else {
            FileHandle file = Gdx.files.local("highscores.csv");
            fileStr = file.readString();
        }
        String[] records = fileStr.split("\n");
        if(fileStr.equals("")){
            return;
        }
        for(String record: records){
            String[] parts = record.split(",");
            boolean foundScore = false;
            if(highScores.containsKey(parts[0])) {
                for(int i = 0; i <highScores.get(record.split(",")[0]).size(); i++ ) {
                    if (highScores.get(parts[0]).get(i).score == Integer.parseInt(parts[2]) &&
                            highScores.get(parts[0]).get(i).gameMode.equals(parts[1])) {
                        foundScore = true;
                    }
                }
                if(!foundScore) {
                    highScores.get(parts[0]).add(new HighScore(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            } else {
                ArrayList<HighScore> scores = new ArrayList<>();
                scores.add(new HighScore(parts[0], parts[1], Integer.parseInt(parts[2])));
                highScores.put(parts[0],scores);
            }
        }
    }

    /**
     * Class which represents a single high score for a certain game mode.
     */
    public class HighScore implements Comparable<HighScore> {
        public String gameMode;
        public int score;
        public String name;
        public HighScore(String name, String gameMode, int score){
            this.gameMode = gameMode;
            this.name = name;
            this.score = score;
        }


        /**
         * Comparator to allow the sorting of highscores by their score.
         * @param other
         * @return
         */
        @Override
        public int compareTo(HighScore other) {
            if(other.gameMode.equals(this.gameMode) && other.score < this.score){
                return -1;
            } else if(other.gameMode.equals(this.gameMode) && other.score > this.score) {
                return 1;
            } else {
                return 0;
            }

        }
    }

}
