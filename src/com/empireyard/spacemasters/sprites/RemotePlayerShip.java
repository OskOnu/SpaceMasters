package com.empireyard.spacemasters.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.handlers.AssetHandler;

/**
 * Created by osk on 07.02.17.
 */

public class RemotePlayerShip extends PlayerShip {

    public RemotePlayerShip(AssetHandler assetHandler, World world, String assetName, String resourceName, int type, String color, int BEAM_1_PER_CANNON_NUMBER, int MAX_HEALTH) {
        super(assetHandler, world, assetName, resourceName, type, color, BEAM_1_PER_CANNON_NUMBER, MAX_HEALTH);
    }
}
