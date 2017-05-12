package com.empireyard.spacemasters.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.handlers.AssetHandler;

/**
 * Created by osk on 28.01.17.
 */

public class Beam extends Sprite {

    float BEAM_1_FACTOR = 1;
    float BEAM_2_FACTOR = 2;
    float BEAM_3_FACTOR = 3;

    float SCALE;
    float BEAM_SPEED;

    AssetHandler assetHandler;
    String assetName;
    String resourceName;
    int type;
    String color;

    TextureRegion beam;
    Body beamBody;

    World world;

    boolean redefine;

    boolean fired;
    Vector2 startPoint = new Vector2();
    float distance;

    Object object;

    public enum STATE{
        FIRED, DESTROY, SLEEP, NONE
    }

    private STATE state;

    boolean host;


    public Beam(AssetHandler assetHandler, World world, Object object, String assetName, String resourceName, int type, String color, float SCALE, float BEAM_SPEED) {
        this.world = world;
        this.object = object;
        this.SCALE = SCALE;
        this.BEAM_SPEED = BEAM_SPEED;

        this.assetHandler = assetHandler;
        this.assetName = assetName;
        this.resourceName = resourceName;
        this.type = type;
        this.color = color;

        beam = new TextureRegion(assetHandler.getTextureAtlas(assetName).findRegion(resourceName + type + color));

        setSize(beam.getRegionWidth()*SCALE, beam.getRegionHeight()*SCALE);
        //defineBeam();

        setBounds(0, 0, getWidth() / SpaceMasters.PIXEL_PER_METER, getHeight() / SpaceMasters.PIXEL_PER_METER);
        setRegion(beam);
        fired = false;

        state = STATE.SLEEP;

        this.host = host;
    }

    public void dispose(){
        beam.getTexture().dispose();
        getTexture().dispose();
    }

    public void define(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        beamBody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();


        if(type == 1){
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(beam.getRegionWidth()*SCALE/4/SpaceMasters.PIXEL_PER_METER, beam.getRegionHeight()*SCALE/4/SpaceMasters.PIXEL_PER_METER);


            fixtureDef.density = 200f*BEAM_1_FACTOR/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
            fixtureDef.restitution = 100/BEAM_1_FACTOR/SpaceMasters.PIXEL_PER_METER;

            fixtureDef.shape = shape;

        }else if(type == 2){
            CircleShape shape = new CircleShape();
            shape.setRadius(Math.min(beam.getRegionWidth(), beam.getRegionHeight())*SCALE/5/SpaceMasters.PIXEL_PER_METER);

            fixtureDef.density = 200*BEAM_2_FACTOR/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
            fixtureDef.restitution = 100/BEAM_2_FACTOR/SpaceMasters.PIXEL_PER_METER;

            fixtureDef.shape = shape;

        }else if(type == 3){

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(beam.getRegionWidth()*SCALE/20/SpaceMasters.PIXEL_PER_METER, beam.getRegionHeight()*SCALE/3/SpaceMasters.PIXEL_PER_METER);

            fixtureDef.density = 200*BEAM_3_FACTOR/SpaceMasters.PIXEL_PER_METER;
//        fixtureDef.friction = 0.2f;
            fixtureDef.restitution = 100/BEAM_3_FACTOR/SpaceMasters.PIXEL_PER_METER;

            fixtureDef.shape = shape;

        }


        if(object instanceof EnemyShip){
            fixtureDef.filter.categoryBits = SpaceMasters.ENEMY_BEAM_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.PLAYER_BIT | SpaceMasters.PLAYER_BEAM_BIT;
        }else if(object instanceof RemotePlayerShip){
            fixtureDef.filter.categoryBits = SpaceMasters.REMOTE_PLAYER_BEAM_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.PLAYER_BIT | SpaceMasters.ENEMY_BIT | SpaceMasters.ENEMY_BEAM_BIT;

        }else if(object instanceof PlayerShip){
            fixtureDef.filter.categoryBits = SpaceMasters.PLAYER_BEAM_BIT;
            fixtureDef.filter.maskBits = SpaceMasters.SCREEN_BIT | SpaceMasters.REMOTE_PLAYER_BIT | SpaceMasters.ENEMY_BIT | SpaceMasters.ENEMY_BEAM_BIT;

        }



        beamBody.createFixture(fixtureDef).setUserData(this);

        //EdgeShape edgeShape = new EdgeShape();
        //edgeShape.set();
    }

