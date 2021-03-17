package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.HomePage_PostDisplay;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.MainPages.MainActivity;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.notificationFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.activity.login.sign_in;
import com.bisbizkuit.whistalk.adapters.HomePostDisplayAdapter;
import com.bisbizkuit.whistalk.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class homeFrag extends Fragment {

    private TextView actionBarUsername;
    private ViewPager postViewpager;

    private String currentUserId;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        actionBarUsername = (TextView) view.findViewById(R.id.appGreeting);
        postViewpager = (ViewPager) view.findViewById(R.id.viewPager);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        HomePostDisplayAdapter homePostDisplayAdapter = new HomePostDisplayAdapter(getFragmentManager());
        postViewpager.setAdapter(homePostDisplayAdapter);

        postViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorFrom;
                int colorTo;
                ValueAnimator colorAnimation;
                switch (position) {
                    case 0:
                        if (positionOffset == 0) {
                            colorFrom = getResources().getColor(R.color.colorBlack);
                            colorTo = getResources().getColor(R.color.colorWhite);
                            colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo);
                            colorAnimation.setDuration(180); // milliseconds
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    view.setBackgroundColor((int) animation.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        }
                        break;
                    case 1:
                        if (positionOffset == 0) {
                            colorFrom = getResources().getColor(R.color.colorWhite);
                            colorTo = getResources().getColor(R.color.fillIn);
                            colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo);
                            colorAnimation.setDuration(180); // milliseconds
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    view.setBackgroundColor((int) animation.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getContext(), "Public Posts", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "Private Posts", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        actionBarUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein)
                        .addToBackStack(null).replace(R.id.selectedFragView, new notificationFrag()).commit();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkSignIn(currentUser);
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void setUpTextWelcome(String currentUserId) {
        DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUserId);

        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                actionBarUsername.setText("Welcome back, " + user.getUsername() + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSignIn(FirebaseUser user) {
        if(user != null){
            Log.d("User","Signed In");
            currentUserId = user.getUid();
            setUpTextWelcome(currentUserId);
        }else {
            Log.d("User", "Not signed in");
            startActivity(new Intent(getContext(), sign_in.class));
            getActivity().finish();
        }
    }
}
