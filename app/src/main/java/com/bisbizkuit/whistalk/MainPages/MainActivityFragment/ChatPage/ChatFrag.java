package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bisbizkuit.whistalk.MainPages.setting.follow_detailFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.adapters.ChatTypeAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatFrag extends Fragment {

    private ViewPager viewPager;
    private AppBarLayout requestAppBar;
    private EditText searchChat;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        searchChat = (EditText) view.findViewById(R.id.searchChat);
        viewPager = (ViewPager) view.findViewById(R.id.chatListViewPager);
        requestAppBar = (AppBarLayout) view.findViewById(R.id.requestChatAppBarLayout);
        ChatTypeAdapter chatTypeAdapter = new ChatTypeAdapter(getFragmentManager());
        viewPager.setAdapter(chatTypeAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        Toast.makeText(getContext(), "Public  chat", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "Private chat", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkRequest();

        return view;
    }

    private void viewPagerAttributeChange() {
        if (requestAppBar.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.requestChatAppBarLayout);
            viewPager.setLayoutParams(params);

            requestAppBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null).add(R.id.selectedFragView, new follow_detailFrag("CHAT_REQUEST")).commit();
                }
            });
        } else {
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.searchAppBarLayout);
            viewPager.setLayoutParams(params);
        }
    }

    private void checkRequest() {
        DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid());
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    requestAppBar.setVisibility(View.VISIBLE);
                    viewPagerAttributeChange();
                } else {
                    viewPagerAttributeChange();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
