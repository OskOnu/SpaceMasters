package com.empireyard.spacemasters.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.gameplay.Enemy;
import com.empireyard.spacemasters.gameplay.GameStateManager;
import com.empireyard.spacemasters.gameplay.Level;
import com.empireyard.spacemasters.gameplay.LevelStateManager;
import com.empireyard.spacemasters.scenes.Hud;
import com.empireyard.spacemasters.sprites.PlayerShip;
import com.empireyard.spacemasters.tools.WorldContactListener;

/**
 * Created by osk on 28.01.17.
 */

public class SinglePlayer implements Screen, InputProcessor {
    //Debug
    ShapeRenderer shapeRenderer = new ShapeRenderer();


    SpaceMasters spaceMasters;

    OrthographicCamera camera;
    Viewport viewport;


    World world;
    Box2DDebugRenderer box2DDebugRenderer;
    //SpaceMastersWorldCreator spaceMastersWorldCreator;


    TextureAtlas textureAtlas;


    TmxMapLoader tmxMapLoader;
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    Array<PlayerShip> playerShips;
    //Enemy enemy;

    //Input handling
    Vector3 firstFinger;
    Vector3 secondFinger;
    MouseJointDef mouseJointDef;
    MouseJoint joint;

    boolean firstFingerTouched;
    boolean secondFingerTouched;

    Hud hud;

    float mapPixelWidth;
    float mapPixelHeight;

    LevelStateManager levelStateManager;

    int host;

    public SinglePlayer(SpaceMasters spaceMasters) {
        this.spaceMasters = spaceMasters;

        camera = new OrthographicCamera();
        viewport = new FitViewport(SpaceMasters.WORLD_WIDTH/SpaceMasters.PIXEL_PER_METER, SpaceMasters.WORLD_HEIGHT/SpaceMasters.PIXEL_PER_METER, camera);


        //textureAtlas = spaceMasters.getAssetHandler().getPlayerShipsTextureAtlas();

        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("graphics/map/space1.tmx");

        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/SpaceMasters.PIXEL_PER_METER);

        camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        box2DDebugRenderer = new Box2DDebugRenderer();

        world = new World(new Vector2(0, 0), true);
        int i = 0;

        MapProperties mapProperties = tiledMap.getProperties();

        int mapWidth = mapProperties.get("width", Integer.class);
        int mapHeight = mapProperties.get("height", Integer.class);
        int tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        int tilePixelHeight = mapProperties.get("tileheight", Integer.class);

        this.mapPixelWidth = mapWidth * tilePixelWidth/SpaceMasters.PIXEL_PER_METER;
        this.mapPixelHeight = mapHeight * tilePixelHeight/SpaceMasters.PIXEL_PER_METER;

        Body boundary;

        boundary = createPolygon(mapPixelWidth, 0, new Vector2(0, 0));
        boundary = createPolygon(mapPixelWidth, 0, new Vector2(0, mapPixelHeight));
        boundary = createPolygon(0, mapPixelHeight, new Vector2(0, 0));
        boundary = createPolygon(0, mapPixelHeight, new Vector2(mapPixelWidth, 0));

        playerShips = new Array<PlayerShip>();
        playerShips.add(new PlayerShip(spaceMasters.getAssetHandler(), world, "player_ships", "ship", 1, "blue", 10, 10000));

        //enemy = new Enemy(spaceMasters.getAssetHandler(), world, 1, 8, playerShips);
        //Input
        Gdx.input.setInputProcessor(this);

        firstFinger = new Vector3();
        secondFinger = new Vector3();

        playerShips.get(0).getShipBody().setTransform(400/SpaceMasters.PIXEL_PER_METER, 300/SpaceMasters.PIXEL_PER_METER, (float)Math.toRadians(120));


        levelStateManager = new LevelStateManager();
        levelStateManager.push(new Level(
                spaceMasters.getAssetHandler(), world, playerShips, new Vector2(mapPixelWidth/2, mapPixelHeight/2),
                1, 2, 3,
                2, 0, 0,
                10, 0, 0,
                40, 20,
                1000, 0, 0
        ));

        Body circle = createCircle(6f/SpaceMasters.PIXEL_PER_METER, new Vector2(0, 0));

        mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = circle;
        mouseJointDef.collideConnected = true;
        mouseJointDef.maxForce = 15000/SpaceMasters.PIXEL_PER_METER;

        host = 1;
        world.setContactListener(new WorldContactListener(world, playerShips, levelStateManager.peek().getEnemy().getShips(), levelStateManager.peek(), host));

