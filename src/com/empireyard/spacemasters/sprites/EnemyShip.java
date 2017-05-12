package com.empireyard.spacemasters.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.gameplay.Enemy;
import com.empireyard.spacemasters.handlers.AssetHandler;
import com.empireyard.spacemasters.tools.SteeringAgent;

/**
 * Created by osk on 29.01.17.
 */

public class EnemyShip extends PlayerShip {
    public float MAX_DISTANCE_TO_TARGET;
    public float OFFSET_TO_TARGET;
    private SteeringAgent steeringAgent;
    private Seek seek;
    private static Flee flee;
    Arrive<Vector2> arrivalBehavior;

    public StateMachine<EnemyShip, EnemyState> stateMachine;

    PlayerShip target;

    float distanceToTarget;

    int counter;

    int host;


    public EnemyShip(AssetHandler assetHandler, World world, int type, int BEAM_1_PER_CANNON_NUMBER, PlayerShip target, int MAX_HEALTH) {
        super(assetHandler, world, "enemy_ships", "enemy", type, "red", 0, MAX_HEALTH);

        super.name = "Enemy";
        this.target = target;

        super.beamsA = new Array<Beam>();
        for (int i = 0; i < BEAM_1_PER_CANNON_NUMBER; i++){
            beamsA.insert(i, new Beam(assetHandler, world, this, "beams", "beam", 1, "red", SCALE, 10f/SpaceMasters.PIXEL_PER_METER));
        }
        beamsB = new Array<Beam>();
        for (int i = 0; i < BEAM_1_PER_CANNON_NUMBER; i++){
            beamsB.insert(i, new Beam(assetHandler, world, this, "beams", "beam", 1, "red", SCALE, 10f/SpaceMasters.PIXEL_PER_METER));
        }

        MAX_DISTANCE_TO_TARGET = getWidth()*1.5f;
        OFFSET_TO_TARGET = 1;

        steeringAgent = new SteeringAgent(this);

        arrivalBehavior = new Arrive<Vector2>(steeringAgent, target)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(getWidth()*0.5f)
                .setDecelerationRadius(getWidth()*1.5f);

        steeringAgent.setMaxLinearSpeed(10);
        steeringAgent.setMaxAngularSpeed(1f);

        flee = new Flee<Vector2>(steeringAgent, target);

        stateMachine = new DefaultStateMachine<EnemyShip, EnemyState>(this, EnemyState.FIRE);

        seek = new Seek<Vector2>(steeringAgent, target);

        steeringAgent.setSteeringBehavior(arrivalBehavior);

        counter = 0;

        host = 1;
    }

