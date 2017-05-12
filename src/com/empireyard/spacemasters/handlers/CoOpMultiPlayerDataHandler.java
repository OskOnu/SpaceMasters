package com.empireyard.spacemasters.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import com.empireyard.spacemasters.SpaceMasters;
import com.empireyard.spacemasters.gameplay.Enemy;
import com.empireyard.spacemasters.sprites.Beam;
import com.empireyard.spacemasters.sprites.EnemyShip;
import com.empireyard.spacemasters.sprites.PlayerShip;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by osk on 02.02.17.
 */

public class CoOpMultiPlayerDataHandler {
    float FLOATING_POINT_CONVERSION = 1000;
    float NEGATIVE_VALUE_CONVERSION = 1000;

    Array<PlayerShip> playerShips;
    Array<EnemyShip> enemyShips;

    ByteArray dataBuffer;


    byte[] sendMessageBuffer = new byte[100];

    LinkedHashMap<String, Float> parsedReceivedData;

    //Map<String, Integer> dataSignatures; //dataName, number of bytes
    LinkedHashMap<String, Integer> dataSignatures;


    int dataIterator;
    Vector2 ship1Position;
    float ship1Angle;

    private String leadPlayerId;

    int host;

    int[] firedBeamsA;
    int[] firedBeamsB;

    public CoOpMultiPlayerDataHandler(Array<PlayerShip> playerShips, Array<EnemyShip> enemyShips) {
        this.playerShips = playerShips;
        this.enemyShips = enemyShips;

        dataBuffer = new ByteArray();
        parsedReceivedData = new LinkedHashMap<String, Float>();
        dataSignatures = new LinkedHashMap<String, Integer>();

        dataSignatures.clear();
        //dataSignatures.put("dataName", "numberOfBytes");
        dataSignatures.put("shipPositionX", 2);
        dataSignatures.put("shipPositionY", 2);
        dataSignatures.put("shipAngle", 2);


        sendMessageBuffer = new byte[100];

        ship1Position = new Vector2();

        firedBeamsA = new int[playerShips.get(1).getBeamsA().size];
        firedBeamsB = new int[playerShips.get(1).getBeamsB().size];
    }

    public byte[] prepareBroadcastMessage(PlayerShip playerShip, Array<EnemyShip> enemyShips){
        int i = 0;
        //data for ship1
        sendMessageBuffer[i] = (byte) ((int)(playerShip.getShipBody().getPosition().x*FLOATING_POINT_CONVERSION) >> 8);
        sendMessageBuffer[++i] = (byte) ((int)(playerShip.getShipBody().getPosition().x*FLOATING_POINT_CONVERSION));

        sendMessageBuffer[++i] = (byte) ((int)(playerShip.getShipBody().getPosition().y*FLOATING_POINT_CONVERSION) >> 8);
        sendMessageBuffer[++i] = (byte) ((int)(playerShip.getShipBody().getPosition().y*FLOATING_POINT_CONVERSION));

        sendMessageBuffer[++i] = (byte) ((int)((float)Math.toDegrees(playerShip.getShipBody().getAngle()) + NEGATIVE_VALUE_CONVERSION) >> 8);
        sendMessageBuffer[++i] = (byte) ((int)((float)Math.toDegrees(playerShip.getShipBody().getAngle()) + NEGATIVE_VALUE_CONVERSION));

        for(Beam beam : playerShip.getBeamsA()){
            sendMessageBuffer[++i] = (byte) ((char)((beam.getBeamBody() != null) ? '1' : '0'));
        }
        for(Beam beam : playerShip.getBeamsB()){
            sendMessageBuffer[++i] = (byte) ((char)((beam.getBeamBody() != null) ? '1' : '0'));
        }

        if(host == 1) {
            for (EnemyShip enemyShip : enemyShips) {
                if (enemyShip.getShipBody() != null) {
                    sendMessageBuffer[++i] = (byte) ((char)((enemyShip.getArrivalBehavior().getTarget() == playerShips.get(0)) ? '1' : '0'));

                    sendMessageBuffer[++i] = (byte) ((int) (enemyShip.getShipBody().getPosition().x * FLOATING_POINT_CONVERSION) >> 8);
                    sendMessageBuffer[++i] = (byte) ((int) (enemyShip.getShipBody().getPosition().x * FLOATING_POINT_CONVERSION));

                    sendMessageBuffer[++i] = (byte) ((int) (enemyShip.getShipBody().getPosition().y * FLOATING_POINT_CONVERSION) >> 8);
                    sendMessageBuffer[++i] = (byte) ((int) (enemyShip.getShipBody().getPosition().y * FLOATING_POINT_CONVERSION));

                    sendMessageBuffer[++i] = (byte) ((int)((float)Math.toDegrees(enemyShip.getShipBody().getAngle()) + NEGATIVE_VALUE_CONVERSION) >> 8);
                    sendMessageBuffer[++i] = (byte) ((int)((float)Math.toDegrees(enemyShip.getShipBody().getAngle()) + NEGATIVE_VALUE_CONVERSION));

                }
            }
        }


//        //data for ship1's beams
//        for(int c = 0; c < playerShip.getBeamsArraySize(); c++){
//            if(playerShip.getBeamsA().get(i).getBeamBody() != null){
//                sendMessageBuffer[++i] = (byte) ((int)(1));
//            }else{
//                sendMessageBuffer[++i] = (byte) ((int)(0));
//                sendMessageBuffer[++i] =
//
//            }
//
//        }




        return sendMessageBuffer;
    }

