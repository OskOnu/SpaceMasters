package com.empireyard.spacemasters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.empireyard.spacemasters.gameplay.GameStateManager;
import com.empireyard.spacemasters.handlers.AssetHandler;
import com.empireyard.spacemasters.menu.MainMenu;
import com.empireyard.spacemasters.tools.PlayServices;

public class SpaceMasters extends Game {

	public static String message;

	public static final String TITLE = "Space Masters";

	public static GameStateManager gameStateManager;

	private static PlayServices playServices; //Google Play Services

	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 480;
	public static final float PIXEL_PER_METER = 100;

	public static final short NOTHING_BIT = 0;
	public static final short SCREEN_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short REMOTE_PLAYER_BIT = 4;
	public static final short ENEMY_BIT = 8;
	public static final short PLAYER_BEAM_BIT = 16;
	public static final short REMOTE_PLAYER_BEAM_BIT = 32;
	public static final short ENEMY_BEAM_BIT = 64;

	boolean multiplayer;


	SpriteBatch spriteBatch;
	//private ShapeRenderer shapeRenderer;
	AssetHandler assetHandler;

	public Music music;
	private boolean soundPlaying;


	public SpaceMasters(PlayServices playServices){
		this.playServices = playServices;
	}


	float screenHeightRatio;
	float screenWidthRatio;

	@Override
	public void create () {
		gameStateManager = new GameStateManager();

		spriteBatch = new SpriteBatch();
		//shapeRenderer = new ShapeRenderer();
		assetHandler = new AssetHandler();

		soundPlaying = true;
		music = assetHandler.getMusic("music1");
		music.setLooping(true);
		music.setVolume(0.5f);

		multiplayer = false;

		setScreen(new MainMenu(this));
	}

	public static String getMessage() {
		return message;
	}

	public static void setMessage(String message) {
		SpaceMasters.message = message;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public AssetHandler getAssetHandler() {
		return assetHandler;
	}

	public void setAssetHandler(AssetHandler assetHandler) {
		this.assetHandler = assetHandler;
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public void setSpriteBatch(SpriteBatch spriteBatch) {
		this.spriteBatch = spriteBatch;
	}

	public static PlayServices getPlayServices() {
		return playServices;
	}

	public void setPlayServices(PlayServices playServices) {
		this.playServices = playServices;
	}

	public float getScreenWidthRatio() {
		return screenWidthRatio;
	}
	public void setScreenWidthRatio(float screenWidthRatio) {
		this.screenWidthRatio = screenWidthRatio;
	}

	public float getScreenHeightRatio() {
		return screenHeightRatio;
	}

	public void setScreenHeightRatio(float screenHeightRatio) {
		this.screenHeightRatio = screenHeightRatio;
	}

	public boolean isSoundPlaying() {
		return soundPlaying;
	}

	public void setSoundPlaying(boolean soundPlaying) {
		this.soundPlaying = soundPlaying;
	}

	public void update(){
		if(soundPlaying == true){
			music.play();
		}else {
			music.stop();
		}

	}

	public boolean isMultiplayer() {
		return multiplayer;
	}

	public void setMultiplayer(boolean multiplayer) {
		this.multiplayer = multiplayer;
	}

	@Override
	public void render () {
		super.render();

	}

	public enum GamePlayState{
		PLAYER_DEAD, PLAYER_WON,  ENEMY_DEAD,
		QUIT,
		NONE
	}



	@Override
	public void dispose () {
		spriteBatch.dispose();
		assetHandler.dispose();
		music.dispose();
	}
}