    @Override
    public void update(float dt){

        if(shipBody != null) {
            counter++;
            if(host == 1) {
                steeringAgent.update(dt);
                stateMachine.update();

                setOrigin(getWidth() / 2, getHeight() / 2);
                setPosition(shipBody.getPosition().x - getWidth() / 2, shipBody.getPosition().y - getHeight() / 2);
                setRotation((float)Math.toDegrees(shipBody.getAngle()) - 90);
                //shipBody.setTransform(getPosition().x++, getPosition().y, (float) Math.toRadians(getOrientation() + 90));
                //

                if (distanceToTarget > MAX_DISTANCE_TO_TARGET) {
                    shipBody.setLinearVelocity(steeringAgent.getLinearVelocity().x, steeringAgent.getLinearVelocity().y);
                    //shipBody.setAngularVelocity(steeringAgent.getAngularVelocity());
                } else {
                    shipBody.setLinearVelocity(0, 0);
                }

                shipBody.setTransform(shipBody.getPosition(), steeringAgent.getOrientation() + 1.5708f);
            }else {
                steeringAgent.update(dt);
                stateMachine.update();
                setOrigin(getWidth() / 2, getHeight() / 2);
                setPosition(shipBody.getPosition().x - getWidth() / 2, shipBody.getPosition().y - getHeight() / 2);
                setRotation((float)Math.toDegrees(shipBody.getAngle()) - 90);
            }if (distanceToTarget < MAX_DISTANCE_TO_TARGET) {
                shipBody.setLinearVelocity(0, 0);
            }

//            Gdx.app.log("steeringAgent: ",
//                    Float.toString(steeringAgent.getPosition().x) + " | " +
//                    Float.toString(steeringAgent.getPosition().y) + " | " +
//                    Float.toString(steeringAgent.getLinearVelocity().x) + " | " +
//                    Float.toString(steeringAgent.getLinearVelocity().y) + " | " +
//                    Float.toString((float)Math.toDegrees(steeringAgent.getOrientation()))
//            );

            if (weaponType == 1) {
                for (Beam beam : beamsA) {
                    beam.update(dt);
                }
                for (Beam beam : beamsB) {
                    beam.update(dt);
                }
            }

            //shipBody.setLinearVelocity(steeringAgent.getShip().getLinearVelocity());
            distanceToTarget = (float)Math.hypot(
                    target.getPosition().x - shipBody.getPosition().x,
                    target.getPosition().y - shipBody.getPosition().y
            );
        }

        if(health < 0){
            destroy();
            health = 0;
        }
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    @Override
    public void fire(){
        if(counter > fireFrequency) {
            super.fire();
            counter = 0;
        }

    }

    public enum EnemyState implements State<EnemyShip> {

        FIRE() {
            @Override
            public void update(EnemyShip entity) {
                if (entity.target.getShipBody() == null) {
                    //Gdx.app.log("EnemyState: ", "NOT FIRE");
                    entity.fire();
                    entity.steeringAgent.setSteeringBehavior(entity.arrivalBehavior);
                    // entity.stateMachine.changeState(FIRE);
                } else {
                    //Gdx.app.log("EnemyState: ", "FIRE");
                    entity.fire();
                    entity.steeringAgent.setSteeringBehavior(entity.arrivalBehavior);
                }
            }
        };

//        RUN_AWAY() {
//            @Override
//            public void update(EnemyShip entity) {
//                if (entity.getHealth() > entity.ENEMY_A_MAX_HEALTH * 0.1) {
//                    entity.stateMachine.changeState(FIRE);
//                } else {
//                    entity.steeringAgent.setSteeringBehavior(flee);
//                }
//            }
//        },
//
//        SLEEP() {
//            @Override
//            public void update(EnemyShip entity) {
//                if (entity.getTargets().getShip().getShipBody() != null) {
//                    entity.stateMachine.changeState(FIRE);
//                } else {
//                    entity.ships.getShipBody().setLinearVelocity(new Vector2(0, 0));
//
//                }
//            }
//        },
//
//        DEAD() {
//            @Override
//            public void update(EnemyShip entity) {
//                if (entity.ships.getShipBody() != null) {
//                    entity.stateMachine.changeState(FIRE);
//                } else {
//                    entity.ships.destroy();
//                }
//            }
//        };

        @Override
        public void enter(EnemyShip entity) {

        }

        @Override
        public void exit(EnemyShip entity) {

        }

        @Override
        public boolean onMessage(EnemyShip entity, Telegram telegram) {
            return false;
        }
    }

    public void setArrivalBehavior(float timeToTarget, float arrivalTolerance, float decelerationRadius) {

        this.arrivalBehavior = new Arrive<Vector2>(steeringAgent, target)
                .setTimeToTarget(timeToTarget)
                .setArrivalTolerance(arrivalTolerance)
                .setDecelerationRadius(decelerationRadius);
        steeringAgent.setSteeringBehavior(arrivalBehavior);
    }

    public Arrive<Vector2> getArrivalBehavior() {
        return arrivalBehavior;
    }

    public float getOFFSET_TO_TARGET() {
        return OFFSET_TO_TARGET;
    }

    public void setOFFSET_TO_TARGET(float OFFSET_TO_TARGET) {
        this.OFFSET_TO_TARGET = OFFSET_TO_TARGET;
    }

    public float getMAX_DISTANCE_TO_TARGET() {
        return MAX_DISTANCE_TO_TARGET;
    }

    public void setMAX_DISTANCE_TO_TARGET(float MAX_DISTANCE_TO_TARGET) {
        this.MAX_DISTANCE_TO_TARGET = MAX_DISTANCE_TO_TARGET;
    }

    public SteeringAgent getSteeringAgent() {
        return steeringAgent;
    }

    public void setSteeringAgent(SteeringAgent steeringAgent) {
        this.steeringAgent = steeringAgent;
    }
}
