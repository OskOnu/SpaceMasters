package com.empireyard.spacemasters.gameplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.handlers.AssetHandler;
import com.empireyard.spacemasters.sprites.EnemyShip;
import com.empireyard.spacemasters.sprites.PlayerShip;
import com.kotcrab.vis.ui.widget.VisRadioButton;

/**
 * Created by osk on 02.02.17.
 */

public class Level {
    float ENEMY_1_FACTOR = 1;
    float ENEMY_2_FACTOR = 2;
    float ENEMY_3_FACTOR = 3;

    Array<Integer> enemiesByTypeNumbers;
    Array<Integer> enemiesByTypeBeamsNumbers;
    Array<Integer> enemiesMaxHealths;

    int enemyDestructionPower = 1;
    int enemyBeamPower = 1;

    Enemy enemy;

    int enemiesNumber;
    boolean nextLevel;
    public Level(AssetHandler assetHandler, World world, Array<PlayerShip> targets, Vector2 MOTHER_SHIP_POSITION,
                 float ENEMY_1_FACTOR, float ENEMY_2_FACTOR, float ENEMY_3_FACTOR,
                 int enemies1Number, int enemies2Number, int enemies3Number,
                 int beamsPerEnemy1, int beamsPerEnemy2, int beamsPerEnemy3,
                 int enemyDestructionPower, int enemyBeamPower,
                 int enemy1maxHealth, int enemy2maxHealth, int enemy3maxHealth) {
        this.ENEMY_1_FACTOR = ENEMY_1_FACTOR;
        this.ENEMY_2_FACTOR = ENEMY_2_FACTOR;
        this.ENEMY_3_FACTOR = ENEMY_3_FACTOR;

        enemiesByTypeNumbers = new Array<Integer>();
        enemiesByTypeNumbers.add(enemies1Number);
        enemiesByTypeNumbers.add(enemies2Number);
        enemiesByTypeNumbers.add(enemies3Number);
        enemiesNumber = enemies1Number + enemies2Number + enemies3Number;

        enemiesByTypeBeamsNumbers = new Array<Integer>();
        enemiesByTypeBeamsNumbers.add(beamsPerEnemy1);
        enemiesByTypeBeamsNumbers.add(beamsPerEnemy2);
        enemiesByTypeBeamsNumbers.add(beamsPerEnemy3);

        enemiesMaxHealths = new Array<Integer>();
        enemiesMaxHealths.add(enemy1maxHealth);
        enemiesMaxHealths.add(enemy2maxHealth);
        enemiesMaxHealths.add(enemy3maxHealth);

        this.enemyDestructionPower = enemyDestructionPower;
        this.enemyBeamPower = enemyBeamPower;

        enemy = new Enemy(
                assetHandler, world, MOTHER_SHIP_POSITION,
                enemiesByTypeNumbers, enemiesByTypeBeamsNumbers, targets, enemiesMaxHealths);

        nextLevel = false;
    }

    public void update(float dt){
        enemy.update(dt);
        if(enemy.getDestroyedShips() >= enemiesNumber){
            nextLevel = true;
            enemy.setDestroyedShips(0);
        }
    }

    public  void draw(Batch batch){
        enemy.draw(batch);
    }

    public void dispose(){
        //enemy.dispose();
        //enemy = null;
    }

    public int getEnemyBeamPower() {
        return enemyBeamPower;
    }

    public int getEnemyDestructionPower() {
        return enemyDestructionPower;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public boolean isNextLevel() {
        return nextLevel;
    }
}
