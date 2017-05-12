package com.empireyard.spacemasters.gameplay;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.handlers.AssetHandler;
import com.empireyard.spacemasters.sprites.EnemyShip;
import com.empireyard.spacemasters.sprites.PlayerShip;
import com.empireyard.spacemasters.tools.SteeringAgent;


/**
 * Created by osk on 29.01.17.
 */

public class Enemy {
    Vector2 MOTHER_SHIP_POSITION;

    Array<EnemyShip> ships;


    int destroyedShips;

    public Enemy(AssetHandler assetHandler, World world, Vector2 MOTHER_SHIP_POSITION,
                 Array<Integer> enemiesByTypeNumbers, Array<Integer> enemiesByTypeBeamsNumbers,
                 Array<PlayerShip> targets, Array<Integer> enemiesMaxHealths) {
        this.MOTHER_SHIP_POSITION = MOTHER_SHIP_POSITION;

        ships = new Array<EnemyShip>();

        int type = 1;
        int ct = 0;
        int chooseTarget;
        for(Integer enemiesByTypeNumber : enemiesByTypeNumbers){
            for(int i = 0; i < enemiesByTypeNumber; i++){
                if (targets.size == 1){
                    ships.add(new EnemyShip(assetHandler, world, type, enemiesByTypeBeamsNumbers.get(type - 1), targets.get(0), enemiesMaxHealths.get(type - 1)));
                    ships.get(ct).getShipBody().setTransform(MOTHER_SHIP_POSITION, 0);
                    ct++;
                }else if(targets.size == 2){
                    if(i%2 == 0) {
                        ships.add(new EnemyShip(assetHandler, world, type, enemiesByTypeBeamsNumbers.get(type - 1), targets.get(0), enemiesMaxHealths.get(type - 1)));
                    }else{
                        ships.add(new EnemyShip(assetHandler, world, type, enemiesByTypeBeamsNumbers.get(type - 1), targets.get(1), enemiesMaxHealths.get(type - 1)));
                    }
                        ships.get(ct).getShipBody().setTransform(MOTHER_SHIP_POSITION, 0);
                    ct++;
                }
            }
            type++;
        }



//        for(int i = 0; i < MAX_SHIPS_NUMBER; i ++) {
//            ships.add(new EnemyShip(assetHandler, world, type, 2, targets.get(0), 20));
//            //ships.get(i).getShipBody().setUserData();
//            ships.get(i).getShipBody().setTransform(MOTHER_SHIP_POSITION.x, MOTHER_SHIP_POSITION.y, 0);
//        }
//        ships.get(0).getShipBody().getPosition().x = 1;
//        ships.get(0).getShipBody().getPosition().y = 1;


//        ships.get(0).getShipBody().setTransform(3, 2, 30);
//
//        ships.get(0).getShipBody().setTransform(3, 2, 30);
//

        destroyedShips = 0;

    }

    public void update(float dt){
        destroyedShips = 0;
        for(EnemyShip ship : ships){
            ship.update(dt);
            if(ship.getShipBody() == null){
                destroyedShips++;
            }
        }
    }

    public  void draw(Batch batch){
        for (EnemyShip ship : ships){
            ship.draw(batch);
        }
    }

    public void dispose(){
        for(EnemyShip ship : ships){
            ship.dispose();

        }
    }

    public Array<EnemyShip> getShips() {
        return ships;
    }

    public int getDestroyedShips() {
        return destroyedShips;
    }

    public void setDestroyedShips(int destroyedShips) {
        this.destroyedShips = destroyedShips;
    }
}
