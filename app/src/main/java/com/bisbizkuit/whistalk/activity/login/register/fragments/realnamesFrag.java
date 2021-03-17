package com.bisbizkuit.whistalk.activity.login.register.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class realnamesFrag extends Fragment implements TextWatcher {

    private EditText firstnameEdt, lastnameEdt;
    private Button confirmFullname;

    private String Firstname, Lastname, ValidatedFirstname, ValidatedLastname;

    private FragmentToActivityCommunicator fragmentCommunicator;
    private Context context;

    private ProgressDialog progressDialog;
    private Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_realnames, container, false);

        firstnameEdt = view.findViewById(R.id.firstnameSignUp);
        lastnameEdt = view.findViewById(R.id.lastnameSignUp);
        confirmFullname = view.findViewById(R.id.ConfirmFullname);

        confirmFullname.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                setButtonAnimations(confirmFullname, R.anim.fadeout);
                fragmentCommunicator.passFullnameToActivity(Firstname, Lastname);
            }
        });

        firstnameEdt.addTextChangedListener(this);
        lastnameEdt.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunicator = (FragmentToActivityCommunicator) context;
    }

    private void setButtonAnimations(Button button, int id) {
        animation = AnimationUtils.loadAnimation(getContext(),id);
        button.startAnimation(animation);
    }

    private void validationFullname(String firstname, String lastname) {

        ValidatedFirstname = firstname.replaceAll(" ","");
        ValidatedLastname = lastname.replaceAll(" ","");

        if (TextUtils.isEmpty(ValidatedFirstname)) {
            firstnameEdt.setError(getResources().getString(R.string.Blanked));
        } else if (Pattern.compile("[0-9]").matcher(ValidatedFirstname).find()) {
            firstnameEdt.setError(getResources().getString(R.string.firstnameAvailability));
        } else if (checkSymbolExist(ValidatedFirstname)) {
            firstnameEdt.setError(getResources().getString(R.string.firstnameAvailability));
        } else if (TextUtils.isEmpty(ValidatedLastname)) {
            lastnameEdt.setError(getResources().getString(R.string.Blanked));
        } else if (Pattern.compile("[0-9]").matcher(ValidatedLastname).find()) {
            lastnameEdt.setError(getResources().getString(R.string.lastnameAvailability));
        } else if (checkSymbolExist(ValidatedLastname)) {
            lastnameEdt.setError(getResources().getString(R.string.lastnameAvailability));
        } else {
            confirmFullname.setBackground(getResources().getDrawable(R.drawable.background_signupbutton));
            confirmFullname.setClickable(true);
            confirmFullname.setEnabled(true);
        }
    }

    private boolean checkSymbolExist(String checkValue) {
        if (checkValue.contains("@") || checkValue.contains("!") || checkValue.contains("#") ||
                checkValue.contains("$") || checkValue.contains("%") || checkValue.contains("^") ||
                checkValue.contains("&") || checkValue.contains("*") || checkValue.contains("(") ||
                checkValue.contains(")") || checkValue.contains("?") || checkValue.contains("/")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Firstname = firstnameEdt.getText().toString();
        Lastname = lastnameEdt.getText().toString();
        validationFullname(Firstname, Lastname);
    }
}
