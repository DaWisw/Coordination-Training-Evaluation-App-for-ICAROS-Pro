package com.example.fujiapp.function;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class Task implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensorGyro;
    private SensorData gyroData = null;
    private long dt = 10; //100ms
    private long timeGyro = 0;

    //GyroDaten
    private float gyroX = 0f;
    private float gyroY = 0f;
    private float gyroZ = 0f;

    double goalX;
    double goalY;
    double goalZ;
    Context context;

    int seconds = 0;

    double SCOPE = 4;
    double DURATION;

    boolean test[];
    int i = 0;

    public Task(SensorManager sensorManager, Context context){
        this.sensorManager = sensorManager;
        this.context = context;
    }

    public void startTask(double goalX, double goalY, double goalZ, double duration) {
        String erg = "";
        this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
        this.DURATION = duration * dt;
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null)
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorGyro, SensorManager.SENSOR_DELAY_NORMAL);

        test = new boolean[(int)duration];
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            getGyroData(sensorEvent);
    }


    private void getGyroData(SensorEvent e){
        if(gyroData == null) {
            gyroData = new SensorData(e.values[0], e.values[1], e.values[2], e.timestamp);
            timeGyro = System.currentTimeMillis();
        }
        else{
            float time = (System.currentTimeMillis() - timeGyro) / 1000f; // ZeitDifferenz
            gyroData.setX1(e.values[0]);
            gyroData.setX2(e.values[1]);
            gyroData.setX3(e.values[2]);

            Log.d("Sensor", "gyroData: " + gyroData.getX1() + "\t time: " + time + "\t erg: "+ gyroData.getX1() * time);
            gyroX += gyroData.getX1() * time;
            gyroY += gyroData.getX2() * time;
            gyroZ += gyroData.getX3() * time;
        }

        if (System.currentTimeMillis() - timeGyro >= dt) {
            //tvGyroscopeGyroX.setText("gyroX: "+ String.format("%.02f", gyroX*(180.0/Math.PI)) + "°");
            //tvGyroscopeGyroY.setText("gyroY: "+ String.format("%.02f", gyroY*(180.0/Math.PI)) + "°");
            //tvGyroscopeGyroZ.setText("gyroZ: "+ String.format("%.02f", gyroZ*(180.0/Math.PI)) + "°");
            double x = gyroX * (180.0/Math.PI);
            double y = gyroY * (180.0/Math.PI);
            double z = gyroZ * (180.0/Math.PI);

            Log.d("SensorTask", "gX: " + goalX + "\t X: " + x);
            Log.d("SensorTask", "gY: " + goalY + "\t Y: " + y);
            Log.d("SensorTask", "gZ: " + goalZ + "\t Z: " + z);
            Log.d("SensorTask", " ");


            if (x >= goalX-SCOPE && x <= goalX+SCOPE) {
                seconds += 1;
            } else {
                seconds = 0;
            }

            if (seconds == DURATION) {
                Toast.makeText(context, "Goal erreicht", Toast.LENGTH_LONG).show();
                sensorManager.unregisterListener(this, sensorGyro);
            }


            timeGyro = System.currentTimeMillis();
        }
    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
