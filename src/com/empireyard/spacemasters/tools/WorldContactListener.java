package com.empireyard.spacemasters.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.gameplay.Enemy;
import com.empireyard.spacemasters.gameplay.Level;
import com.empireyard.spacemasters.sprites.Beam;
import com.empireyard.spacemasters.sprites.EnemyShip;
import com.empireyard.spacemasters.sprites.PlayerShip;

/**
 * Created by osk on 30.01.17.
 */

public class WorldContactListener implements ContactListener {
    private final int host;
    World world;
    Array<PlayerShip> playerShips;
    Array<EnemyShip> enemyShips;

    Fixture fixtureA, fixtureB;

    PlayerShip playerShipA, playerShipB;
    EnemyShip enemyShipA, enemyShipB;
    Beam beamA, beamB;

    Level level;

    public WorldContactListener(World world, Array<PlayerShip> playerShips, Array<EnemyShip> enemyShips, Level level, int host) {
        this.world = world;
        this.playerShips = playerShips;
        this.enemyShips = enemyShips;
        this.level = level;
        this.host = host;
    }

    @Override
    public void beginContact(Contact contact) {
        //Gdx.app.log("WorldContactListener: ", "beginContact: " + contact.getFixtureA().getUserData() + " + " + contact.getFixtureB().getUserData());

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        //Fixture fixture

        //Gdx.app.log("WorldContactListener: ", "beginContact: " + fixtureA.getBody().getClass().getSuperclass().toString() + " + " + fixtureB.getBody().getClass().getSimpleName());

       // if(fixtureA.getClass().toString())

        int categoryDefine = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;


//        if(categoryDefine == (SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT)){
//            Gdx.app.log("WorldContactListener: ", "SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT ");
//        }

        switch (categoryDefine){
            case SpaceMasters.PLAYER_BEAM_BIT | SpaceMasters.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.PLAYER_BEAM_BIT){
                    beamA = ((Beam) fixtureA.getUserData());
                    playerShipA = ((PlayerShip) beamA.getObject());
                    enemyShipA = ((EnemyShip) fixtureB.getUserData());

                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(level.getEnemyBeamPower());
                    enemyShipA.decreaseHealth(level.getEnemyBeamPower());
                    if(host == 1) {
                        enemyShipA.getArrivalBehavior().setTarget(playerShipA);
                    }
                    beamA.setState(Beam.STATE.DESTROY);

                        //Gdx.app.log("WorldContactListener Exception: ", e.getMessage() + " | " + e.getCause());

                }else {
                    beamA = ((Beam) fixtureB.getUserData());
                    playerShipA = ((PlayerShip) beamA.getObject());
                    enemyShipA = ((EnemyShip) fixtureA.getUserData());

                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(level.getEnemyBeamPower());
                    enemyShipA.decreaseHealth(level.getEnemyBeamPower());
                    if(host == 1) {
                        enemyShipA.getArrivalBehavior().setTarget(playerShipA);
                    }
                    beamA.setState(Beam.STATE.DESTROY);
                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureA.getUserData()).getHealth()));

                }
                break;
            case SpaceMasters.ENEMY_BEAM_BIT | SpaceMasters.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.ENEMY_BEAM_BIT){
                    beamA = (Beam) fixtureA.getUserData();
                    playerShipA = ((PlayerShip) fixtureB.getUserData());
                    playerShipA.decreaseHealth(level.getEnemyBeamPower());
                    beamA.setState(Beam.STATE.DESTROY);
                    //((Beam) fixtureA.getUserData()).destroy();
                    //((EnemyShip) fixtureB.getUserData()).decreaseHealth(level.ENEMY_1_DESTRUCTION_POWER);
                }else {
                    beamA = (Beam) fixtureB.getUserData();
                    playerShipA = ((PlayerShip) fixtureA.getUserData());
                    playerShipA.decreaseHealth(level.getEnemyBeamPower());
                    beamA.setState(Beam.STATE.DESTROY);
                    //((EnemyShip) fixtureB.getUserData()).increaseScore(level.SCORE_FOR_ENEMY_1);
                    //((Beam) fixtureB.getUserData()).destroy();
                }
                break;
            case SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.PLAYER_BIT){
                    playerShipA = ((PlayerShip) fixtureA.getUserData());
                    enemyShipA = ((EnemyShip) fixtureB.getUserData());
                    playerShipA.decreaseHealth(level.getEnemyDestructionPower());
                    enemyShipA.decreaseHealth(level.getEnemyDestructionPower());
                    if(host == 1) {
                        enemyShipA.getArrivalBehavior().setTarget(playerShipA);
                    }
                }else {
                    playerShipA = ((PlayerShip) fixtureB.getUserData());
                    enemyShipA = ((EnemyShip) fixtureA.getUserData());
                    playerShipA.decreaseHealth(level.getEnemyDestructionPower());
                    enemyShipA.decreaseHealth(level.getEnemyDestructionPower());
                    if(host == 1) {
                        enemyShipA.getArrivalBehavior().setTarget(playerShipA);
                    }
                }
                break;
            case SpaceMasters.PLAYER_BEAM_BIT | SpaceMasters.ENEMY_BEAM_BIT:
                break;
            case SpaceMasters.PLAYER_BIT | SpaceMasters.PLAYER_BIT:
                break;
            case SpaceMasters.ENEMY_BIT |SpaceMasters.ENEMY_BIT:
                break;
            case SpaceMasters.PLAYER_BEAM_BIT | SpaceMasters.PLAYER_BEAM_BIT:
                break;
            case SpaceMasters.ENEMY_BEAM_BIT | SpaceMasters.ENEMY_BEAM_BIT:
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {
        //Gdx.app.log("WorldContactListener: ", "endContact: " + contact.getChildIndexA() + " + " + contact.getChildIndexB());

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        //Gdx.app.log("WorldContactListener: ", "preSolve: " + contact.getChildIndexA() + " + " + contact.getChildIndexB());

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        //Gdx.app.log("WorldContactListener: ", "postSolve: " + contact.getChildIndexA() + " + " + contact.getChildIndexB());

    }
}
