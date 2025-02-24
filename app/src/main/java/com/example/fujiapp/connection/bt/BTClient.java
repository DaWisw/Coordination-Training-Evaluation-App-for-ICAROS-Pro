package com.example.fujiapp.connection.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;


public class BTClient extends Thread {
    private final String TAG = "BTClient";
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter _adapter;

    private BluetoothSocket _btStream;
    private String _connectionString;

    private BluetoothDevice _device;

    public BluetoothDevice getDevice() {
        return _device;
    }

    private BTComms _comms;

    public BTComms getComms() {
        return _comms;
    }


    private boolean _isConnected = false;

    public boolean IsConnected() {
        return _isConnected;
    }

    public boolean _isValidBlueToothAddress = false;

    public void IsValidBlueToothAddress() {
        _isValidBlueToothAddress = _adapter.checkBluetoothAddress(_connectionString);
    }

    private Vector<ConnectedListener<BTClient>> eventSubscribers = new Vector<ConnectedListener<BTClient>>();

    /*This is an array of objects that implement the ConnectedListener interface. Right now the only class that
    implements it is ConnectListenerImpl class and it implements the Connected method*/
    private void OnConnected() {
        @SuppressWarnings("unchecked")
        //Create a new  Vector with exactly same elements as eventSubscribers
                Vector<ConnectedListener<BTClient>> handler = (Vector<ConnectedListener<BTClient>>) eventSubscribers.clone();
        Iterator<ConnectedListener<BTClient>> iter = handler.iterator();
        while (iter.hasNext()) {
            iter.next().Connected(new ConnectedEvent<BTClient>(this));
        }
    }

    public void addConnectedEventListener(ConnectedListener<BTClient> listener) {
        eventSubscribers.add(listener);
    }

    public void removeConnectedEventListener(ConnectedListener<BTClient> listener) {
        eventSubscribers.remove(listener);
    }

    public BTClient(BluetoothAdapter adapter, String connectionString) {
        //Initialize the class variables
        _adapter = adapter; //Assign the adaptor
        // Initializing all other variables to null
        _isConnected = false;
        _comms = null;
        _btStream = null;
        _device = null;
        _connectionString = connectionString;

    }

    //Added
	/*private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
			throws IOException {
		if(Build.VERSION.SDK_INT >= 10){
			try {
				final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection", e);
			}
		}
		return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}*/

    public boolean tryToConnect() {
        IsValidBlueToothAddress();
        //Get a device for the given BT MAC address
        _device = _adapter.getRemoteDevice(_connectionString);
        String name = _device.getName();
        try {
            //Create a Secure RFComm Bluetooth socket for secure outgoing connection with device
            //using a given "UUID--using well known UUID SPP suggested by Android??"
            _btStream = _device.createRfcommSocketToServiceRecord(MY_UUID);
            //_btStream = createBluetoothSocket(_device);

            //Needs to call this to be sure enough to cancel a discovery service
            //since it is heavy weight service
            _adapter.cancelDiscovery();

            if (_btStream != null) {    //Attempt to connect to a remote device...Needs an exception check here to
                //see if it fails
                _btStream.connect();
                _isConnected = true;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);

            /*//Check if necessary
            try {
                _btStream = (BluetoothSocket) _device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(_device, 3);
                _btStream.connect();
                _isConnected = true;

            } catch (Exception e1) {
                Log.e(TAG, "BTClient", e1);
                try {
                    _btStream.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }*/
        }
        return _isConnected;
    }


    @Override
    public void run() {

        //Needs to call this to be sure enough to cancel a discovery service
        //since it is heavy weight service
        _adapter.cancelDiscovery();


        if (_isConnected == true)
            StartCommunication();
        else
            System.out.println("Can't connect to the BioHarness.");
    }

    private void StartCommunication() {
        //Create a new commmunication thread and run it
        _comms = new BTComms(_btStream);
        _comms.start();

        //Calls Connected method which calls ReceivedPacketMethod that prints information on the phone's text box based on the message type
        OnConnected();
    }

    public void Close() {
        if (_btStream != null) {
            try {
                _btStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "Closing has not worked", e);
            }
        }
        if(_comms != null) {
            _comms.Close();
        }
        _isConnected = false;
        //_btStream = null;
    }
}
