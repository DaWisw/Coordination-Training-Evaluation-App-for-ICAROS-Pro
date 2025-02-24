package com.example.fujiapp.ui.training;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fujiapp.R;
import com.example.fujiapp.connection.position.BackPositionData;
import com.example.fujiapp.data.Goal;
import com.example.fujiapp.databinding.FragmentTrainingBinding;
import com.example.fujiapp.function.Training_Task;
import com.example.fujiapp.services.HealthDataService;
import com.example.fujiapp.services.BackPositionDataService;

import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment implements SensorEventListener {

    private TrainingViewModel trainingViewModel;
    private FragmentTrainingBinding binding;
    private SensorManager sensorManager;
    private Sensor sensorGyro;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private float[] erg = new float[3];

    private View root;
    Training_Task task;
    private TextView heartRateTextView;
    private TextView respirationRateTextView;
    private ImageView btConnectedStatusIV;
    private TextView positionTextView, positionTextViewY, positionTextViewZ;
    private ImageView positionConnectedStatusIV;

    private ConnectedStatusDrawable btConnectedStatusDrawable;
    private DisconnectedStatusDrawable btDisconnectedStatusDrawable;

    private ConnectedStatusDrawable positionConnectedStatusDrawable;
    private DisconnectedStatusDrawable positionDisconnectedStatusDrawable;

    Button btnStartTask, btnStopTask, btnCalibrate;

    private HealthDataService healthDataService;
    private BackPositionDataService positionDataService;

    public static BackPositionData firstBackPositionTop;
    public static BackPositionData firstBackPositionBot;

    public static BackPositionData recentBackPositionTop;
    public static BackPositionData recentBackPositionBot;

    private Spinner spinTasks;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trainingViewModel =
                new ViewModelProvider(this).get(TrainingViewModel.class);

        binding = FragmentTrainingBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        sensorManager = (SensorManager)root.getContext().getSystemService(root.getContext().SENSOR_SERVICE);
        //sensorGyro = SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        btnStartTask = root.findViewById(R.id.btnStartTask);
        btnStopTask = root.findViewById(R.id.btnStopTask);
        btnCalibrate = root.findViewById(R.id.btnCalibrate);
        heartRateTextView = root.findViewById(R.id.PulseTVvalue);
        respirationRateTextView = root.findViewById(R.id.RespirationTVvalue);
        btConnectedStatusIV = root.findViewById(R.id.btConnectedStatusIV);
        positionTextView = root.findViewById(R.id.positionTVvalue);
        positionTextViewY = root.findViewById(R.id.positionTVvalueY);
        positionTextViewZ = root.findViewById(R.id.positionTVvalueZ);
        positionConnectedStatusIV = root.findViewById(R.id.positionConnectedStatusIV);

        spinTasks = root.findViewById(R.id.spinTasks);

        btConnectedStatusDrawable = new ConnectedStatusDrawable();
        btDisconnectedStatusDrawable = new DisconnectedStatusDrawable();
        btConnectedStatusIV.setImageDrawable(btDisconnectedStatusDrawable);

        positionConnectedStatusDrawable = new ConnectedStatusDrawable();
        positionDisconnectedStatusDrawable = new DisconnectedStatusDrawable();
        positionConnectedStatusIV.setImageDrawable(positionDisconnectedStatusDrawable);


        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Task 1");
        spinnerArray.add("Task 2");
        spinnerArray.add("Task 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTasks.setAdapter(adapter);

        btnStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(root.getContext(),"Button start", Toast.LENGTH_SHORT).show();
                Goal goal1 = new Goal(25,0,10);
                Goal goal2 = new Goal(15,10,7);
                Goal goal3 = new Goal(15,-10,7);
                Goal goal4 = new Goal(20,5,7);
                Goal goal5 = new Goal(20,-5,7);
                Goal goal6 = new Goal(10,15,7);
                Goal goal7 = new Goal(10,-15,7);
                if(spinTasks.getSelectedItem().toString().equals("Task 1")){
                    Goal[] goals = {goal1, goal2, goal3, goal4, goal5, goal6, goal7};
                    task.startTask(goals, "Task 1" );
                }else if(spinTasks.getSelectedItem().toString().equals("Task 2")){
                    Goal[] goals = {goal1, goal5, goal4, goal7, goal6, goal3, goal2};
                    task.startTask(goals, "Task 2" );
                }
                else{
                    Goal[] goals = {goal1, goal6, goal7, goal2, goal3, goal4, goal5};
                    task.startTask(goals, "Task 3");
                }

                root.setKeepScreenOn(true);

                btnCalibrate.setEnabled(false);
                btnStopTask.setEnabled(true);
                btnStartTask.setEnabled(false);
            }
        });
        btnStopTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(root.getContext(),"Button stop", Toast.LENGTH_SHORT).show();
                task.saveTask(false);
                task.stopTask();
            }
        });

        btnCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.stopTask();
                task = new Training_Task(sensorManager, root.getContext());
            }
        });

        return root;
    }
    public void onSensorChanged(SensorEvent event) {
        //Log.d("Sensor","Test");
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        //SensorManager.registerListener(this, sensorGyro, 10000);
        task = new Training_Task(sensorManager, root.getContext());

        getActivity().bindService(new Intent(getActivity(), HealthDataService.class), healthDataServiceConnection, Context.BIND_AUTO_CREATE);
        getActivity().bindService(new Intent(getActivity(), BackPositionDataService.class), backPositionDataServiceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter healthDataIntentFilter = new IntentFilter(HealthDataService.HEALTH_META_DATA_UPDATE);
        getActivity().registerReceiver(healthMetaDataBroadcastReceiver, healthDataIntentFilter);

        IntentFilter backPositionDataIntentFilter = new IntentFilter(BackPositionDataService.BACK_POSITION_DATA_UPDATE);
        getActivity().registerReceiver(backPositionDataReceiver, backPositionDataIntentFilter);

        IntentFilter bluetoothConnectedIntentFilter = new IntentFilter(HealthDataService.BLUETOOTH_CONNECTED);
        getActivity().registerReceiver(bluetoothConnectedReceiver, bluetoothConnectedIntentFilter);

        IntentFilter backPositionSocketConnectedIntentFilter = new IntentFilter(BackPositionDataService.BACK_POSITION_SOCKET_CONNECTED);
        getActivity().registerReceiver(backPositionSocketConnectedReceiver, backPositionSocketConnectedIntentFilter);

        IntentFilter backPositionSocketDisconnectedIntentFilter = new IntentFilter(BackPositionDataService.BACK_POSITION_SOCKET_DISCONNECTED);
        getActivity().registerReceiver(backPositionDisconnectedReceiver, backPositionSocketDisconnectedIntentFilter);

        btnStopTask.setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        task.stopTask();
        getActivity().unbindService(healthDataServiceConnection);
        getActivity().unbindService(backPositionDataServiceConnection);
        getActivity().unregisterReceiver(healthMetaDataBroadcastReceiver);
        getActivity().unregisterReceiver(backPositionDataReceiver);
        getActivity().unregisterReceiver(backPositionDisconnectedReceiver);
        getActivity().unregisterReceiver(bluetoothConnectedReceiver);
        root.setKeepScreenOn(false);
        // SensorManager.unregisterListener(this);
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    BroadcastReceiver healthMetaDataBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String heartRate = (String) intent.getExtras().get("heart_rate");
            String respirationRate = (String) intent.getExtras().get("respiration_rate");

            task.setValues(Integer.parseInt(heartRate), Double.parseDouble(respirationRate));

            heartRateTextView.setText(heartRate);
            respirationRateTextView.setText(respirationRate);
        }
    };

    BroadcastReceiver backPositionDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(positionConnectedStatusIV.getDrawable() == positionDisconnectedStatusDrawable)
                positionConnectedStatusIV.setImageDrawable(positionConnectedStatusDrawable);
            //TODO: Tessa
            BackPositionData backPositionData = intent.getParcelableExtra(BackPositionDataService.BACK_POSITION_DATA);
            positionTextView.setText(backPositionData.toString());
            positionTextViewY.setText("Euler y: " + backPositionData.getEy());
            positionTextViewZ.setText("Euler z: " + backPositionData.getEz());

            if(backPositionData.getTracker().equals("top")){
                task.setTempfirsBackPositionDataTop(backPositionData);
                recentBackPositionTop = backPositionData;
            }
            if(backPositionData.getTracker().equals("bottom")){
                task.setTempfirsBackPositionDataBot(backPositionData);
                recentBackPositionBot = backPositionData;
            }
            evaluateBackPosition();
        }
    };

    BroadcastReceiver bluetoothConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btConnectedStatusIV.setImageDrawable(btConnectedStatusDrawable);
        }
    };

    BroadcastReceiver backPositionSocketConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            positionConnectedStatusIV.setImageDrawable(positionConnectedStatusDrawable);
        }
    };

    BroadcastReceiver backPositionDisconnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            positionConnectedStatusIV.setImageDrawable(btDisconnectedStatusDrawable);
            positionTextView.setText("-");
        }
    };

    private ServiceConnection healthDataServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            healthDataService = ((HealthDataService.HealthDataBinder) iBinder).getService();
            healthDataService.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            healthDataService.disconnect();
            healthDataService = null;
        }
    };

    private ServiceConnection backPositionDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            positionDataService = ((BackPositionDataService.BackPositionDataBinder) iBinder).getService();
            positionDataService.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            positionDataService.disconnect();
            positionDataService = null;
        }
    };

    private void evaluateBackPosition(){
        if(recentBackPositionBot != null && recentBackPositionTop != null){
            task.backPosition(recentBackPositionTop, recentBackPositionBot);
            //Log.d("PositionRecent", recentBackPositionTop.toString());
        }



    }
}