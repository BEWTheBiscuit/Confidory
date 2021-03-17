package com.bisbizkuit.whistalk.MainPages;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage.ChatFrag;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.HomePage_PostDisplay.homeFrag;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.profileFrag;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.searchFrag;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.uploadFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.activity.login.sign_in;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userId;

    public BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;
    private Fragment currentFragment;
    private homeFrag onCreatedHomeFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        progressBar = (ProgressBar) findViewById(R.id.splashLoading);

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                }
                catch (Exception e) { } // Just catch the InterruptedException

                // Now we use the Handler to post back to the main thread
                handler.post(new Runnable() {
                    public void run() {
                        // Set the View's visibility back on the main UI Thread
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();

        //intro activity:
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkSignIn(currentUser);
            }
        };

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        onCreatedHomeFrag = new homeFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.selectedFragView, onCreatedHomeFrag).commit();

    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void checkSignIn(FirebaseUser user) {
        if(user != null){
            Log.d("User","Signed In");
            userId = currentUser.getUid();
        }else {
            Log.d("User", "Not signed in");
            startActivity(new Intent(MainActivity.this, sign_in.class));
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.naviHome:
                            selectedFragment = new homeFrag();
                            break;
                        case R.id.naviSearch:
                            selectedFragment = new searchFrag();
                            break;
                        case R.id.naviUpload:
                            selectedFragment = new uploadFrag();
                            break;
                        case R.id.naviChat:
                            selectedFragment = new ChatFrag();
                            break;
                        case R.id.naviProfile:
                            selectedFragment = new profileFrag(userId);
                            break;
                        default:
                            selectedFragment = null;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.selectedFragView, selectedFragment).commit();
                    currentFragment = selectedFragment;
                    return true;
                }
            };
}
