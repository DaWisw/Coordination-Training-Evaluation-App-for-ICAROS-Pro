package com.example.fujiapp.ui.user;

import android.app.DatePickerDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.R;
import com.example.fujiapp.data.DbDataSource;
import com.example.fujiapp.data.IP_Address;
import com.example.fujiapp.data.User;
import com.example.fujiapp.databinding.FragmentUserBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private FragmentUserBinding binding;

    private View root;

    TextView tVUsername;
    TextInputEditText eTName, eTFirstname, eTHeight, eTWeight;
    EditText eTBirthday;
    final Calendar myCalendar= Calendar.getInstance();
    RadioGroup radioSex;

    Button btnSave;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentUserBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        tVUsername = root.findViewById(R.id.TVUsername);
        eTName = root.findViewById(R.id.tIETName);
        eTFirstname = root.findViewById(R.id.tIETFirstname);
        radioSex = root.findViewById(R.id.radio_sex);
        eTHeight = root.findViewById(R.id.tIETHeight);
        eTWeight = root.findViewById(R.id.tIETWeight);
        eTBirthday = root.findViewById(R.id.eTDBirthday);

        User u = MainActivity.activUser;
        tVUsername.setText(u.getUserName());
        eTFirstname.setText(u.getFirstName());
        eTName.setText(u.getName());
        switch(u.getSex()) {
            case "m":
                ((RadioButton)root.findViewById(R.id.radio_male)).setChecked(true);
                break;
            case "f":
                ((RadioButton)root.findViewById(R.id.radio_female)).setChecked(true);
                break;
            case "d":
                ((RadioButton)root.findViewById(R.id.radio_divers)).setChecked(true);
                break;
        }
        eTHeight.setText(Double.toString(u.getHeight()));
        eTWeight.setText(Double.toString(u.getWeight()));
        eTBirthday.setText(u.getDate());
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
                new DatePickerDialog(root.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSave = root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u.setFirstName(eTFirstname.getText().toString());
                u.setName(eTName.getText().toString());
                switch(radioSex.getCheckedRadioButtonId()) {
                    case R.id.radio_male:
                        u.setSex("m");
                        break;
                    case R.id.radio_female:
                        u.setSex("f");
                        break;
                    case R.id.radio_divers:
                        u.setSex("d");
                        break;
                }
                u.setHeight(Double.parseDouble(eTHeight.getText().toString()));
                u.setWeight(Double.parseDouble(eTWeight.getText().toString()));
                u.setDate(eTBirthday.getText().toString());
                MainActivity.activUser = u;

                DbDataSource db = new DbDataSource(getContext());
                db.openDB();
                db.updateUser(u);
                db.closeDB();
            }
        });

        return root;
    }
    private void updateLabel(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.GERMANY);
        eTBirthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}