        hud = new Hud(spaceMasters.getSpriteBatch(), playerShips);
        hud.addPlayerLabel(1);
        hud.update(0);

    }

    @Override
    public void show() {

    }

    int i = 0;
    public void update(float dt){
        world.step(1/60f, 6, 2);


        Array<Fixture> fixtures = new Array<Fixture>();
        world.getFixtures(fixtures);

        for(Fixture fixture : fixtures){
            if(fixture.getUserData() == "\0"){
                world.destroyBody(fixture.getBody());
            }
        }

        for(PlayerShip playerShip : playerShips) {
            playerShip.update(dt);
        }

        levelStateManager.update(dt);

        if(playerShips.get(0).getShipBody() != null) {
            camera.position.x = playerShips.get(0).getShipBody().getPosition().x;
            camera.position.y = playerShips.get(0).getShipBody().getPosition().y;
            if(camera.position.x > (mapPixelWidth - viewport.getWorldWidth()/2)){
                camera.position.x = mapPixelWidth - viewport.getWorldWidth()/2;
            }
            if(camera.position.y > (mapPixelHeight - viewport.getWorldHeight()/2)){
                camera.position.y = mapPixelHeight - viewport.getWorldHeight()/2;
            }
            if(camera.position.x < viewport.getWorldWidth()/2){
                camera.position.x = viewport.getWorldWidth()/2;
            }
            if(camera.position.y < viewport.getWorldHeight()/2){
                camera.position.y = viewport.getWorldHeight()/2;
            }
//            Gdx.app.log("screen size: ",
//                    viewport.getWorldWidth() + " | " +
//                    viewport.getWorldHeight() + " | " +
//                    camera.position.x + " | " +
//                    camera.position.y + " | " +
//                    mapPixelWidth + " | " +
//                    mapPixelWidth
//            );
        }

        if(levelStateManager.peek().isNextLevel()) {
            levelStateManager.set(new Level(
                    spaceMasters.getAssetHandler(), world, playerShips, new Vector2(mapPixelWidth / 2, mapPixelHeight / 2),
                    1, 2, 3,
                    2, 2, 2,
                    10, 10, 10,
                    40, 20,
                    1000, 1000, 1000
            ));
        }

        hud.update(dt);
        camera.update();
        orthogonalTiledMapRenderer.setView(camera);



    }

    @Override
    public void render(float delta) {
        if(SpaceMasters.gameStateManager.peek() == GameStateManager.GameState.PAUSE){
            return;
        }
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();

        box2DDebugRenderer.render(world, camera.combined);

        spaceMasters.getSpriteBatch().setProjectionMatrix(camera.combined);
        spaceMasters.getSpriteBatch().begin();
        levelStateManager.draw(spaceMasters.getSpriteBatch());

//        spaceMasters.getSpriteBatch().draw(
//                ship,
//                playerShips.get(0).getShipBody().getPosition().x, playerShips.get(0).getShipBody().getPosition().y,
//                playerShips.get(0).getWidth()/2, playerShips.get(0).getHeight()/2,
//                playerShips.get(0).getWidth(), playerShips.get(0).getHeight(),
//                0, 0,
//                (float)Math.toDegrees(playerShips.get(0).getShipBody().getAngle()));
        for(PlayerShip playerShip : playerShips){
            playerShip.draw(spaceMasters.getSpriteBatch());
        }
        spaceMasters.getSpriteBatch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.BLUE);

        for(PlayerShip playerShip : playerShips) {
            shapeRenderer.rect(
                    playerShip.getBoundingRectangle().getX(), playerShip.getBoundingRectangle().getY(),
                    playerShip.getBoundingRectangle().getWidth(), playerShip.getBoundingRectangle().getHeight()
            );
        }
