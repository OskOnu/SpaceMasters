package com.empireyard.spacemasters.gameplay;

import com.empireyard.spacemasters.SpaceMasters;

import java.util.Stack;

/**
 * Created by osk on 02.02.17.
 */

public class GameStateManager {
    private Stack<GameState> gameStates;

    public GameStateManager(){
        //this.spaceMasters = spaceMasters;
        gameStates = new Stack<GameState>();
        gameStates.push(GameState.NONE);
    }

    public GameState peek(){
        return gameStates.peek();
    }

    public void push(GameState state){
        gameStates.push(state);
    }

    public void pop(){
        gameStates.pop();
    }

    public void set(GameState state){
        gameStates.pop();
        gameStates.push(state);
    }

    public enum GameState{
        SINGLE_PLAYER_MENU, MULTI_PLAYER_MENU, VERSUS_MENU, COOP_MENU, RANKING_MENU, SETTINGS_MENU, INVITATIONS_MENU, SHIP_MARKET_MENU, WEAPON_MARKET_MENU,
        FACEBOOK, INFO, RETRY,
        SINGLE_PLAYER_READY, QUICK_GAME_VERSUS_READY, INVITE_VERSUS_READY, QUICK_GAME_COOP_READY, INVITE_COOP_READY,
        LOGGED, UNLOGGED,
        PEER_LEFT, PLAYER_1_DEAD, PLAYER_2_DEAD, ENEMY_DEAD,
        NONE,WAITING, PAUSE, QUIT, PLAY
    }
}
