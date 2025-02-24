package com.example.fujiapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;

import com.example.fujiapp.data.DbDataSource;

import com.example.fujiapp.data.DbHelper;
import com.example.fujiapp.data.IP_Address;
import com.example.fujiapp.data.TrainingSession;

import com.example.fujiapp.data.User;
import com.example.fujiapp.ui.login.LoginFragment;
import com.example.fujiapp.ui.registration.RegistrationFragment;
import com.example.fujiapp.ui.settings.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fujiapp.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private SensorManager mSensorManager;

    private Button btnLogin;
    private Button btnRegistration;
    private TextInputEditText tiLoginUsername;
    private TextInputEditText tiLoginPassword;

    private MenuItem itemLogout;
    private MenuItem itemSettings;

    private AppCompatActivity appCompatActivity;
    private View view;

    public static User activUser;
    public static MainActivity mainActivity;

    public static IP_Address ip_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = this;

        //this.deleteDatabase(DbHelper.DB_NAME);

        start();
    }

    public void start(){
        view = new LoginFragment().onCreateView(getLayoutInflater(), null, null);

        setContentView(view);

        tiLoginUsername = findViewById(R.id.tiLoginUsername);
        tiLoginPassword = findViewById(R.id.tiLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(tiLoginUsername.getText().toString(), tiLoginPassword.getText().toString());
            }
        });

        btnRegistration = findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationFragment registrationFragment = new RegistrationFragment(appCompatActivity, MainActivity.this);
            }
        });

        ip_address = new IP_Address();
        //File inFile = new File(Environment.getExternalStorageDirectory(), IP_Address.FILENAME);
        ObjectInputStream in = null;
        FileInputStream fis;
        try {
            fis = view.getContext().openFileInput(IP_Address.FILENAME);
            InputStream stream = new BufferedInputStream(fis);
            in = new ObjectInputStream(stream);
            ip_address = (IP_Address) in.readObject();
            fis.close();
        } catch (Exception ex) {
            ip_address.setIp_Adsress("172.22.202.208");
        }
        mainActivity = this;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.action_settings == item.getItemId()) {
            SettingFragment settingFragment = new SettingFragment(appCompatActivity, MainActivity.this);
        } else if (R.id.action_logout == item.getItemId()){
            itemLogout.setVisible(false);
            activUser = null;

            Intent intent = new Intent(view.getContext(), MainActivity.class);
            //intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra(KEY_RESTART_INTENT, nextIntent);
            view.getContext().startActivity(intent);
            if (view.getContext() instanceof Activity) {
                ((Activity) view.getContext()).finish();
            }

            Runtime.getRuntime().exit(0);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        itemLogout = menu.findItem(R.id.action_logout);
        itemLogout.setVisible(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void login(String name, String pw){
        DbDataSource db = new DbDataSource(view.getContext());
        db.openDB();
        User user  = db.getUserByLogIn(name.toLowerCase(), pw.toString());
        db.closeDB();

        Log.d(LOG_TAG, user.toString());
        if(user.getUserName() != null){
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            /*setSupportActionBar(binding.appBarMain.toolbar);
            binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });*/

            BottomNavigationView navView = findViewById(R.id.nav_view);


            /*DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;*/
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_training, R.id.nav_results, R.id.nav_user)
                    .build();
            NavController navController = Navigation.findNavController(appCompatActivity, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(appCompatActivity, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            activUser = user;
            itemLogout.setVisible(true);
        }else{
            Snackbar.make(view, getResources().getString(R.string.login_faild), Snackbar.LENGTH_LONG).setAction("Login", null).show();
        }
    }
}