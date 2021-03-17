package com.bisbizkuit.whistalk.activity.login.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.SpecialClass.FragmentToActivityCommunicator;
import com.bisbizkuit.whistalk.MainPages.MainActivity;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.SpecialClass.SwipeDisabledViewPager;
import com.bisbizkuit.whistalk.adapters.SignUpViewPagerAdapter;
import com.bisbizkuit.whistalk.activity.login.sign_in;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity implements View.OnClickListener, FragmentToActivityCommunicator {

    private SwipeDisabledViewPager SignUpViewPager;
    private ImageView CancelCross;
    private Button Back;
    private TabLayout Dots;

    private String backPage;
    private int position;
    private String Username;
    private String Firstname;
    private String Lastname;
    private String Email;
    private String Password;
    private Boolean Accept;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            backPage = bundle.getString("backPage");
        } else {
            Log.d("Error", "Null Bundle");
        }

        SignUpViewPager = (SwipeDisabledViewPager) findViewById(R.id.SignUpViewPager);

        CancelCross = (ImageView) findViewById(R.id.cancelCross);
        Back = (Button) findViewById(R.id.lastPage);
        Dots = (TabLayout) findViewById(R.id.dots);

        SignUpViewPager.setPagingEnabled(false);

        Back.setEnabled(false);
        Back.setTextColor(getResources().getColor(R.color.colorTransparentWhite));

        CancelCross.setOnClickListener(this);
        Back.setOnClickListener(this);

        SignUpViewPagerAdapter signUpViewPagerAdapter = new SignUpViewPagerAdapter(getSupportFragmentManager());
        SignUpViewPager.setAdapter(signUpViewPagerAdapter);
        Dots.setupWithViewPager(SignUpViewPager);

        buttonProperties();
    }

    private void setCancel() {
        if (backPage.equals("SIGN_IN")) {
            startActivity(new Intent(register.this, sign_in.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lastPage:
                if (position > 0) {
                    position--;
                    SignUpViewPager.setCurrentItem(position);
                    buttonProperties();
                }
                break;
            case R.id.cancelCross:
                setCancel();
                break;
        }
    }

    private void buttonProperties() {
        if (position < Dots.getTabCount() - 1 && position > 0) {
            Back.setTextColor(getResources().getColor(R.color.colorWhite));
            Back.setEnabled(true);
        } else if (position == 0) {
            Back.setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            Back.setEnabled(false);
        }
    }

    @Override
    public void passUsernameToActivity(String username) {
        this.Username = username;
        Log.d("Username", Username);
        position = SignUpViewPager.getCurrentItem() + 1;
        SignUpViewPager.setCurrentItem(position);
        buttonProperties();
    }

    @Override
    public void passFullnameToActivity(String firstname, String lastname) {
        this.Lastname = lastname;
        this.Firstname = firstname;
        Log.d("Firstname", Firstname);
        Log.d("Lastname", Lastname);
        position = SignUpViewPager.getCurrentItem() + 1;
        SignUpViewPager.setCurrentItem(position);
        buttonProperties();
    }

    @Override
    public void passEmailToActivity(String email) {
        this.Email = email;
        Log.d("Email", Email);
        position = SignUpViewPager.getCurrentItem() + 1;
        SignUpViewPager.setCurrentItem(position);
        buttonProperties();
    }

    @Override
    public void passPasswordToActivity(String password) {
        this.Password = password;
        Log.d("Password", Password);
        position = SignUpViewPager.getCurrentItem() + 1;
        SignUpViewPager.setCurrentItem(position);
        buttonProperties();
    }

    @Override
    public void passCreateBooleanToActivity(Boolean accept) {
        Accept = accept;
        signUp(Email,Password,Username,Firstname,Lastname, Accept);
    }

    private void signUp(final String email, final String password, final String username, final String firstname, final String lastname, Boolean accept) {
        if (accept) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog = new ProgressDialog(getApplicationContext());
                    progressDialog.setCancelable(true);
                    if (task.isSuccessful()) {

                        int reported = 0;

                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        String userId = user.getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", username);
                        hashMap.put("first_name", firstname);
                        hashMap.put("last_name", lastname);
                        hashMap.put("email", email);
                        hashMap.put("id", userId);
                        hashMap.put("bio", "");
                        hashMap.put("password", password);
                        hashMap.put("icon", "");

                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        user.updateProfile(changeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG","Profile updated");
                                        }
                                    }
                                });

                        user.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "User email address updated.");
                                        }
                                    }
                                });

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Data","Data set up");
                                onSignUpComplete(user);
                            }
                        });
                    } else {
                        Log.d("SignUp", "Unsuccessful");
                    }
                }
            });

        }
    }

    private void onSignUpComplete(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent();
            intent.setClass(register.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("SignUp", "Unsuccessful");
            Toast.makeText(this, "Sign Up unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

}
