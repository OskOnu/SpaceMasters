package com.empireyard.spacemasters.gameplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import java.util.Stack;

/**
 * Created by osk on 30.01.17.
 */

public class LevelStateManager {
    Stack<Level> levels;

    public LevelStateManager() {
        levels = new Stack<Level>();
        levels.push(null);
    }

    public void push(Level level){
        levels.push(level);
    }

    public void pop(){
        levels.pop().dispose();
    }

    public void set(Level level){
        levels.pop().dispose();
        levels.push(level);
    }

    public Level peek(){
        return levels.peek();
    }

    public void  update(float dt){
        levels.peek().update(dt);
    }

    public void draw(Batch batch){
        levels.peek().draw(batch);
    }

    public void dispose(){
        levels.peek().dispose();
    }

}
