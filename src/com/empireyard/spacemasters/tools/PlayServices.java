package com.empireyard.spacemasters.tools;

import com.badlogic.gdx.graphics.g2d.freetype.FreeType;

/**
 * Created by osk on 18.01.17.
 */

public interface PlayServices{
    static final long ROLE_ALLAY = 0x1; // peer for CoOp
    static final long ROLE_ENEMY = 0x2; // peer for Versus
    static final int MIN_PLAYERS = 1;
    static final int MAX_PLAYERS = 1;

    enum GameMode{
        VERSUS, COOP, NONE
    }

    public void signIn();
    public void signOut();
    public void rateGame();

    public void displayVersusLeaderboard();
    public void displayCoOpLeaderboard();
    public void submitVersusScore(int highScore);
    public void submitCoOpScore(int highScore);

    public void unlockAchievement();
    public void incrementAchievement();
    public void displayAchievements();

    public void showSavedGames();
    public void writeSavedGames(byte[] data, String desc);
    public void loadSavedGames();

    public boolean isSignedIn();
    public void leaveRoom();

    public void startQuickGame(long role);
    public void startCustomGame();
    public void showInvitations();

    public void broadcastData(byte[] sendMessageBuffer, boolean reliable);
    public byte[] getReceivedMessageBuffer();
    public String getMyName();
    public String getMyId();
    public String getPeerId();
    public String getPeerName();

    public GameMode getGameMode();
    public void setGameMode(GameMode gameMode);

    public void resetGame();
    public void startGame(GameMode gameMode);
}