    public void redefine(){
       if(beamBody != null) {
            Vector2 position = beamBody.getPosition();
            world.destroyBody(beamBody);
            beamBody = null;
            define(position);
        }else {
            define(new Vector2(getX(), getY()));
        }
    }

    public boolean fire(float x, float y, float offset, float rotation, float spacing){
        if(beamBody == null) {
            define(new Vector2(getX(), getY()));
            //setBounds(0, 0, getWidth() / SpaceMasters.PIXEL_PER_METER, getHeight() / SpaceMasters.PIXEL_PER_METER);
            //setPosition(x, y);
            //setRotation(rotation);
            beamBody.setTransform(
                    x + (-(float) Math.sin(rotation + spacing))*offset/SpaceMasters.PIXEL_PER_METER,
                    y + ((float) Math.cos(rotation + spacing))*offset/SpaceMasters.PIXEL_PER_METER,
                    rotation
            );
            beamBody.applyForceToCenter(
                    SpaceMasters.PIXEL_PER_METER * BEAM_SPEED * (-(float) Math.sin(beamBody.getAngle())),
                    SpaceMasters.PIXEL_PER_METER  * BEAM_SPEED * ((float) Math.cos(beamBody.getAngle())),
                    true
            );
            startPoint.set(beamBody.getPosition());
            return true;
        }
        return false;
    }

    public void setFired(boolean fired){
        this.fired = fired;
    }

    public boolean isFired(){
        return fired;
    }

    public void destroy(){
        if(beamBody != null) {
            //fired = false;
            world.destroyBody(beamBody);
            beamBody = null;
            distance = 0;
        }
    }


    public void update(float dt){
        if(beamBody != null) {
            if(state == STATE.DESTROY){
                world.destroyBody(beamBody);
                beamBody = null;
                distance = 0;
                state = STATE.SLEEP;
                return;
                //destroy();
            }

//            beamBody.setLinearVelocity(BEAM_SPEED * ((float) Math.cos(beamBody.getAngle())), BEAM_SPEED * ((float) Math.sin(beamBody.getAngle())));

            setOrigin(getWidth()/2, getHeight()/2);
            setPosition(beamBody.getPosition().x - getWidth()/2 , beamBody.getPosition().y - getHeight()/2);
            setRotation((float)Math.toDegrees(beamBody.getAngle()));

            Vector2 sub = beamBody.getPosition().sub(startPoint);
            distance = (float)Math.hypot(
                    beamBody.getPosition().x - startPoint.x,
                    beamBody.getPosition().y - startPoint.y);
            if(distance > SpaceMasters.WORLD_WIDTH/2/SpaceMasters.PIXEL_PER_METER){
                destroy();
            }
        }

    }

    @Override
    public void draw(Batch batch){
        if(beamBody != null) {
            super.draw(batch);
        }
    }

    public Body getBeamBody() {
        return beamBody;
    }

    public void setBeamBody(Body beamBody) {
        this.beamBody = beamBody;
    }

    public boolean isRedefine() {
        return redefine;
    }

    public void setRedefine(boolean redefine) {
        this.redefine = redefine;
    }

    public void changeBeam(int type, String color) {
        if(color == null){
            color = this.color;
        }else {
            this.color = color;
        }
        this.type = type;
        // destroy();
        //setTexture();
        beam = new TextureRegion(assetHandler.getTextureAtlas(assetName).findRegion(resourceName + type + color));
        this.setRegion(beam);
        setSize(beam.getRegionWidth()*SCALE/SpaceMasters.PIXEL_PER_METER, beam.getRegionHeight()*SCALE/SpaceMasters.PIXEL_PER_METER);

        //setBounds(0, 0, getWidth()/SpaceMasters.PIXEL_PER_METER, getHeight()/SpaceMasters.PIXEL_PER_METER);
        //redefine();
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public float getBeamFactor(){
        if(type == 1){
            return BEAM_1_FACTOR;
        }else if(type == 2){
            return BEAM_2_FACTOR;
        }else if(type == 3){
            return BEAM_3_FACTOR;
        }else{
            return 0;
        }
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }
}
