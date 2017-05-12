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
import com.empireyard.spacemasters.gameplay.Level;
import com.empireyard.spacemasters.sprites.Beam;
import com.empireyard.spacemasters.sprites.EnemyShip;
import com.empireyard.spacemasters.sprites.PlayerShip;
import com.empireyard.spacemasters.sprites.RemotePlayerShip;

/**
 * Created by osk on 30.01.17.
 */

public class VersusWorldContactListener implements ContactListener {
    private final int host;
    World world;
    Array<PlayerShip> playerShips;

    Fixture fixtureA, fixtureB;

    PlayerShip playerShipA, playerShipB;
    EnemyShip enemyShipA, enemyShipB;
    Beam beamA, beamB;

    public VersusWorldContactListener(World world, Array<PlayerShip> playerShips, int host) {
        this.world = world;
        this.playerShips = playerShips;
        this.host = host;
    }

    @Override
    public void beginContact(Contact contact) {
        //Gdx.app.log("WorldContactListener: ", "beginContact: " + contact.getFixtureA().getUserData() + " + " + contact.getFixtureB().getUserData());

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        //Fixture fixture

        //Gdx.app.log("WorldContactListener: ", "beginContact: " + Integer.toString(fixtureA.getFilterData().categoryBits) + " + " + Integer.toString(fixtureB.getFilterData().categoryBits));

       // if(fixtureA.getClass().toString())

        int categoryDefine = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;


//        if(categoryDefine == (SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT)){
//            Gdx.app.log("WorldContactListener: ", "SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT ");
//        }

        switch (categoryDefine){
            case SpaceMasters.PLAYER_BEAM_BIT | SpaceMasters.REMOTE_PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.PLAYER_BEAM_BIT){
                    beamA = ((Beam) fixtureA.getUserData());
                    playerShipA = ((PlayerShip) beamA.getObject());
                    playerShipB = ((RemotePlayerShip) fixtureB.getUserData());
                    if(playerShipA == playerShipB) return;
//
//                    Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(20);
                    playerShipB.decreaseHealth(20);

                    beamA.setState(Beam.STATE.DESTROY);

                        //Gdx.app.log("WorldContactListener Exception: ", e.getMessage() + " | " + e.getCause());

                }else {
                    beamA = ((Beam) fixtureB.getUserData());
                    playerShipA = ((PlayerShip) beamA.getObject());
                    playerShipB = ((RemotePlayerShip) fixtureA.getUserData());
                    if(playerShipA == playerShipB) return;
                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(20);
                    playerShipB.decreaseHealth(20);

                    beamA.setState(Beam.STATE.DESTROY);
                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureA.getUserData()).getHealth()));

                }
                break;
            case SpaceMasters.REMOTE_PLAYER_BEAM_BIT | SpaceMasters.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.REMOTE_PLAYER_BEAM_BIT){
                    beamA = ((Beam) fixtureA.getUserData());
                    playerShipA = ((RemotePlayerShip) beamA.getObject());
                    playerShipB = ((PlayerShip) fixtureB.getUserData());
                    if(playerShipA == playerShipB) return;
//
//                    Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(20);
                    playerShipB.decreaseHealth(20);

                    beamA.setState(Beam.STATE.DESTROY);

                    //Gdx.app.log("WorldContactListener Exception: ", e.getMessage() + " | " + e.getCause());

                }else {
                    beamA = ((Beam) fixtureB.getUserData());
                    playerShipA = ((RemotePlayerShip) beamA.getObject());
                    playerShipB = ((PlayerShip) fixtureA.getUserData());
                    if(playerShipA == playerShipB) return;
                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureB.getUserData()).getHealth()));

                    playerShipA.increaseScore(20);
                    playerShipB.decreaseHealth(20);

                    beamA.setState(Beam.STATE.DESTROY);
                    //Gdx.app.log("enemy health: ", Float.toString(((EnemyShip) fixtureA.getUserData()).getHealth()));

                }
                break;
            case SpaceMasters.PLAYER_BIT | SpaceMasters.REMOTE_PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == SpaceMasters.PLAYER_BIT){
                    playerShipA = ((PlayerShip) fixtureA.getUserData());
                    playerShipB = ((RemotePlayerShip) fixtureB.getUserData());
                    playerShipA.decreaseHealth(20);
                    playerShipB.decreaseHealth(20);
                }else {
                    playerShipA = ((PlayerShip) fixtureB.getUserData());
                    playerShipB = ((RemotePlayerShip) fixtureA.getUserData());
                    playerShipA.decreaseHealth(20);
                    playerShipB.decreaseHealth(20);
                }
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
