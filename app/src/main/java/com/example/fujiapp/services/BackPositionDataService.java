package com.example.fujiapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.connection.position.BackPositionClient;
import com.example.fujiapp.connection.position.BackPositionData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackPositionDataService extends Service  {

    private final static String LOG_TAG = BackPositionDataService.class.getSimpleName();
    public final static String BACK_POSITION_DATA = "back_position_data";
    public final static String BACK_POSITION_DATA_UPDATE = "BACK_POSITION_DATA_UPDATE";
    public final static String BACK_POSITION_SOCKET_CONNECTED = "BACK_POSITION_SOCKET_CONNECTED";
    public final static String BACK_POSITION_SOCKET_DISCONNECTED = "BACK_POSITION_SOCKET_DISCONNECTED";


    //Need to be adapted
    // socketAddress for emulator: 10.0.2.2: Not tested for real device
    //private final static String socketAddress = "10.0.2.2";
    //private final static String socketAddress = "172.22.202.208";
    private final static String socketAddress = MainActivity.ip_address.getIp_Adsress();
    private final static int socketPort = 10001;

    private final BackPositionDataBinder positionDataBinder = new BackPositionDataBinder();

    private boolean connected = false;
    private BackPositionClient backPositionClient;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Log.d("IPAddress", socketAddress);
    }

    private void init() {
        Log.d(LOG_TAG, "Init PositionDataService");
    }


    private void establishConnection() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BackPositionClient backPositionClient = new BackPositionClient(socketAddress, socketPort, this::onBackPositionClientDisconnected);

            while(!backPositionClient.tryToConnect()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            BackPositionDataService.this.onConnectionEstablished(backPositionClient);
        });
    }

    private void onBackPositionClientDisconnected() {
        connected = false;
        backPositionClient.disconnect();
        doBackPositionClientDisconnectedBroadcast();
        establishConnection();
    }

    private void onConnectionEstablished(BackPositionClient backPositionClient) {
        this.backPositionClient = backPositionClient;

        if(backPositionClient.isConnected()) {
            this.backPositionClient.addBackPositionReceivedListener(backPositionDataHandler);
            this.backPositionClient.start();
            connected = true;
            doBackPositionClientConnectedBroadcast();
        } else {
            Log.e(LOG_TAG, "BackPositionClient is not connected");
            connected = false;
        }
    }

    public void connect() {
        if(!connected){
            establishConnection();
        }
    }

    public void disconnect() {
        if(connected) {
            backPositionClient.disconnect();
            connected = false;
        }
    }


    private void doBackPositionClientConnectedBroadcast() {
        Intent intent = new Intent(BACK_POSITION_SOCKET_CONNECTED);
        sendBroadcast(intent);
    }

    private void doBackPositionClientDisconnectedBroadcast() {
        Intent intent = new Intent(BACK_POSITION_SOCKET_DISCONNECTED);
        sendBroadcast(intent);
    }

    private void doBackPositionDataBroadBast(BackPositionData backPositionData) {
        Intent intent = new Intent(BACK_POSITION_DATA_UPDATE);
        intent.putExtra(BACK_POSITION_DATA, backPositionData);
        sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return positionDataBinder;
    }


    public class BackPositionDataBinder extends Binder {
        public BackPositionDataService getService() {
            return BackPositionDataService.this;
        }
    }

    final Handler backPositionDataHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            BackPositionData bpd = (BackPositionData) msg.getData().get(BACK_POSITION_DATA);
            doBackPositionDataBroadBast(bpd);
        }
    };
}
