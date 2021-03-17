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
import com.bisbizkuit.whistalk.objects.User;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class namesFrag extends Fragment implements TextWatcher {

    private EditText usernameEdt;
    private Button confirmName;

    private String Username;
    private DatabaseReference userReference;

    private FragmentToActivityCommunicator fragmentCommunicator;
    private Context context;

    private ProgressDialog progressDialog;
    private Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_names, container, false);

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        usernameEdt = view.findViewById(R.id.usernameSignUp);
        confirmName = view.findViewById(R.id.ConfirmName);

        confirmName.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                setButtonAnimations(confirmName, R.anim.fadeout);
                fragmentCommunicator.passUsernameToActivity(Username);
            }
        });

        usernameEdt.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunicator = (FragmentToActivityCommunicator) context;
    }

    private void validationUsername(String username) {
        if (username.contains(" ")) {
            usernameEdt.setError(getResources().getString(R.string.usernameBlankSpace));
        } else if (TextUtils.isEmpty(username)) {
            usernameEdt.setError(getResources().getString(R.string.Blanked));
        } else if (username.equals("")) {
            usernameEdt.setError(getResources().getString(R.string.Blanked));
        } else if (username.equals("Confidory Official")) {
            usernameEdt.setError(getResources().getString(R.string.usernameInvalid));
        } else {
            confirmName.setClickable(true);
            confirmName.setEnabled(true);
            confirmName.setBackground(getResources().getDrawable(R.drawable.background_signupbutton));
        }
    }

    private void setButtonAnimations(Button button, int id) {
        animation = AnimationUtils.loadAnimation(getContext(),id);
        button.startAnimation(animation);
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Username = usernameEdt.getText().toString();
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    setProgressDialog();
                    User user = snapshot.getValue(User.class);
                    if (Username.equals(user.getUsername())) {
                        usernameEdt.setError(getResources().getString(R.string.usernameExisted));
                        confirmName.setClickable(false);
                        confirmName.setEnabled(false);
                        confirmName.setBackground(getResources().getDrawable(R.drawable.background_disabledsignupbutton));
                        break;
                    } else {
                        validationUsername(Username);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
