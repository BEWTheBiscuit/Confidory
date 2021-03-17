package com.bisbizkuit.whistalk.activity.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.activity.login.register.register;

import com.bisbizkuit.whistalk.MainPages.MainActivity;
import com.bisbizkuit.whistalk.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class sign_in extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 000;
    private String name, last_name, first_name, email, image_url, password;
    private String UId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;

    private SignInButton GoogleSignInButton;
    private EditText Email;
    private EditText Password;
    private Button signIn;
    private Button createAccount;
    private ImageView appIcon;
    private TextView termOfPolicySignIn;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        Email = (EditText) findViewById(R.id.usernameSignIn);
        Password = (EditText) findViewById(R.id.passwordSignIn);
        signIn = (Button) findViewById(R.id.signInWithPassword);
        createAccount = (Button) findViewById(R.id.CreateAccount);
        appIcon = (ImageView) findViewById(R.id.appIcon);
        termOfPolicySignIn = (TextView) findViewById(R.id.TermOfPolicySignIn);
        layout = (View) findViewById(R.id.signInLayout);

        Animation fromBottomSlow = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        Animation fromBottomFast = AnimationUtils.loadAnimation(this, R.anim.from_bottom2nd);
        layout.setAnimation(fromBottomSlow);
        appIcon.setAnimation(fromBottomFast);

        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/app_icon.png?alt=media&token=a353adda-bd59-497a-b1a9-191fa207f8f4")
                .into(appIcon);

        createAccount.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
                createAccount.startAnimation(animation);
                Intent intent = new Intent();
                intent.setClass(sign_in.this,register.class);
                String backPage = "SIGN_IN";
                Bundle bundle = new Bundle();
                bundle.putString("backPage",backPage);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        signIn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
                signIn.startAnimation(animation);
                SignIn();
            }
        });

        GoogleSignInButton = (SignInButton) findViewById(R.id.googleSignIn);
        GoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInButton.setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetGoogleSignIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        setPolicyClick();
    }

    private void SetGoogleSignIn() {
        Intent SignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    firebaseWithGoogle(googleSignInAccount);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void firebaseWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            user = firebaseAuth.getCurrentUser();
                            if (acct != null) {
                                name = acct.getDisplayName();
                                last_name = acct.getFamilyName();
                                first_name = acct.getGivenName();
                                email = acct.getEmail();
                                image_url = "";
                                password = "GoogleSignIn";
                            }
                            dataSetUp(name, last_name, first_name, email, image_url);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.errorSignInEmailInvalid), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SignIn(){
        final String emailSignIn = Email.getText().toString();
        final String passwordSignIn = Password.getText().toString();
        if(TextUtils.isEmpty(emailSignIn) || TextUtils.isEmpty(passwordSignIn)){
            Log.d("Sign In","Sign In Successful");
        }else{
            firebaseAuth.signInWithEmailAndPassword(emailSignIn, passwordSignIn)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(sign_in.this, "Sign In Unsuccessful", Toast.LENGTH_LONG).show();
                            }else{
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                checkSignIn(user);
                                finish();
                            }
                        }
                    });
        }
    }

    private void dataSetUp(final String name, final String last_name, final String first_name, final String email, final String image_url){

        UId = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UId);

        DatabaseReference checkReference = FirebaseDatabase.getInstance().getReference("Users");

        checkReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(UId).exists()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("username", name);
                    hashMap.put("first_name", first_name);
                    hashMap.put("last_name", last_name);
                    hashMap.put("email", email);
                    hashMap.put("id", UId);
                    hashMap.put("bio", "");
                    if (password.equals("GoogleSignIn")) {
                        hashMap.put("password","google sign in");
                        hashMap.put("icon", "");
                    }

                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse(image_url))
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

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Data","Data set up");
                            checkSignIn(user);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "An account is existed with this email.", Toast.LENGTH_LONG).show();
                            Log.d("Data","Data set up unsuccessful");
                        }
                    });
                } else {
                    checkSignIn(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkSignIn(FirebaseUser user) {
        if(user != null){
            Intent intent = new Intent(sign_in.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Email.requestFocus();
            Password.requestFocus();
        }
    }

    private void setPolicyClick() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.TermOfPolicy_SignIn));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(sign_in.this, termofpolicy.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, 29, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorFinish)), 29, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termOfPolicySignIn.setText(spannableString);
        termOfPolicySignIn.setMovementMethod(LinkMovementMethod.getInstance());
    }

}