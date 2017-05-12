package com.empireyard.spacemasters.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.empireyard.spacemasters.sprites.EnemyShip;

/**
 * Created by osk on 23.01.17.
 */

public class SteeringAgent implements Steerable<Vector2>{

    EnemyShip ship;

    Vector2 position;
    float orientation;
    Vector2 linearVelocity;
    float angularVelocity;
    float maxSpeed;

    boolean tagged;
    float maxLinearSpeed, maxLinearAcceleration;
    float maxAngularSpeed, maxAngularAcceleration;

    boolean independentFacing = false;

    SteeringBehavior<Vector2> steeringBehavior;
    SteeringAcceleration<Vector2> steeringOutput;

    public SteeringAgent(EnemyShip ship){

        this.ship = ship;

        this.maxLinearSpeed = 2;
        this.maxLinearAcceleration = 2;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 2;

        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

        position = new Vector2();
        linearVelocity = new Vector2();

    }

    public void update (float delta){
        position = ship.getShipBody().getPosition();
//                .add(
//                ship.getShipBody().getLinearVelocity().x*ship.OFFSET_TO_TARGET,
//                ship.getShipBody().getLinearVelocity().y*ship.OFFSET_TO_TARGET
//        );
        orientation = ship.getShipBody().getAngle();
        linearVelocity = ship.getShipBody().getLinearVelocity();
        angularVelocity = ship.getShipBody().getAngularVelocity();
        if(steeringBehavior != null){
            steeringBehavior.calculateSteering(steeringOutput);
            //Gdx.app.log("steeringAgent", "steering behaviour " + steeringBehavior);

            applySteering(steeringOutput, delta);
        }
    }

    private void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed
        this.position.mulAdd(linearVelocity, time);
        this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());

        // Update orientation and angular velocity
//        Gdx.app.log("steeringAgent: ",
//                Float.toString(linearVelocity.x) + " | " +
//                Float.toString(linearVelocity.y));
        // Update orientation and angular velocity
        if (independentFacing) {
            this.orientation += angularVelocity * time;
            this.angularVelocity += steering.angular * time;
        } else {
            // For non-independent facing we have to align orientation to linear velocity
            float newOrientation = calculateOrientationFromLinearVelocity(this);
            if (newOrientation != this.orientation) {
                this.angularVelocity = (newOrientation - this.orientation) * time;
                this.orientation = newOrientation;
            }
        }
    }

    public static float calculateOrientationFromLinearVelocity (SteeringAgent character) {
        // If we haven't got any velocity, then we can do nothing.
        if (character.linearVelocity.isZero(character.getZeroLinearSpeedThreshold()))
            return character.getOrientation();

        return character.vectorToAngle(character.getLinearVelocity());
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }
    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    public SteeringAcceleration<Vector2> getSteeringOutput() {
        return steeringOutput;
    }
    public void setSteeringOutput(SteeringAcceleration<Vector2> steeringOutput) {
        this.steeringOutput = steeringOutput;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return this.linearVelocity;
    }

    @Override
    public float getAngularVelocity() {
        return this.angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity){
        this.angularVelocity = angularVelocity;
    }

    @Override
    public float getBoundingRadius() {
        return 0;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return this.position;
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

}
