package com.example.fujiapp.function;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.R;
import com.example.fujiapp.connection.position.BackPositionData;
import com.example.fujiapp.data.DbDataSource;
import com.example.fujiapp.data.Goal;
import com.example.fujiapp.data.TrainingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

public class Training_Task implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensorGyro;
    private SensorData gyroData = null;
    private long dt = 10; //100ms
    private long timeGyro = 0;

    //GyroDaten
    private float gyroX = 0f;
    private float gyroY = 0f;
    private float gyroZ = 0f;

    Goal[] goals;
    Context context;

    TextView xTV, xTVTask, xTVTaskGoal;
    TextView yTV, yTVTask, yTVTaskGoal;
    TextView zTV, zTVTask, zTVTaskGoal;
    TextView taskTV, taskResultTV, backPositionHelp, tVTimer;

    int seconds = 0;

    LocalDateTime start;

    double SCOPE = 5;
    double DURATION;

    int i;
    boolean flag;

    boolean straightBack = false;
    int counterBackPosition;
    boolean backTracking = false;
    ImageView imageBack;

    TrainingSession ts;
    LinkedList<Integer> pulse;
    LinkedList<Double> respiration;

    BackPositionData tempfirsBackPositionDataTop;
    BackPositionData tempfirsBackPositionDataBot;

    BackPositionData firsBackpositionDataTop;
    BackPositionData firsBackpositionDataBot;

    // Positionspfeile Anzeige
    ImageView arrowUp;
    ImageView arrowLeft;
    ImageView arrowHold;
    ImageView arrowRight;
    ImageView arrowDown;

    String taskName;

    public Training_Task(SensorManager sensorManager, Context context){
        this.sensorManager = sensorManager;
        this.context = context;

        xTV = (TextView)((Activity)context).findViewById(R.id.xTVvalue);
        yTV = (TextView)((Activity)context).findViewById(R.id.yTVvalue);
        zTV = (TextView)((Activity)context).findViewById(R.id.zTVvalue);
        xTVTask = (TextView)((Activity)context).findViewById(R.id.TaskTVcurrentX);
        yTVTask = (TextView)((Activity)context).findViewById(R.id.TaskTVcurrentY);
        zTVTask = (TextView)((Activity)context).findViewById(R.id.TaskTVcurrentZ);
        xTVTaskGoal = (TextView)((Activity)context).findViewById(R.id.TaskTVgoalX);
        yTVTaskGoal = (TextView)((Activity)context).findViewById(R.id.TaskTVgoalY);
        zTVTaskGoal = (TextView)((Activity)context).findViewById(R.id.TaskTVgoalZ);
        taskTV = (TextView)((Activity)context).findViewById(R.id.TVTask);
        taskResultTV = (TextView)((Activity)context).findViewById(R.id.TaskTVResult);
        backPositionHelp = (TextView)((Activity)context).findViewById(R.id.backPositionHelp);
        tVTimer = (TextView)((Activity)context).findViewById(R.id.tVTimer);
        imageBack = (ImageView) ((Activity)context).findViewById(R.id.imageBack);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null)
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorGyro, SensorManager.SENSOR_DELAY_NORMAL);
        flag = false;

        // Positionspfeile Bildobjekte suchen
        arrowUp = (ImageView)((Activity)context).findViewById(R.id.IV_training_upArrow);
        arrowLeft = (ImageView)((Activity)context).findViewById(R.id.IV_training_leftArrow);
        arrowHold = (ImageView)((Activity)context).findViewById(R.id.IV_training_holdPosition);
        arrowRight = (ImageView)((Activity)context).findViewById(R.id.IV_training_rightArrow);
        arrowDown = (ImageView)((Activity)context).findViewById(R.id.IV_training_downArrow);
    }

    public void startTask(Goal[] goals, String taskName ) {
        ZoneId zid = ZoneId.of("Europe/Berlin");
        start = LocalDateTime.now(zid);
        counterBackPosition = 0;
        taskResultTV.setText("");
        sensorManager.registerListener(this, sensorGyro, SensorManager.SENSOR_DELAY_NORMAL);
        ts = new TrainingSession();
        ts.setUserID(MainActivity.activUser.getId());
        ts.setTaskName(taskName);
        ts.setDate(start.getDayOfMonth() + "." + start.getMonthValue() + "." + start.getYear());
        String m = String.valueOf(start.getMinute());
        if(start.getMinute()<10){
            m = 0 + m;
        }
        String s = String.valueOf(start.getSecond());
        if(start.getSecond()<10){
            s = 0 + s;
        }
        ts.setTime(start.getHour() + ":" + m + ":" + s);

        pulse = new LinkedList<>();
        respiration = new LinkedList<>();

        firsBackpositionDataTop = tempfirsBackPositionDataTop;
        firsBackpositionDataBot = tempfirsBackPositionDataBot;

        this.goals = goals;
        flag = true;
        i = 0;

        //xTVTaskGoal.setText(Double.toString(goals[i].getGoalX()));
        yTVTaskGoal.setText(Double.toString(this.goals[i].getGoalY()));
        zTVTaskGoal.setText(Double.toString(this.goals[i].getGoalZ()));

        taskTV.setText(((Activity)context).getString(R.string.task));

        // Positionsanzeige vorbereiten für Start
        arrowUp.setColorFilter(Color.GREEN);
        arrowLeft.setColorFilter(Color.GREEN);
        arrowHold.setColorFilter(Color.GREEN);
        arrowHold.setVisibility(View.INVISIBLE);
        arrowRight.setColorFilter(Color.GREEN);
        arrowRight.setColorFilter(Color.GREEN);
        arrowDown.setColorFilter(Color.GREEN);
    }

    public void stopTask(){
        sensorManager.unregisterListener(this);
        flag = false;
        i = -1;
        seconds = 0;
        taskTV.setText("");
        yTVTaskGoal.setText("");
        zTVTaskGoal.setText("");


        Button btnStopTask = (Button) ((Activity) context).findViewById(R.id.btnStopTask);
        btnStopTask.setEnabled(false);
        Button btnStartTask = (Button) ((Activity) context).findViewById(R.id.btnStartTask);
        btnStartTask.setEnabled(true);
        Button btnCalibrate = (Button) ((Activity) context).findViewById(R.id.btnCalibrate);
        btnCalibrate.setEnabled(true);

        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(R.id.fragmentTraining);
        rootView.setKeepScreenOn(false);

        // Positionsanzeige vorbereiten (Alles auf Schwarz und zeigen)
        arrowUp.setColorFilter(Color.BLACK);
        arrowUp.setVisibility(View.VISIBLE);
        arrowLeft.setColorFilter(Color.BLACK);
        arrowLeft.setVisibility(View.VISIBLE);
        arrowHold.setColorFilter(Color.BLACK);
        arrowHold.setVisibility(View.VISIBLE);
        arrowRight.setColorFilter(Color.BLACK);
        arrowRight.setVisibility(View.VISIBLE);
        arrowDown.setColorFilter(Color.BLACK);
        arrowDown.setVisibility(View.VISIBLE);
    }

    public void setValues(int heartRate, double respirationRate){
        if(flag){
            pulse.add(heartRate);
            respiration.add(respirationRate);
        }
    }

    public void saveTask(boolean success){
        ZoneId zid = ZoneId.of("Europe/Berlin");
        LocalDateTime stop = LocalDateTime.now(zid);
        long seconds = ChronoUnit.SECONDS.between(start, stop)%60;

        long minutes = ChronoUnit.MINUTES.between(start, stop)%60;
        long hours = ChronoUnit.HOURS.between(start, stop);
        if(seconds>9){
            ts.setResultTime(minutes + ":" + seconds);
        }else if(seconds<10){
            ts.setResultTime(minutes + ":0" + seconds);
        }
        if(pulse.size() > 0) {
            ts.setMaxPulse(Collections.max(pulse).toString());
            ts.setMinPulse(Collections.min(pulse).toString());
            int sum = 0;
            String pulseAll = "";
            for(int p : pulse) {
                sum += p;
                pulseAll = pulseAll + p + " ";
            }
            ts.setPulse(pulseAll);
            if(sum>0){
                ts.setAveragePulse(String.valueOf(sum/pulse.size()));
            } else {
                ts.setAveragePulse(String.valueOf(0));
            }
        } else {
            ts.setMaxPulse("-");
            ts.setMinPulse("-");
            ts.setAveragePulse("-");
            ts.setPulse("-");
        }
        if(respiration.size() > 0) {
            ts.setMaxRespiration(Collections.max(respiration).toString());
            ts.setMinRespiration(Collections.min(respiration).toString());
            double sum = 0;
            String respirationAll = "";
            for(double r : respiration) {
                sum += r;
                respirationAll = respirationAll + r + " ";
            }
            ts.setRespiration(respirationAll);
            if(sum>0){
                ts.setAverageRespiration(String.valueOf(sum/respiration.size()));
            } else {
                ts.setAverageRespiration(String.valueOf(0));
            }
        } else {
            ts.setMaxRespiration("-");
            ts.setMinRespiration("-");
            ts.setAverageRespiration("-");
            ts.setRespiration("-");
        }
        if(backTracking){
            ts.setResultBackposition(String.valueOf(counterBackPosition));
            backTracking = false;
        } else {
            ts.setResultBackposition("-");
        }
        Log.d("DB Test",ts.toString());
        ts.setSuccess(success);
        DbDataSource db = new DbDataSource(context);
        db.openDB();
        db.insertTrainingSession(ts);
        db.closeDB();
        Log.d("DB Test",ts.toString());
        flag = false;
    }



    public void backPosition(BackPositionData recentBackPositionTop, BackPositionData recentBackPositionBot){
        if(flag) {
            backTracking = true;
            double x = firsBackpositionDataBot.getEx() - firsBackpositionDataTop.getEx();
            double y = firsBackpositionDataBot.getEy() - firsBackpositionDataTop.getEy();
            double z = firsBackpositionDataBot.getEz() - firsBackpositionDataTop.getEz();

            double recentX = recentBackPositionBot.getEx() - recentBackPositionTop.getEx();
            double recentY = recentBackPositionBot.getEy() - recentBackPositionTop.getEy();
            double recentZ = recentBackPositionBot.getEz() - recentBackPositionTop.getEz();

            Log.d("PositionRecent123", "top: " + recentBackPositionTop.getEy());
            Log.d("PositionRecent123", "bot: " + recentBackPositionBot.getEy());

            Log.d("PositionTest", "x:  "+ x + " y:  " + y + " z:  " + z);
            Log.d("PositionTest", "rx: "+ recentX + " ry: " + recentY + " rz: " + recentZ);

            double diff = 3;
            //if(x+diff >= recentX && x-diff <=recentX && y+diff >= recentY && y-diff <=recentY && z+diff >= recentZ && z-diff <=recentZ) {
            if(y+diff >= recentY && y-diff <=recentY) {
                straightBack = true;
                backPositionHelp.setText(context.getString(R.string.task_feedback_back));
                imageBack.setImageDrawable(context.getDrawable(R.drawable.back));
            } else if (y-diff <=recentY){
                backPositionHelp.setText(context.getString(R.string.task_feedback_backUp));
                imageBack.setImageDrawable(context.getDrawable(R.drawable.back_up));
                if(straightBack){
                    counterBackPosition++;
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                }
                straightBack = false;
            } else {
                backPositionHelp.setText(context.getString(R.string.task_feedback_backDown));
                imageBack.setImageDrawable(context.getDrawable(R.drawable.back_down));
                if(straightBack){
                    counterBackPosition++;
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                }
                straightBack = false;
            }
        }
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
            Log.d("SensorTask", "MATH ROUND: " + Math.round(x));
            xTV.setText((int) Math.round(x) + "");
            double y = gyroY * (180.0/Math.PI);
            yTV.setText((int) Math.round(y) + "");
            double z = gyroZ * (180.0/Math.PI);
            zTV.setText((int) Math.round(z) + "");

            /*Log.d("SensorTask", "gX: " + goals[i].getGoalX() + "\t X: " + x);
            Log.d("SensorTask", "gY: " + goals[i].getGoalY() + "\t Y: " + y);
            Log.d("SensorTask", "gZ: " + goals[i].getGoalZ() + "\t Z: " + z);
            Log.d("SensorTask", " ");*/

            if(flag){
                // Positionsanzeige für Benutzer
                // Y-Koordinate
                // Nach Unten kippen
                if (y <= goals[i].getGoalY()-SCOPE) {
                    arrowDown.setVisibility(View.VISIBLE);
                } else {
                    arrowDown.setVisibility(View.INVISIBLE);
                }

                // Nach Oben kippen
                if (y >= goals[i].getGoalY()+SCOPE) {
                    arrowUp.setVisibility(View.VISIBLE);
                } else {
                    arrowUp.setVisibility(View.INVISIBLE);
                }

                // Z-Koordinate
                // Nach Links kippen
                if (z <= goals[i].getGoalZ()-SCOPE) {
                    arrowLeft.setVisibility(View.VISIBLE);
                } else {
                    arrowLeft.setVisibility(View.INVISIBLE);
                }

                // Nach Rechts kippen
                if (z >= goals[i].getGoalZ()+SCOPE){
                    arrowRight.setVisibility(View.VISIBLE);
                } else {
                    arrowRight.setVisibility(View.INVISIBLE);
                }

                this.DURATION = (goals[i].getDuration() * dt)/2;
                xTVTask.setText(Math.round(x) + "");
                yTVTask.setText(Math.round(y) + "");
                zTVTask.setText(Math.round(z) + "");

                if (y >= goals[i].getGoalY()-SCOPE && y <= goals[i].getGoalY()+SCOPE && z >= goals[i].getGoalZ()-SCOPE && z <= goals[i].getGoalZ()+SCOPE) {
                    // Positionsanzeige halten einschalten
                    arrowHold.setVisibility(View.VISIBLE);

                    Log.d("SensorTask2", "HALTEN: " + i);
                    seconds += 1;
                    double temp = (this.DURATION-seconds)*2/dt;
                    tVTimer.setText(temp + "s");
                } else {
                    // Positionsanzeige halten abschalten
                    arrowHold.setVisibility(View.INVISIBLE);

                    seconds = 0;
                    tVTimer.setText("");
                }

                if (seconds == DURATION) {
                    Toast.makeText(context, "Goal erreicht", Toast.LENGTH_LONG).show();
                    taskResultTV.setText(context.getString(R.string.task_feedback_task1) + " " + (i+1) + " " + context.getString(R.string.task_feedback_task2) + " " + goals.length + " " + context.getString(R.string.task_feedback_task3));
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), notification);
                    mp.start();

                    if(i == goals.length-1){
                        taskResultTV.setText(context.getString(R.string.task_feedback_taskAll));
                        saveTask(true);
                        stopTask();
                    }else{
                        seconds = 0;
                        i++;
                        //xTVTaskGoal.setText(Double.toString(goals[i].getGoalX()));
                        yTVTaskGoal.setText(Double.toString(goals[i].getGoalY()));
                        zTVTaskGoal.setText(Double.toString(goals[i].getGoalZ()));
                    }
                }
            }


            timeGyro = System.currentTimeMillis();
        }
    }

    public void calibrate(){
        /*gyroData = new SensorData(0f, 0f, 0f, 0);

        xTVTask.setText("0");
        yTVTask.setText("0");
        zTVTask.setText("0");
        xTV.setText("0");
        yTV.setText("0");
        zTV.setText("0");*/
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public BackPositionData getTempfirsBackPositionDataTop() {
        return tempfirsBackPositionDataTop;
    }

    public void setTempfirsBackPositionDataTop(BackPositionData tempfirsBackPositionDataTop) {
        this.tempfirsBackPositionDataTop = tempfirsBackPositionDataTop;
    }

    public BackPositionData getTempfirsBackPositionDataBot() {
        return tempfirsBackPositionDataBot;
    }

    public void setTempfirsBackPositionDataBot(BackPositionData tempfirsBackPositionDataBot) {
        this.tempfirsBackPositionDataBot = tempfirsBackPositionDataBot;
    }
}
