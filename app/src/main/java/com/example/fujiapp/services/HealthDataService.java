package com.example.fujiapp.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.fujiapp.connection.bt.BTClient;
import com.example.fujiapp.connection.bt.ConnectListenerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HealthDataService extends Service {

    private final static String LOG_TAG = HealthDataService.class.getSimpleName();
    private final static String MAC = "A0:E6:F8:48:2D:D0";

    public final static String HEALTH_META_DATA_UPDATE = "HEALTH_META_DATA_UPDATE";
    public final static String BLUETOOTH_CONNECTED = "BLUETOOTH_CONNECTED";

    private final int GEN_PACKET = 1200;
    private final int ECG_PACKET = 1202;
    private final int BREATH_PACKET = 1204;
    private final int R_to_R_PACKET = 1206;
    private final int ACCELEROMETER_PACKET = 1208;
    private final int SERIAL_NUM_PACKET = 1210;
    private final int SUMMARY_DATA_PACKET =1212;
    private final int EVENT_DATA_PACKET =1214;
    public byte[] dataBytes;

    private boolean connected;

    private final IBinder healthDataBinder = new HealthDataBinder();
    private BluetoothAdapter adapter;
    private BTClient btClient;
    private ConnectListenerImpl connectListener;


    @Override
    public void onCreate() {
        init();
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        registerReceivers();
    }

    private void establishConnection() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            adapter = BluetoothAdapter.getDefaultAdapter();
            BTClient btClient = new BTClient(adapter, MAC);

            while(!btClient.tryToConnect()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            HealthDataService.this.onConnectionEstablished(btClient);
        });
    }

    private void onConnectionEstablished(BTClient btClient) {
        this.btClient = btClient;

        connectListener = new ConnectListenerImpl(handler,dataBytes);
        btClient.addConnectedEventListener(connectListener);

        if(btClient.IsConnected()) {
            btClient.start();
            connected = true;
            doBluetoothConnectedBroadcast();
        } else {
            Log.e(LOG_TAG, "Client is not connected");
            connected =  false;
        }
    }


    private void registerReceivers() {
        //Sending a message to android that we are going to initiate a pairing request
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        //Registering a new BTBroadcast receiver from the Main Activity context with pairing request event
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return healthDataBinder;
    }


    public boolean isConnected() {
        return connected;
    }

    public void connect() {
        if(!connected)
            establishConnection();
    }

    public void disconnect() {
        if(connected) {
            btClient.removeConnectedEventListener(connectListener);
            btClient.Close();
            connected = false;
        }
    }

    private void doHealthMetaDataBroadcast(String heartRate, String respirationRate) {
        Intent intent = new Intent(HEALTH_META_DATA_UPDATE);
        intent.putExtra("heart_rate", heartRate);
        intent.putExtra("respiration_rate", respirationRate);
        sendBroadcast(intent);
    }

    private void doBluetoothConnectedBroadcast() {
        Intent intent = new Intent(BLUETOOTH_CONNECTED);
        sendBroadcast(intent);
    }


    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d(LOG_TAG, "BOND_STATED = " + device.getBondState());
        }
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[] {String.class} );
                byte[] pin = (byte[])m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("LOG_TAG", result.toString());
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case GEN_PACKET:
                    //In this package is the HeartRate
                    String genText = msg.getData().getString("genText");
                    String heartRate = msg.getData().getString("heartRate");
                    String respirationRate = msg.getData().getString("respirationRate");
                    doHealthMetaDataBroadcast(heartRate, respirationRate);
                    break;
                case ECG_PACKET:
                    String ecgText = msg.getData().getString("ecgText");
                    break;
                case BREATH_PACKET:
                    String breathText = msg.getData().getString("breathText");
                    break;
                case R_to_R_PACKET:
                    String RtoRText = msg.getData().getString("RtoRText");
                    break;
                case ACCELEROMETER_PACKET:
                    String AccelerometerText = msg.getData().getString("Accelerometertext");
                    break;
                case SERIAL_NUM_PACKET:
                    String SerialNumtext = msg.getData().getString("SerialNumtxt");
                    break;
                case SUMMARY_DATA_PACKET:
                    String SummaryText = msg.getData().getString("SummaryDataText");
                    break;
                case EVENT_DATA_PACKET:
                    String EventText = msg.getData().getString("EventDataText");
                    break;
            }
        }
    };

    public class HealthDataBinder extends Binder {
        public HealthDataService getService() {
            return HealthDataService.this;
        }
    }
}
