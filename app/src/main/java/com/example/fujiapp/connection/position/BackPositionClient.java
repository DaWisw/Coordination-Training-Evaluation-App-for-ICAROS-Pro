package com.example.fujiapp.connection.position;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.fujiapp.services.BackPositionDataService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BackPositionClient extends Thread {
    private final static String LOG_TAG = BackPositionClient.class.getSimpleName();

    private final String ip;
    private final int port;

    private Handler backPositionDataHandler;

    private Socket positionSocket;
    private String buffer = "";
    private char[] charBuffer = new char[1024];

    private int lastSeparatorIndex;
    private int nextSeparatorIndex;

    private DisconnectedCallback disconnectedCallback;

    public BackPositionClient(String ip, int port, DisconnectedCallback disconnectedCallback) {
        this.ip = ip;
        this.port = port;
        this.disconnectedCallback = disconnectedCallback;
    }

    public boolean tryToConnect() {
        try {
            positionSocket = new Socket(ip, port);
            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "PositionClient could not connect!");
        }
        return false;
    }

    public boolean isConnected() {
        return positionSocket.isConnected();
    }


    @Override
    public void run() {
        boolean timeout = false;
        int timeoutCounter = 0;
        if(isConnected()) {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(positionSocket.getInputStream()))) {
                while (isConnected()) {
                    int charsRead = in.read(charBuffer, 0, charBuffer.length);
                    if(charsRead != -1) {
                        processData(charsRead);
                    } else {
                        if(!timeout) {
                            Thread.sleep(5000);
                            timeout = true;
                        } else if(timeoutCounter <=5) {
                            timeoutCounter++;
                        } else {
                            break;
                        }
                    }
                }
            } catch (IOException e){
                Log.e(LOG_TAG, "Failed to create Stream");
            } catch(InterruptedException e ){
                Log.e(LOG_TAG, "Sleep got canceled");
            }
            catch (Exception e){
                Log.e(LOG_TAG, "Socket got closed");
            }
        }
        disconnectedCallback.onDisconnect();
    }

    public void disconnect() {
        try {
            positionSocket.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "PositionClient failed to disconnect!");
        }
    }

    private void processData(int charsRead) {
        for(int i = 0; i < charsRead; i++) {
            buffer += charBuffer[i];
            if(charBuffer[i] == '}') {
                processMessage(buffer);
                buffer = "";
            }
        }
    }

    private void processMessage(String message) {
        //Log.d(LOG_TAG, String.format("BackPositionReceived: %s", message));

        BackPositionData data = new BackPositionData();
        //Extract Data out of Message
        try {
            JSONObject object = new JSONObject(message);
            data.setTracker(object.getString("tracker"));
            data.setX(object.getDouble("x"));
            data.setY(object.getDouble("y"));
            data.setZ(object.getDouble("z"));

            data.setQx(object.getDouble("qx"));
            data.setQy(object.getDouble("qy"));
            data.setQz(object.getDouble("qz"));

            data.setEx(object.getDouble("ex"));
            data.setEy(object.getDouble("ey"));
            data.setEz(object.getDouble("ez"));

            Message handlerMessage = backPositionDataHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putParcelable(BackPositionDataService.BACK_POSITION_DATA, data);
            handlerMessage.setData(b);
            backPositionDataHandler.sendMessage(handlerMessage);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Message is not parsable");
        }

    }

    public void addBackPositionReceivedListener(Handler backPositionDataHandler) {
        this.backPositionDataHandler = backPositionDataHandler;
    }
}
