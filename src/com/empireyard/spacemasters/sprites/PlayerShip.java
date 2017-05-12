package com.empireyard.spacemasters.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.handlers.AssetHandler;

/**
 * Created by osk on 28.01.17.
 */

public class PlayerShip extends Sprite implements Location<Vector2> {
    final float SHIP_1_FACTOR = 1;
    final float SHIP_2_FACTOR = 2;
    final float SHIP_3_FACTOR = 3;

    protected final static float SHIP_1_BEAM_OFFSET = 45;//offset from center of the ship
    protected final static float SHIP_1_BEAM_SPACING = 30;//angular spacing between beams(keeps symmetry between left and right)

    int MAX_HEALTH;
    int INITIAL_SCORE = 0;

    private int BEAM_1_PER_CANNON_NUMBER = 0; //beams number per cannon

    AssetHandler assetHandler;
    TextureRegion ship;
    Body shipBody;

    World world;

    boolean redefine;

    float SCALE = 0.8f;

    String assetName;
    String resourceName;
    int type;
    String color;

    int weaponType;
    Array<Beam> beamsA;
    Array<Beam> beamsB;

    float orientation;

    int health;
    int score;

    String name;

    float fireFrequency;

    public enum STATE{
        SLEEP, DESTROYED, ABOUT_DEAD, ATTACK
    }

    private STATE state;

    public PlayerShip(AssetHandler assetHandler, World world, String assetName, String resourceName, int type, String color, int BEAM_1_PER_CANNON_NUMBER, int MAX_HEALTH) {
        this.assetHandler = assetHandler;
        this.assetName= assetName;
        this.resourceName = resourceName;
        this.type = type;
        this.color = color;

        this.name = "Master";

        ship = new TextureRegion(assetHandler.getTextureAtlas(assetName).findRegion(resourceName + type + color));

        this.world = world;
        setSize(ship.getRegionWidth()*SCALE, ship.getRegionHeight()*SCALE);
        define(new Vector2(0, 0));

        setBounds(0, 0, getWidth()/SpaceMasters.PIXEL_PER_METER, getHeight()/SpaceMasters.PIXEL_PER_METER);
        setRegion(ship);


        this.BEAM_1_PER_CANNON_NUMBER = BEAM_1_PER_CANNON_NUMBER;

        //beamA = new Beam(assetHandler, world, 1, "blue", SCALE, 2f);
        beamsA = new Array<Beam>();
        for (int i = 0; i < BEAM_1_PER_CANNON_NUMBER; i++){
            beamsA.add(new Beam(assetHandler, world, this, "beams", "beam", 1, "blue", SCALE, 10f/SpaceMasters.PIXEL_PER_METER));
        }
        beamsB = new Array<Beam>();
        for (int i = 0; i < BEAM_1_PER_CANNON_NUMBER; i++){
            beamsB.add(new Beam(assetHandler, world, this, "beams", "beam", 1, "blue", SCALE, 10f/SpaceMasters.PIXEL_PER_METER));
        }

        weaponType = 1;

        this.MAX_HEALTH = MAX_HEALTH;
        health = MAX_HEALTH;
        this.score = INITIAL_SCORE;
    }

    public void define(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        setShipBody(getWorld().createBody(bodyDef));

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Math.min(ship.getRegionWidth(), ship.getRegionHeight())*SCALE/2/SpaceMasters.PIXEL_PER_METER);
        //PolygonShape shape = new PolygonShape();

        //shape.setAsBox(getWidth()/2/SpaceMasters.PIXEL_PER_METER, getHeight()/2/SpaceMasters.PIXEL_PER_METER);

