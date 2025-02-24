package com.example.fujiapp.ui.registration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.R;
import com.example.fujiapp.data.DbDataSource;
import com.example.fujiapp.data.User;
import com.example.fujiapp.databinding.FragmentRegistrationBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class RegistrationFragment {

    private FragmentRegistrationBinding binding;
    private View root;

    TextInputEditText eTUsername, eTName, eTFirstname, eTHeight, eTWeight, eTPassword;
    EditText eTBirthday;
    final Calendar myCalendar= Calendar.getInstance();
    RadioGroup radioSex;
    String sex = "";

    private Button btnClose;
    private Button btnSave;
    CheckBox checkBox;

    AppCompatActivity appCompatActivity;
    Context context;
    View helpView;
    Button button;
    AlertDialog dialog;

    public RegistrationFragment (AppCompatActivity appCompatActivity, Context context){
        this.appCompatActivity = appCompatActivity;
        this.context = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        helpView = appCompatActivity.getLayoutInflater().inflate(R.layout.fragment_registration, null);
        builder.setView(helpView);
        builder.setTitle("Registrierung");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegistUser();
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
        button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        dialog.show();
        button.setEnabled(false);


        checkBox    = helpView.findViewById(R.id.checkBox);
        eTUsername  = helpView.findViewById(R.id.tIETUsername);
        eTName      = helpView.findViewById(R.id.tIETName);
        eTFirstname = helpView.findViewById(R.id.tIETFirstname);
        radioSex    = helpView.findViewById(R.id.radio_sex);
        eTHeight    = helpView.findViewById(R.id.tIETHeight);
        eTWeight    = helpView.findViewById(R.id.tIETWeight);
        eTBirthday  = helpView.findViewById(R.id.eTDBirthday);
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        eTBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(helpView.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        eTPassword  = helpView.findViewById(R.id.tIETPassword);

        setListener();
    }

    private void updateLabel(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.GERMANY);
        eTBirthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setListener() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tryEnableSaveButton();
            }
        });

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tryEnableSaveButton();
            }
        };

        eTUsername .addTextChangedListener(tw);
        eTName     .addTextChangedListener(tw);
        eTFirstname.addTextChangedListener(tw);
        eTHeight   .addTextChangedListener(tw);
        eTWeight   .addTextChangedListener(tw);
        eTBirthday .addTextChangedListener(tw);
        eTPassword .addTextChangedListener(tw);

        radioSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    //RadioButton rb = helpView.findViewById(radioGroup.getCheckedRadioButtonId());
                    switch(radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_male:
                            sex = "m";
                            break;
                        case R.id.radio_female:
                            sex = "f";
                            break;
                        case R.id.radio_divers:
                            sex = "d";
                            break;
                    }
                }
                tryEnableSaveButton();
            }
        });
    }

    private void RegistUser() {
        DbDataSource db = new DbDataSource(context);
        db.openDB();

        if(db.containsUsername(eTUsername.getText().toString())){
            Toast.makeText(context, context.getResources().getString(R.string.registraion_UsernameExist),Toast.LENGTH_LONG).show();
        }else {
            User u = new User();
            u.setUserName(eTUsername.getText().toString());
            u.setFirstName(eTFirstname.getText().toString());
            u.setSex(sex);
            u.setName(eTName.getText().toString());
            u.setPassword(eTPassword.getText().toString());
            u.setHeight(Double.parseDouble(eTHeight.getText().toString()));
            u.setWeight(Double.parseDouble(eTWeight.getText().toString()));
            u.setDate(eTBirthday.getText().toString());
            MainActivity.activUser = u;

            db.insertUser(u);
            MainActivity.mainActivity.login(u.getName(), u.getPassword());
        }

        db.closeDB();
    }

    private void tryEnableSaveButton(){
        boolean b1 = eTUsername.getText().toString().trim().equals("") ? false : true;
        boolean b2 = eTName.getText().toString().trim().equals("") ? false : true;
        boolean b3 = eTFirstname.getText().toString().trim().equals("") ? false : true;
        boolean b4 = sex.equals("") ? false : true;
        boolean b5 = eTHeight.getText().toString().trim().equals("") ? false : true;
        boolean b6 = eTWeight.getText().toString().trim().equals("") ? false : true;
        boolean b7 = eTBirthday.getText().toString().trim().equals("") ? false : true;
        boolean b8 = eTPassword.getText().toString().trim().equals("") ? false : true;
        boolean b9 = checkBox.isChecked();
        if(b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9)
            button.setEnabled(true);
        else
            button.setEnabled(false);
    }
}