    public void implementReceivedData(byte[] receivedMessageBuffer, PlayerShip playerShip, Array<EnemyShip> enemyShips) {
        int i = 0;

        playerShip.getShipBody().setTransform(
                (((float)(((receivedMessageBuffer[i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF))/FLOATING_POINT_CONVERSION),
                (((float)(((receivedMessageBuffer[++i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF))/FLOATING_POINT_CONVERSION),
                (float)Math.toRadians(ship1Angle = (((float)(((receivedMessageBuffer[++i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF)) - NEGATIVE_VALUE_CONVERSION))
        );
        playerShip.setRotation(ship1Angle - 90);

        for(int c = 0; c < playerShip.getBeamsA().size; c ++){
            if(receivedMessageBuffer[++i] == '1'){
                //Gdx.app.log("beamA", "[" + c + "] = " + receivedMessageBuffer[i]);
                playerShip.fireBeamA(c);
            }else if(receivedMessageBuffer[i] == '0'){
                playerShip.destroyBeamA(c);
            }
        }
        for(int c = 0; c < playerShip.getBeamsB().size; c ++){
            if(receivedMessageBuffer[++i] == '1'){
                //Gdx.app.log("beamB", "[" + c + "] = " + receivedMessageBuffer[i]);
                playerShip.fireBeamB(c);
            }else if(receivedMessageBuffer[i] == '0'){
                playerShip.destroyBeamB(c);
            }
        }

        if(host == 0) {
            for (EnemyShip enemyShip : enemyShips) {
                if(receivedMessageBuffer[++i] == '1') {
                    //Gdx.app.log("enemyShip", "[" + i + "] = " + receivedMessageBuffer[i]);
                    enemyShip.getArrivalBehavior().setTarget(playerShips.get(1));
                }else if(receivedMessageBuffer[i] == '0'){
                    //Gdx.app.log("enemyShip", "[" + i + "] = " + receivedMessageBuffer[i]);
                    enemyShip.getArrivalBehavior().setTarget(playerShips.get(0));
                }
                enemyShip.getShipBody().setTransform(
                        (((float)(((receivedMessageBuffer[++i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF))/FLOATING_POINT_CONVERSION),
                        (((float)(((receivedMessageBuffer[++i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF))/FLOATING_POINT_CONVERSION),
                        (float)Math.toRadians((((float)(((receivedMessageBuffer[++i] << 8) & 0xFF00) | (receivedMessageBuffer[++i]) & 0xFF)) - NEGATIVE_VALUE_CONVERSION))
                );
            }
        }
    }

    public byte[] prepareValue(float value){
        return new byte[]{(byte) ((int) (value) >> 8), (byte) ((int) (value) >> 8)};
    }

    public void addValueToArray(ByteArray byteArray, float value){
        byte[] littleBuffer = new byte[]{(byte) ((int) (value) >> 8), (byte) ((int) (value) >> 8)};

        for(byte byteHalf: littleBuffer){
            byteArray.add(byteHalf);
        }
    }

    public HashMap<String, Float> getParsedReceivedData() {
        return parsedReceivedData;
    }

    public Vector2 getShip1Position() {
        return ship1Position;
    }

    public float getShip1Angle() {
        return ship1Angle;
    }

    public void setLeadPlayerId(String player1Id, String player2Id){
        if(player1Id != null && player2Id != null) {
            char[] str1 = player1Id.toCharArray();
            char[] str2 = player2Id.toCharArray();
            for (int i = 0; i < (str1.length < str2.length ? str1.length : str2.length); i++) {
                if (str1[i] > str2[i]) {
                    //appEnvironment.printLog(string1);
                    this.leadPlayerId = player1Id;
                    return ;
                } else if (str1[i] < str2[i]) {
                    //appEnvironment.printLog(string2);
                    this.leadPlayerId =  player2Id;
                    return;
                } else {
                    continue;//
                }
            }
            this.leadPlayerId = str1.length > str2.length ? player1Id : player2Id;
            return;
        }else {
            this.leadPlayerId =  null;
            return;
        }
    }

    public String getLeadPlayerId() {
        return leadPlayerId;
    }

    public void setLeadPlayerId(String leadPlayerId) {
        this.leadPlayerId = leadPlayerId;
    }

    public void setHost() {
        this.host = -1;
        if(leadPlayerId != null){
            if(leadPlayerId == SpaceMasters.getPlayServices().getMyId()){
                this.host = 1;
                return;
            }else if(leadPlayerId == SpaceMasters.getPlayServices().getPeerId()){
                this.host = 0;
                return;
            }else{
                this.host = -1;
                throw new RuntimeException("leadPlayerId does't match users ids");
            }
        }else {
            this.host = -1;
            throw new RuntimeException("leadPlayerId is null");
        }
    }

    public int isHost() {//checks if whether this user is a host
        return host;
    }
}
