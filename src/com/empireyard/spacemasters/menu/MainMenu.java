package com.empireyard.spacemasters.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.gameplay.GameStateManager;
import com.empireyard.spacemasters.handlers.AssetHandler;
import com.empireyard.spacemasters.screens.CoOpMultiPlayer;
import com.empireyard.spacemasters.screens.SinglePlayer;
import com.empireyard.spacemasters.screens.VersusMultiPlayer;
import com.empireyard.spacemasters.tools.PlayServices;

/**
 * Created by osk on 26.01.17.
 */

public class MainMenu implements Screen {

    public final static int OPTION_TEXT_FONT_SIZE = 40;

    SpaceMasters spaceMasters;

    OrthographicCamera camera;
    Viewport viewport;
    private Stage stage;
    private Table table;

    Sprite menuFrameSprite;
    Image menuFrame;
    float scaleX, scaleY;

    Array<Sprite> optionButtonsUp;
    Sprite optionButtonDown;

    Array<ImageButton> optionButtons;

    Array<Sprite> navigationButtonsUp;
    Sprite navigationButtonDown;
    Sprite navigationButtonSoundOn;
    Sprite navigationButtonSoundOff;
    SpriteDrawable navigationButtonSound;
    Array<ImageButton> navigationButtons;

    Array<BitmapFont> optionsText;
    Array<Vector2> optionsTextSize;