        if(this instanceof  EnemyShip) {
            fixtureDef.filter.categoryBits = SpaceMasters.ENEMY_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT | SpaceMasters.PLAYER_BEAM_BIT;

            //fixtureDef.friction = 0f;
            if(type == 1){
                fixtureDef.density = 100f*SHIP_1_FACTOR/SpaceMasters.PIXEL_PER_METER;
                fireFrequency = 40;
//        fixtureDef.friction = 0.2f;
                fixtureDef.restitution = 1/SHIP_1_FACTOR;
            }else if(type == 2){
                fixtureDef.density = 100f*SHIP_2_FACTOR/SpaceMasters.PIXEL_PER_METER;
                fireFrequency = 40;
//        fixtureDef.friction = 0.2f;
                fixtureDef.restitution = 1/SHIP_2_FACTOR;
            }else if(type == 3){
                fixtureDef.density = 100f*SHIP_3_FACTOR/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
                fixtureDef.restitution = 1/SHIP_3_FACTOR;
                fireFrequency = 40;
            }


        }else if(this instanceof  RemotePlayerShip){
            fixtureDef.filter.categoryBits = SpaceMasters.REMOTE_PLAYER_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.PLAYER_BIT | SpaceMasters.PLAYER_BEAM_BIT | SpaceMasters.REMOTE_PLAYER_BIT | SpaceMasters.ENEMY_BIT | SpaceMasters.ENEMY_BEAM_BIT;
            fixtureDef.density = 500f/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
//        fixtureDef.restitution = 5;

        }else if(this instanceof  PlayerShip){
            fixtureDef.filter.categoryBits = SpaceMasters.PLAYER_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.REMOTE_PLAYER_BIT | SpaceMasters.REMOTE_PLAYER_BEAM_BIT | SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT | SpaceMasters.ENEMY_BEAM_BIT;
            fixtureDef.density = 500f/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
//        fixtureDef.restitution = 5;

        }
        fixtureDef.shape = shape;
        getShipBody().createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        //EdgeShape edgeShape = new EdgeShape();
        //edgeShape.set();
    }

    public void redefine(){
        if(shipBody != null) {
            Vector2 position = shipBody.getPosition();
            world.destroyBody(shipBody);
            shipBody = null;
            define(position);
        }else{
            define(new Vector2(getX(), getY()));
        }

    }

    public void fire(){
        if(weaponType == 1){
            for (Beam beam : beamsA){
                if(beam.fire(
                        getX() + getWidth()/2, getY() + getHeight()/2, SHIP_1_BEAM_OFFSET*SCALE,
                        (float)Math.toRadians(getRotation()), (float)Math.toRadians(SHIP_1_BEAM_SPACING))
                ){
                    break;
                }
            }
            for (Beam beam : beamsB) {
                if (beam.fire(
                        getX() + getWidth()/2, getY() + getHeight()/2, SHIP_1_BEAM_OFFSET * SCALE,
                        (float)Math.toRadians(getRotation()), (float)Math.toRadians(-SHIP_1_BEAM_SPACING))
                ){
                    break;
                }
            }
        }
    }

    public void fireBeamA(int beamIndex){
        if(weaponType == 1){
            beamsA.get(beamIndex).fire(
                    getX() + getWidth()/2, getY() + getHeight()/2, SHIP_1_BEAM_OFFSET * SCALE,
                    (float)Math.toRadians(getRotation()), (float)Math.toRadians(SHIP_1_BEAM_SPACING)
            );
        }
    }

    public void fireBeamB(int beamIndex){
        if(weaponType == 1){
            beamsB.get(beamIndex).fire(
                    getX() + getWidth()/2, getY() + getHeight()/2, SHIP_1_BEAM_OFFSET * SCALE,
                    (float)Math.toRadians(getRotation()), (float)Math.toRadians(-SHIP_1_BEAM_SPACING)
            );
        }
    }

    public void destroyBeamA(int beamIndex){
        if(weaponType == 1){
            beamsA.get(beamIndex).destroy();
        }
    }

    public void destroyBeamB(int beamIndex){
        if(weaponType == 1){
            beamsB.get(beamIndex).destroy();
        }
    }

    public void update(float dt){
        if(shipBody != null) {
            setOrigin(getWidth() / 2, getHeight() / 2);
            setPosition(shipBody.getPosition().x - getWidth() / 2, shipBody.getPosition().y - getHeight() / 2);
            //setRotation((float)Math.toDegrees(shipBody.getAngle()) - 90);
            shipBody.setTransform(shipBody.getPosition(), (float) Math.toRadians(getRotation() + 90));

            if (weaponType == 1) {
                for (Beam beam : beamsA) {
                    beam.update(dt);
                }
                for (Beam beam : beamsB) {
                    beam.update(dt);
                }
            }if(health < 0){
                destroy();
                health = 0;
            }
            //Gdx.app.log("shipPosition: ", Float.toString(shipBody.getPosition().x) + " | " + Float.toString(shipBody.getPosition().y) + " | " + Float.toString(shipBody.getAngle()));
        }
    }

    @Override
    public void draw(Batch batch){
        if(shipBody != null) {
            super.draw(batch);
            if (weaponType == 1) {
                for (Beam beam : beamsA) {
                    beam.draw(batch);
                }
                for (Beam beam : beamsB) {
                    beam.draw(batch);
                }
            }
        }
    }

    public TextureRegion getTextureRegion(AssetHandler assetHandler, String assetName, String resourceName, int type, String color) {
        TextureRegion textureRegion;
        try {
            textureRegion  = new TextureRegion(assetHandler.getTextureAtlas(assetName).findRegion(resourceName + type + color));
            return textureRegion;
        } catch(Exception e){
            SpaceMasters.setMessage("ship" + type + color + " not found.");
            Gdx.app.log("getShipTextureRegion: ", "ship" + type + color + " not found.");

            //choose another ship
            return null;
        }

    }

    public World getWorld() {
        return world;
    }

    public Body getShipBody() {
        return shipBody;
    }

    public TextureRegion getShip() {
        return ship;
    }

    public void setShip(TextureRegion ship) {
        this.ship = ship;
    }

    public void setShipBody(Body shipBody) {
        this.shipBody = shipBody;
    }


    public void changeShip(int type) {
       // destroy();
        //setTexture();
        ship = new TextureRegion(assetHandler.getTextureAtlas(assetName).findRegion(resourceName + type + color));
        this.setRegion(ship);
        setSize(ship.getRegionWidth()*SCALE/SpaceMasters.PIXEL_PER_METER, ship.getRegionHeight()*SCALE/SpaceMasters.PIXEL_PER_METER);

        //setBounds(0, 0, ship.getRegionWidth()/SpaceMasters.PIXEL_PER_METER, ship.getRegionHeight()/SpaceMasters.PIXEL_PER_METER);
        redefine();
    }

    public void changeBeam(int type, String color){
        for(Beam beam : beamsA){
            beam.changeBeam(type, color);
        }
        for (Beam beam : beamsB){
            beam.changeBeam(type, color);
        }
    }

    @Override
    public Vector2 getPosition() {
        return shipBody.getPosition();
    }

    @Override
    public float getOrientation() {
        return this.orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public Vector2 getLinearVelocity() {
        return shipBody.getLinearVelocity();
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        shipBody.setLinearVelocity(linearVelocity);
    }

    public float getAngularVelocity() {
        return  shipBody.getAngularVelocity();
    }

    public void setAngularVelocity(float angularVelocity) {
        shipBody.setAngularVelocity(angularVelocity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getINITIAL_SCORE() {
        return INITIAL_SCORE;
    }

    public void setINITIAL_SCORE(int INITIAL_SCORE) {
        this.INITIAL_SCORE = INITIAL_SCORE;
    }

    public void decreaseScore(int value){
        this.score -= value;
    }

    public void increaseScore(int value){
        this.score += value;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMAX_HEALTH() {
        return MAX_HEALTH;
    }

    public void setMAX_HEALTH(int MAX_HEALTH) {
        this.MAX_HEALTH = MAX_HEALTH;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void decreaseHealth(int value){
        this.health -= value;
    }

    public void  resetHealth(){
        this.health = MAX_HEALTH;
        redefine();
    }

    public void increaseHealth(int value){
        this.health += value;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Array<Beam> getBeamsA() {
        return beamsA;
    }

    public void setBeamsA(Array<Beam> beamsA) {
        this.beamsA = beamsA;
    }

    public Array<Beam> getBeamsB() {
        return beamsB;
    }

    public void setBeamsB(Array<Beam> beamsB) {
        this.beamsB = beamsB;
    }

    public int getBEAM_1_PER_CANNON_NUMBER() {
        return BEAM_1_PER_CANNON_NUMBER;
    }

    public void setBEAM_1_PER_CANNON_NUMBER(int BEAM_1_PER_CANNON_NUMBER) {
        this.BEAM_1_PER_CANNON_NUMBER = BEAM_1_PER_CANNON_NUMBER;
    }

    public int getBeamsArraySize(){
        if(beamsA.size == beamsB.size){
            return beamsA.size;
        }else{
            Gdx.app.log("beamsA and beamsB sizes differ: ", beamsA.size + " | " + beamsB.size);
            return 0;
        }
    }

    public float getShipFactor(){
        if(type == 1){
            return SHIP_1_FACTOR;
        }else if(type == 2){
            return SHIP_2_FACTOR;
        }else if(type == 3){
            return SHIP_3_FACTOR;
        }else{
            return 0;
        }
    }

    public void destroy(){
        if(shipBody != null) {
            world.destroyBody(shipBody);

            shipBody = null;
        }
    }

    public void dispose(){
        for(Beam beam : beamsA){
            beam.dispose();
        }
        for (Beam beam : beamsB){
            beam.dispose();
        }
        ship.getTexture().dispose();
        getTexture().dispose();
    }
}
