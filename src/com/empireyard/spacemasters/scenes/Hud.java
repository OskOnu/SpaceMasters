package com.empireyard.spacemasters.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.sprites.PlayerShip;

/**
 * Created by osk on 31.01.17.
 */

public class Hud {
    public Stage stage;
    private Viewport viewport;

    private Array<PlayerShip> playerShips;

    Array<Label> playerNameLabels;
    Array <Label> playerScoreLabels;
    Array <Label> playerHealthLabels;




    public Hud(Batch batch, Array<PlayerShip> playerShips){
        viewport = new FitViewport(SpaceMasters.WORLD_WIDTH, SpaceMasters.WORLD_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        this.playerShips = playerShips;

        playerNameLabels = new Array<Label>();
        playerScoreLabels = new Array<Label>();
        playerHealthLabels = new Array<Label>();


        for(PlayerShip playerShip : playerShips){
            playerNameLabels.add(new Label("Name", new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
            playerScoreLabels.add(new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.GOLD)));
            playerHealthLabels.add(new Label("Health", new Label.LabelStyle(new BitmapFont(), Color.RED)));
        }
    }

    public void addPlayerLabel(int option){
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        if(option == 1){
            for(Label playerNameLabel : playerNameLabels) {
                table.add(playerNameLabel).expandX();
            }
            table.row();
            for (Label scoreLabel : playerScoreLabels){
                table.add(scoreLabel).expandX();
            }
            table.row();
            for (Label healthLabel : playerHealthLabels){
                table.add(healthLabel).expandX();
            }
        }else if(option == 2){

        }else {
            Gdx.app.log("HUD addPlayerLabel error: ", Integer.toString(option));
        }
        stage.addActor(table);
    }

    public void update(float dt){
        for(int i = 0; i < playerShips.size; i++){
            playerNameLabels.get(i).setText(playerShips.get(i).getName());
            playerScoreLabels.get(i).setText(String.format("%d", playerShips.get(i).getScore()));
            playerHealthLabels.get(i).setText(String.format("%d", playerShips.get(i).getHealth()));
        }
    }
}