    public MainMenu(final SpaceMasters spaceMasters) {
        this.spaceMasters = spaceMasters;

        //Menu frame init
        menuFrameSprite = new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("menu_frame")));

        scaleX = menuFrameSprite.getWidth();
        scaleY = menuFrameSprite.getHeight();
        menuFrameSprite.setSize(SpaceMasters.WORLD_WIDTH, SpaceMasters.WORLD_HEIGHT);
        scaleX = menuFrameSprite.getWidth()/scaleX;
        scaleY = menuFrameSprite.getHeight()/scaleY;
        //Gdx.app.log("scale: ", Float.toString(scaleX) + " | " + Float.toString(scaleY) );
        menuFrame = new Image(new SpriteDrawable(menuFrameSprite));
        menuFrame.setOrigin(menuFrame.getWidth()/2, menuFrame.getHeight()/2);

        //Option buttons init
        optionButtonsUp = new Array<Sprite>();
        optionButtonsUp.add(new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("option_button_up"))));
        optionButtonsUp.add(new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("option_button_up"))));
        changeSpriteSize(optionButtonsUp.get(0), scaleX, scaleY);
        changeSpriteSize(optionButtonsUp.get(1), scaleX, scaleY);

        optionButtonDown = new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("option_button_down")));
        changeSpriteSize(optionButtonDown, scaleX, scaleY);

        optionButtons = new Array<ImageButton>();
        optionButtons.add(new ImageButton(new SpriteDrawable(optionButtonsUp.get(0)), new SpriteDrawable(optionButtonDown)));
        optionButtons.get(0).setOrigin(optionButtons.get(0).getWidth()/2, optionButtons.get(0).getHeight()/2);
        optionButtons.get(0).setPosition(menuFrame.getOriginX()-optionButtons.get(0).getOriginX(), 646*scaleY);

        optionButtons.add(new ImageButton(new SpriteDrawable(optionButtonsUp.get(1)), new SpriteDrawable(optionButtonDown)));
        optionButtons.get(1).setOrigin(optionButtons.get(1).getWidth()/2, optionButtons.get(1).getHeight()/2);
        optionButtons.get(1).setPosition(menuFrame.getOriginX()-optionButtons.get(1).getOriginX(), 420*scaleY);

        //Navigation buttons init
        navigationButtonsUp = new Array<Sprite>();
        navigationButtonsUp.add(new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("facebook"))));
        navigationButtonsUp.add(new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("info"))));
        navigationButtonSoundOn = new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("sound-on")));
        navigationButtonSoundOff = new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("sound-off")));
        navigationButtonsUp.add(navigationButtonSoundOn);
        navigationButtonsUp.add(new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("exit"))));

        navigationButtonDown = new Sprite(new TextureRegion(spaceMasters.getAssetHandler().getTextureAtlas("main_menu").findRegion("button_down")));
        changeSpriteSize(navigationButtonDown, scaleX, scaleY);


        changeSpriteSize(navigationButtonSoundOn, scaleX, scaleY);
        changeSpriteSize(navigationButtonSoundOff, scaleX, scaleY);

        navigationButtons = new Array<ImageButton>();

        navigationButtonSound = new SpriteDrawable();
        navigationButtonSound.setSprite(navigationButtonSoundOn);

        int k = 268;
        for(int i = 0; i < navigationButtonsUp.size; i++){
            if(i != 2) {
                changeSpriteSize(navigationButtonsUp.get(i), scaleX, scaleY);
                navigationButtons.add(new ImageButton(new SpriteDrawable(navigationButtonsUp.get(i)), new SpriteDrawable(navigationButtonDown)));
            }else {
                navigationButtons.add(new ImageButton(navigationButtonSound, new SpriteDrawable(navigationButtonDown)));

            }
            navigationButtons.get(i).setOrigin(navigationButtons.get(i).getWidth()/2, navigationButtons.get(i).getHeight()/2);
            navigationButtons.get(i).setPosition(k * scaleX, menuFrame.getHeight() - 1161 * scaleY);
            k += 290;
        }

        //Font & text init
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/HappyKiller.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        optionsText = new Array<BitmapFont>();
        optionsTextSize = new Array<Vector2>();

        parameter.size = OPTION_TEXT_FONT_SIZE;
        parameter.characters = "Singleplayer";
        parameter.color = new Color(0, 255, 255, 1);
        optionsText.add(generator.generateFont(parameter));

        GlyphLayout fontsLayout = new GlyphLayout();
        fontsLayout.setText(optionsText.get(0), parameter.characters);
        optionsTextSize.add(new Vector2(fontsLayout.width, fontsLayout.height));

        parameter.size = OPTION_TEXT_FONT_SIZE;
        parameter.characters = "Multiplayer";
        parameter.color = new Color(0, 255, 255, 1);
        optionsText.add(generator.generateFont(parameter));

        fontsLayout = new GlyphLayout();
        fontsLayout.setText(optionsText.get(1), parameter.characters);
        optionsTextSize.add(new Vector2(fontsLayout.width, fontsLayout.height));

        generator.dispose();


        camera = new OrthographicCamera();
        viewport = new FitViewport(SpaceMasters.WORLD_WIDTH, SpaceMasters.WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.add(this.menuFrame);
        table.setSize(SpaceMasters.WORLD_WIDTH, SpaceMasters.WORLD_HEIGHT);
        table.setPosition(0, 0);
        for(ImageButton optionButton : optionButtons){
            table.addActor(optionButton);
        }

        for(ImageButton navigationButton : navigationButtons){
            table.addActor(navigationButton);
        }
        stage.addActor(table);


        table.setDebug(true);

        //Gdx.input.setInputProcessor();
        optionButtons.get(0).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("optionButtons.get(0): ", "pressed");
                spaceMasters.setScreen(new SinglePlayer(spaceMasters));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SpaceMasters.gameStateManager.set(GameStateManager.GameState.PLAY);

                //SpaceMasters.getPlayServices().startQuickGame(PlayServices.ROLE_ENEMY);
            }
        });

        optionButtons.get(1).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("optionButtons.get(1): ", "pressed");
                //SpaceMasters.getPlayServices().setGameMode(PlayServices.GameMode.COOP);
                //SpaceMasters.getPlayServices().startQuickGame(PlayServices.ROLE_ALLAY);

                //spaceMasters.setScreen(new CoOpMultiPlayer(spaceMasters));
                spaceMasters.setScreen(new MultiPlayerMenu(spaceMasters));
            }
        });

        navigationButtons.get(0).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("navigationButtons.get(0): ", "pressed");

            }
        });

        navigationButtons.get(1).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("navigationButtons.get(1): ", "pressed");

            }
        });

        navigationButtons.get(2).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("navigationButtons.get(2): ", "pressed");

                if(spaceMasters.isSoundPlaying() == true){
                    navigationButtonSound.setSprite(navigationButtonSoundOff);
                    spaceMasters.setSoundPlaying(false);
                }else {
                    navigationButtonSound.setSprite(navigationButtonSoundOn);
                    spaceMasters.setSoundPlaying(true);
                }

            }
        });

        navigationButtons.get(3).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("navigationButtons.get(3): ", "pressed");

            }
        });
    }

    public void changeSpriteSize(Sprite sprite, float scaleX, float scaleY){
        sprite.setSize(sprite.getWidth()*scaleX, sprite.getHeight()*scaleY);
    }

    @Override
    public void show() {

    }


    public void  update(float dt){
        if(SpaceMasters.gameStateManager.peek() == GameStateManager.GameState.QUICK_GAME_COOP_READY){
            spaceMasters.setScreen(new CoOpMultiPlayer(spaceMasters));
        }else if(SpaceMasters.gameStateManager.peek() == GameStateManager.GameState.QUICK_GAME_VERSUS_READY){
            Gdx.app.log("Quick game versus: ", "ready");
            spaceMasters.setScreen(new VersusMultiPlayer(spaceMasters));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        spaceMasters.getSpriteBatch().setProjectionMatrix(camera.combined);
        spaceMasters.getSpriteBatch().begin();
        optionsText.get(0).draw(spaceMasters.getSpriteBatch(), "Singleplayer", menuFrame.getWidth()/2 - optionsTextSize.get(0).x/2, optionButtons.get(0).getY() + optionButtons.get(0).getHeight()/2 + optionsTextSize.get(0).y/2);
        optionsText.get(1).draw(spaceMasters.getSpriteBatch(), "Multiplayer", menuFrame.getWidth()/2 - optionsTextSize.get(1).x/2, optionButtons.get(1).getY() + optionButtons.get(1).getHeight()/2 + optionsTextSize.get(1).y/2);
        spaceMasters.getSpriteBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        menuFrameSprite.getTexture().dispose();

        for (Sprite optionButtonUp : optionButtonsUp){
            optionButtonUp.getTexture().dispose();
        }
        optionButtonDown.getTexture().dispose();

        for (Sprite navigationButtonUp : navigationButtonsUp){
            navigationButtonUp.getTexture().dispose();
        }
        navigationButtonDown.getTexture().dispose();
        navigationButtonSoundOn.getTexture().dispose();
        navigationButtonSoundOff.getTexture().dispose();

        for(BitmapFont optionText : optionsText){
            optionText.dispose();
        }

        stage.dispose();
    }
}
