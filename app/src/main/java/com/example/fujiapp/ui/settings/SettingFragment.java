package com.example.fujiapp.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.R;
import com.example.fujiapp.data.IP_Address;
import com.example.fujiapp.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class SettingFragment {

    private FragmentLoginBinding binding;
    private View root;

    AppCompatActivity appCompatActivity;
    Context context;
    View helpView;
    AlertDialog dialog;
    TextInputEditText tIETipAdress;

    public SettingFragment (AppCompatActivity appCompatActivity, Context context) {
        this.appCompatActivity = appCompatActivity;
        this.context = context;


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        helpView = appCompatActivity.getLayoutInflater().inflate(R.layout.fragment_settings, null);
        tIETipAdress = helpView.findViewById(R.id.tIETipAdress);
        tIETipAdress.setText(MainActivity.ip_address.getIp_Adsress());
        builder.setView(helpView);
        builder.setTitle(R.string.action_settings);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.ip_address.setIp_Adsress(tIETipAdress.getText().toString());
                //File outFile = new File(Environment.getExternalStorageDirectory(), IP_Address.FILENAME);
                ObjectOutputStream out = null;
                FileOutputStream fos;
                try {
                    fos = context.openFileOutput(IP_Address.FILENAME, context.MODE_PRIVATE);
                    out = new ObjectOutputStream(fos);
                    out.writeObject(MainActivity.ip_address);
                    out.close();
                } catch (Exception ex) {
                    MainActivity.ip_address.setIp_Adsress("172.22.202.208");
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        dialog = builder.show();
        dialog.show();
    }
}
