package com.mygdx.runnjump;

import com.badlogic.gdx.Game;

import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.screens.HighScoresScreen;
import com.mygdx.runnjump.screens.LevelScreen;
import com.mygdx.runnjump.screens.LoadingScreen;
import com.mygdx.runnjump.screens.MenuScreen;

public class Runnjump extends Game {
	//SpriteBatch batch;
	//Texture campaignBt,survivalBt, highScoresBt;


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

		//Gdx.input.setCatchKey(Input.Keys.BACK, true);
		//Gdx.input.setInputProcessor(this);
		/*batch = new SpriteBatch();
		campaignBt = new Texture("campaign_bt.png");
        survivalBt = new Texture("survival_bt.png");
        highScoresBt = new Texture("high_scores_bt.png");
		*/
    }

    public void changeScreen(ScreenEn screenId){
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
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(campaignBt, 0, 0);
		batch.draw(survivalBt, 0, 100);
		batch.draw(highScoresBt, 0, 200);
		batch.end();*/
	}
	
	@Override
	public void dispose () {
		/*batch.dispose();
		campaignBt.dispose();
		survivalBt.dispose();
		highScoresBt.dispose(); */
	}
}
