package com.empireyard.spacemasters.handlers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by osk on 26.01.17.
 */

public class AssetHandler {
    private AssetManager assetManager;

    public AssetHandler() {
        assetManager = new AssetManager();

        //menu music
        assetManager.load("audio/music/music1.ogg", Music.class);

        //gameplay sounds
        assetManager.load("audio/sound/engine/engine1.wav", Music.class);

        //-menu
        assetManager.load("graphics/menu/menu_frame.png", Texture.class);
        //main menu
        assetManager.load("graphics/menu/single_player_button_up.png", Texture.class);
        assetManager.load("graphics/menu/single_player_button_down.png", Texture.class);
        assetManager.load("graphics/menu/multi_player_button_up.png", Texture.class);
        assetManager.load("graphics/menu/multi_player_button_down.png", Texture.class);

        //gameplay assets
        //-background
        assetManager.load("graphics/background/space.png", Texture.class);

        //-menu
        assetManager.load("textures/main_menu.pack", TextureAtlas.class);

        //-player ships
        assetManager.load("textures/player_ships.pack", TextureAtlas.class);

        //-enemy ships
        assetManager.load("textures/enemy_ships.pack", TextureAtlas.class);

        //-beams
        assetManager.load("textures/beams.pack", TextureAtlas.class);

        assetManager.finishLoading();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }


    public Music getMusic(String musicName){
        return assetManager.get("audio/music/" + musicName + ".ogg", Music.class);
    }

    public Sound getSound(String soundName){
        return assetManager.get("audio/sound/" + soundName + ".wav", Sound.class);
    }

    public Texture getMenuTexture(String textureName){
        return assetManager.get("graphics/menu/" + textureName + ".png", Texture.class);
    }

    public Texture getBackgroundTexture(String textureName){
        return assetManager.get("graphics/background/" + textureName + ".png", Texture.class);
    }


    public Texture getEnemyShipTexture(String textureName){
        return assetManager.get("graphics/enemyShip/" + textureName + ".png", Texture.class);
    }

    public TextureAtlas getPlayerShipsTextureAtlas(){
        return assetManager.get("graphics/playerShip/player_ships.pack", TextureAtlas.class);
    }

    public TextureAtlas getTextureAtlas(String name){
        return assetManager.get("textures/" + name + ".pack", TextureAtlas.class);
    }

    public TextureAtlas getBeamTextureAtlas(){
        return assetManager.get("graphics/beam/beams.pack", TextureAtlas.class);
    }


    public void dispose(){
        assetManager.dispose();
    }
}
