package com.bisbizkuit.whistalk.activity.login.register.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class emailFrag extends Fragment implements TextWatcher {

    private EditText emailEdt, confirmEmailEdt;
    private Button confirmEmail;

    private String Email, ConfirmEmail;
    private DatabaseReference userReference;

    private FragmentToActivityCommunicator fragmentCommunicator;
    private Context context;

    private Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email, container, false);

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        emailEdt = view.findViewById(R.id.emailSignUp);
        confirmEmailEdt = view.findViewById(R.id.confirmEmailSignUp);
        confirmEmail = view.findViewById(R.id.ConfirmEmail);

        confirmEmail.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                setButtonAnimations(confirmEmail, R.anim.fadeout);
                Email = emailEdt.getText().toString();
                fragmentCommunicator.passEmailToActivity(Email);
            }
        });

        emailEdt.addTextChangedListener(this);
        confirmEmailEdt.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunicator = (FragmentToActivityCommunicator) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void validationEmail(final String email, final String confirmEmail) {

        if (email.equals("")) {
            emailEdt.setError(getResources().getString(R.string.Blanked));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdt.setError(getResources().getString(R.string.emailWrongFormat));
        } else if (!email.equals(confirmEmail)) {
            confirmEmailEdt.setError(getResources().getString(R.string.emailConfirmation));
        } else {
            this.confirmEmail.setBackgroundResource(R.drawable.background_signupbutton);
            this.confirmEmail.setEnabled(true);
            this.confirmEmail.setClickable(true);
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
        Email = emailEdt.getText().toString().trim();
        ConfirmEmail = confirmEmailEdt.getText().toString().trim();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (Email.equals(user.getEmail())) {
                        emailEdt.setError("This email is already used by someone else.");
                        break;
                    } else {
                        validationEmail(Email, ConfirmEmail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
