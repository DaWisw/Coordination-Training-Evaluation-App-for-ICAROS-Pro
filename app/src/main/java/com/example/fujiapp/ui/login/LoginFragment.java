package com.example.fujiapp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fujiapp.R;
import com.example.fujiapp.databinding.FragmentLoginBinding;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //loginViewModel =  new ViewModelProvider(this).get(LoginViewModel.class);
        //binding = FragmentLoginBinding.inflate(inflater, container, false);
        //root = binding.getRoot();

        root = inflater.inflate(R.layout.fragment_login, container, false);

        return root;
    }
}
