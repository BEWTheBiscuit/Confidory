package com.bisbizkuit.whistalk.activity.login.register.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.bisbizkuit.whistalk.SpecialClass.FragmentToActivityCommunicator;
import com.bisbizkuit.whistalk.R;

import java.util.regex.Pattern;

public class passwordFrag extends Fragment implements TextWatcher {

    private EditText passwordEdt, confirmPasswordEdt;
    private Button confirmPassword;

    private String Password, ConfirmPassword;

    private Context context;
    private FragmentToActivityCommunicator fragmentCommunicator;

    private Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password, container, false);

        passwordEdt = view.findViewById(R.id.passwordSignUp);
        confirmPasswordEdt = view.findViewById(R.id.confirmPasswordSignUp);
        confirmPassword = view.findViewById(R.id.ConfirmPassword);

        confirmPassword.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonAnimations(confirmPassword, R.anim.fadeout);
                Password = passwordEdt.getText().toString();
                fragmentCommunicator.passPasswordToActivity(Password);
            }
        });

        passwordEdt.addTextChangedListener(this);
        confirmPasswordEdt.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunicator = (FragmentToActivityCommunicator) context;
    }

    private void validationPassword(String password, String confirmPassword) {
        if (TextUtils.isEmpty(password)) {
            passwordEdt.setError(getResources().getString(R.string.Blanked));
        } else if (password.length() < 8) {
            passwordEdt.setError(getResources().getString(R.string.passwordLength));
        } else if (!Pattern.compile("[a-zA-Z]").matcher(password).find()) {
            passwordEdt.setError(getResources().getString(R.string.passwordLetter));
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            passwordEdt.setError(getResources().getString(R.string.passwordNumber));
        } else if (password.contains(" ")) {
            passwordEdt.setError(getResources().getString(R.string.passwordBlankSpace));
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordEdt.setError(getResources().getString(R.string.passwordConfirmation));
        } else {
            this.confirmPassword.setEnabled(true);
            this.confirmPassword.setClickable(true);
            this.confirmPassword.setBackground(getResources().getDrawable(R.drawable.background_signupbutton));
        }
    }

    private void setButtonAnimations(Button button, int id) {
        animation = AnimationUtils.loadAnimation(getContext(),id);
        button.startAnimation(animation);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Password = passwordEdt.getText().toString();
        ConfirmPassword = confirmPasswordEdt.getText().toString();
        validationPassword(Password, ConfirmPassword);
    }
}
