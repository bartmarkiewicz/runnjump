package com.mygdx.runnjump;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.screens.HighScoresScreen;
import com.mygdx.runnjump.screens.LevelScreen;
import com.mygdx.runnjump.screens.LoadingScreen;
import com.mygdx.runnjump.screens.MenuScreen;
import com.mygdx.runnjump.util.AudioManager;
import com.mygdx.runnjump.util.DialogueManager;
import com.mygdx.runnjump.util.MusicManager;
import com.mygdx.runnjump.util.SoundHandler;
import com.mygdx.runnjump.util.SoundManager;
import com.mygdx.runnjump.util.TextureManager;

import java.util.TreeMap;

/**
 * This is the actual ‘main’ class of the aplication. It stores all the managers. So they can easily be accessed from the entire app.
 */
public class Runnjump extends Game {
	/**
	 * The Sound manager.
	 */
	//Texture campaignBt,survivalBt, highScoresBt;
	public SoundManager soundManager;
	/**
	 * The Music manager.
	 */
	public MusicManager musicManager;
	/**
	 * The Texture manager.
	 */
	public TextureManager textureManager;

	public DialogueManager dialogueManager;
	/**
	 * The Texture atlas map.
	 */
	public static TreeMap<String, TextureAtlas> textureAtlasMap;

	/**
	 * The enum Screen en.
	 */
	public enum ScreenEn {
		/**
		 * Loading screen en.
		 */
		LOADING,
		/**
		 * Menu screen en.
		 */
		MENU,
		/**
		 * Game screen en.
		 */
		GAME,
		/**
		 * Level screen en.
		 */
		LEVEL,
		/**
		 * Highscores screen en.
		 */
		HIGHSCORES,
	}

	/**
	 * The constant currentScreen.
	 */
	public static ScreenEn currentScreen;
	/**
	 * The constant previousScreen.
	 */
	public static ScreenEn previousScreen;//todo change to a stack of screens?

	/**
	 * The Sound bt.
	 */
	public ImageButton soundBt;
	private MenuScreen menuScreen;
	private LoadingScreen loadingScreen;
	private GameScreen gameScreen;
	private LevelScreen levelScreen;
	private HighScoresScreen highScoresScreen;
	private static int levelSelected;

	/**
	 * this sets the level selected by the levelscreen. So it can be accessed from the game screen.
	 *
	 * @param level the level
	 */
	public static void setLevelSelected(int level){
		levelSelected = level;
	}

	public static int getLevelSelected(){
		if(previousScreen == ScreenEn.MENU)
		{
			return -1;
		}
		return levelSelected;
	}

	/**
	 * creates the app-wide elements such as the managers and the sound button.
	 */
	@Override
	public void create () {
		currentScreen = ScreenEn.LOADING;
		previousScreen = ScreenEn.LOADING;
		loadingScreen = new LoadingScreen(this);
		soundManager = SoundManager.getManager();
		musicManager = MusicManager.getManager();
		textureManager = TextureManager.getManager();
		dialogueManager = DialogueManager.getManager();
		textureAtlasMap = new TreeMap<>();
		setScreen(loadingScreen);
		Drawable soundOnIcon;
		Texture soundTexture;
		Texture soundOff;
		Drawable soundOffIcon;

		soundTexture = new Texture(Gdx.files.internal("sound.png"));
		soundOnIcon = new TextureRegionDrawable(new TextureRegion(soundTexture));
		soundOff = new Texture(Gdx.files.internal("no-sound.png"));
		soundOffIcon = new TextureRegionDrawable(new TextureRegionDrawable(soundOff));
		soundBt = new ImageButton(soundOnIcon, soundOffIcon, soundOffIcon);
		soundBt.addListener(new SoundHandler(this));

    }


	/**
	 * Switches to the screen specified by the enum screenId
	 *
	 * @param screenId - the screen to switch to.
	 */
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
					if (previousScreen == ScreenEn.LEVEL) {
						//campaign mode
						gameScreen = new GameScreen(this, levelScreen.getLevelSelected());
					} else {
						//survival mode
						gameScreen = new GameScreen(this);
					}
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

	public Screen getCurrentScreen(){
		return this.getScreen();
	}

	/**
	 * draws
	 */
	@Override
	public void render () {
		super.render();
		//rendering happens here
	}

	/**
	 *prevent memory leaks by disposing all game elements with are disposable.
	 */
	@Override
	public void dispose () {
		super.dispose();
		highScoresScreen.dispose();
		soundManager.dispose();
		textureManager.dispose();
		musicManager.dispose();

		loadingScreen.dispose();
		highScoresScreen.dispose();
		levelScreen.dispose();
		gameScreen.dispose();
		menuScreen.dispose();
	}
}