//        shapeRenderer.rect(
//                playerShips.get(0).getBeam().getBoundingRectangle().getX(), playerShips.get(0).getBeam().getBoundingRectangle().getY(),
//                playerShips.get(0).getBeam().getBoundingRectangle().getWidth(), playerShips.get(0).getBeam().getBoundingRectangle().getHeight()
//        );
        Vector2 naviMapSize = new Vector2(100,100);
        shapeRenderer.rect(
                SpaceMasters.WORLD_WIDTH - naviMapSize.x, SpaceMasters.WORLD_HEIGHT - naviMapSize.y,
                naviMapSize.x, naviMapSize.y
        );
        shapeRenderer.end();

        hud.stage.draw();
        spaceMasters.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);



    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("inputProcessor: ", "keyDown");
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("inputProcessor: ", "keyUp");
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("inputProcessor: ", "keyTyped");
        return false;
    }

    QueryCallback queryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if(playerShips.get(0).getShipBody() == null || !fixture.testPoint(firstFinger.x, firstFinger.y)){
                return true;
            }

            mouseJointDef.bodyB = playerShips.get(0).getShipBody();
            mouseJointDef.target.set(firstFinger.x, firstFinger.y);

            joint = (MouseJoint) world.createJoint(mouseJointDef);
            return false;
        }
    };

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("inputProcessor: ", "touchDown");

        firstFinger.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
        camera.unproject(firstFinger);
        if(pointer == 0 && playerShips.get(0).getBoundingRectangle().contains(firstFinger.x, firstFinger.y)){
            world.QueryAABB(queryCallback, firstFinger.x, firstFinger.y, firstFinger.x, firstFinger.y);
            firstFingerTouched = true;
        }

        if(pointer == 1){
            secondFinger.set(Gdx.input.getX(1), Gdx.input.getY(1), 0);
            camera.unproject(secondFinger);
            playerShips.get(0).setRotation(-(float)(Math.toDegrees(Math.atan2(
                    (secondFinger.x - playerShips.get(0).getX() - playerShips.get(0).getWidth()/2),
                    (secondFinger.y - playerShips.get(0).getY() - playerShips.get(0).getHeight()/2)
            ))));
            secondFingerTouched = true;
        }
        if(pointer == 2){
            playerShips.get(0).changeShip(2);
            playerShips.get(0).changeBeam(2, "blue");
        }
        if(pointer == 3){
            playerShips.get(0).changeShip(3);
            playerShips.get(0).changeBeam(3, "brightblue");
        }
        if(pointer == 4){
            playerShips.get(0).changeShip(1);
            playerShips.get(0).changeBeam(1, "blue");
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("inputProcessor: ", "touchUp");

        if (pointer == 0 && playerShips.get(0).getShipBody() != null) {
            firstFingerTouched = false;
            playerShips.get(0).getShipBody().setLinearVelocity(0 , 0);

            if(joint != null && world.getJointCount() != 0) {
                world.destroyJoint(joint);
                joint = null;
            }
        }
        if(pointer == 1){
            secondFingerTouched = false;
            //world.destroyBody(playerShips.get(0).getShipBody());
            playerShips.get(0).fire();
//            if(playerShips.get(0).getBeam().getBeamBody() == null){
//                playerShips.get(0).fire(1);
//
//            }else {
//
//                playerShips.get(0).getBeam().destroy();
                //playerShips.get(0).getBeam().getBeamBody().setTransform(playerShips.get(0).getX(), playerShips.get(0).getY(), playerShips.get(0).getRotation());
//            }
        }
        return true;
    }

    Vector2 tmp = new Vector2();
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //Gdx.app.log("inputProcessor: ", "touchDragged");

        if(firstFingerTouched) {
            firstFinger.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
            camera.unproject(firstFinger);
            Gdx.app.log("inputProcessor: ", "first finger dragged ");
            if(joint != null) {
                joint.setTarget(tmp.set(firstFinger.x, firstFinger.y));
            }
        }
        if(secondFingerTouched){
            secondFinger.set(Gdx.input.getX(1), Gdx.input.getY(1), 0);
            camera.unproject(secondFinger);
            Gdx.app.log("inputProcessor: ", "second finger dragged");
            playerShips.get(0).setRotation(-(float)(Math.toDegrees(Math.atan2(
                    (secondFinger.x - playerShips.get(0).getX() - playerShips.get(0).getWidth()/2),
                    (secondFinger.y - playerShips.get(0).getY() - playerShips.get(0).getHeight()/2)
            ))));
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Gdx.app.log("inputProcessor: ", "mouseMoved");
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        Gdx.app.log("inputProcessor: ", "scrolled");
        return false;
    }

    public Body createPolygon(float width, float height, Vector2 position){
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        bodyDef.position.set(
                position.x + width/2,
                position.y + height/2
        );

        body = world.createBody(bodyDef);

        shape.setAsBox(
                width/2,
                height/2
        );
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public Body createCircle(float radius, Vector2 position){
        BodyDef bodyDef = new BodyDef();
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        bodyDef.position.set(
                position.x,
                position.y
        );

        body = world.createBody(bodyDef);

        shape.setRadius(radius);
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    @Override
    public void dispose() {
        for(PlayerShip playerShip : playerShips){
            playerShip.dispose();
        }
        tiledMap.dispose();
        orthogonalTiledMapRenderer.dispose();
        levelStateManager.dispose();
        textureAtlas.dispose();
    }
}
