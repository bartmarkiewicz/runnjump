package com.mygdx.runnjump;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.screens.HighScoresScreen;
import com.mygdx.runnjump.screens.LevelScreen;
import com.mygdx.runnjump.screens.LoadingScreen;
import com.mygdx.runnjump.screens.MenuScreen;

public class Runnjump extends Game {
	//SpriteBatch batch;
	//Texture campaignBt,survivalBt, highScoresBt;
	Music bgMusic;

	public enum ScreenEn {
		LOADING,
		MENU,
		GAME,
		LEVEL,
		HIGHSCORES,
	}
	public static ScreenEn currentScreen;
	public static ScreenEn previousScreen;


	private MenuScreen menuScreen;
	private LoadingScreen loadingScreen;
	private GameScreen gameScreen;
	private LevelScreen levelScreen;
	private HighScoresScreen highScoresScreen;

	@Override
	public void create () {
		currentScreen = ScreenEn.LOADING;
		previousScreen = ScreenEn.LOADING;
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);
		//bgMusic = Gdx.audio.newMusic(//todo get a music file);
		//bgMusic.setLooping(true);
		//bgMusic.play();

    }
    

    public void changeScreen(ScreenEn screenId){
		//method used for changing game screens
		previousScreen = currentScreen;

		switch (screenId){
			case MENU:
				if (menuScreen == null) {
					menuScreen = new MenuScreen(this);
				}
				this.setScreen(menuScreen);
				currentScreen = ScreenEn.MENU;


				break;
			case GAME:
				if (gameScreen == null) {
					gameScreen = new GameScreen(this);
				}
				this.setScreen(gameScreen);
				currentScreen = ScreenEn.GAME;

				break;
			case LEVEL:
				if (levelScreen == null) {
					levelScreen = new LevelScreen(this);
				}
				this.setScreen(levelScreen);
				currentScreen = ScreenEn.LEVEL;

				break;
			case HIGHSCORES:
				if (highScoresScreen == null) {
					highScoresScreen = new HighScoresScreen(this);
				}
				this.setScreen(highScoresScreen);
				currentScreen = ScreenEn.HIGHSCORES;
				break;
		}
	}



	@Override
	public void render () {
		super.render();
		//rendering happens here
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
