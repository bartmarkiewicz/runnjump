package com.mygdx.runnjump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	private MenuScreen menuScreen;
	private LoadingScreen loadingScreen;
	private GameScreen gameScreen;
	private LevelScreen levelScreen;
	private HighScoresScreen highScoresScreen;

	@Override
	public void create () {
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);


		/*batch = new SpriteBatch();
		campaignBt = new Texture("campaign_bt.png");
        survivalBt = new Texture("survival_bt.png");
        highScoresBt = new Texture("high_scores_bt.png");
		*/
    }

    public void changeScreen(ScreenEn screenId){
		switch (screenId){
			case MENU:
				if (menuScreen == null){
					menuScreen = new MenuScreen(this);
					this.setScreen(menuScreen);
				}
				break;
			case GAME:
				if (gameScreen == null){
					gameScreen = new GameScreen(this);
					this.setScreen(gameScreen);
				}
				break;
			case LEVEL:
				if (levelScreen == null){
					levelScreen = new LevelScreen(this);
					this.setScreen(levelScreen);
				}
				break;
			case HIGHSCORES:
				if (highScoresScreen == null){
					highScoresScreen = new HighScoresScreen(this);
					this.setScreen(highScoresScreen);
				}
				break;
		}
	}

	@Override
	public void render () {
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
