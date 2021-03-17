package com.bisbizkuit.whistalk.MainPages.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settingFrag extends Fragment {

    Switch postPri, bioPri, followPri;
    Switch requestFollowerAnon, requestFollowingAnon, requestGuestsAnon, requestFollowerNonAnon, requestFollowingNonAnon, requestGuestsNonAnon;
    TextView confirm, cancel, settingTitle, privacySubtitle, senarioChat, senarioPrivacy;
    View privacySenario, chatSenario, generalSenario;
    ImageView backGeneralSenario;

    Context context;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        postPri = (Switch) view.findViewById(R.id.showPost);
        bioPri = (Switch) view.findViewById(R.id.showBio);
        followPri = (Switch) view.findViewById(R.id.showFollow);
        requestGuestsAnon = (Switch) view.findViewById(R.id.requestFromGuestsAnonymous);
        requestFollowerAnon = (Switch) view.findViewById(R.id.requestFromFollowerAnonymous);
        requestFollowingAnon = (Switch) view.findViewById(R.id.requestFromFollowingAnonymous);
        requestFollowingNonAnon = (Switch) view.findViewById(R.id.requestFromFollowingNonAnonymous);
        requestFollowerNonAnon = (Switch) view.findViewById(R.id.requestFromFollowerNonAnonymous);
        requestGuestsNonAnon = (Switch) view.findViewById(R.id.requestFromGuestsNonAnonymous);

        backGeneralSenario = (ImageView) view.findViewById(R.id.backGeneralSenario);
        generalSenario = (View) view.findViewById(R.id.generalSenario);
        confirm = (TextView) view.findViewById(R.id.confirmPrivacy);
        cancel = (TextView) view.findViewById(R.id.cancelPrivacy);
        settingTitle = (TextView) view.findViewById(R.id.settingTxt);
        privacySubtitle = (TextView) view.findViewById(R.id.privacyTxt);
        privacySenario = (View) view.findViewById(R.id.PrivacySenario);
        chatSenario = (View) view.findViewById(R.id.ChatSenario);
        senarioChat = (TextView) view.findViewById(R.id.senarioChat);
        senarioPrivacy = (TextView) view.findViewById(R.id.senarioPrivacy);

        setUpSwitches();

        senarioPrivacy.setOnClickListener(senarioOnClickListener);
        senarioChat.setOnClickListener(senarioOnClickListener);

        postPri.setOnCheckedChangeListener(checkedChangeListener);
        bioPri.setOnCheckedChangeListener(checkedChangeListener);
        followPri.setOnCheckedChangeListener(checkedChangeListener);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
        settingTitle.setTypeface(typeface);
        privacySubtitle.setTypeface(typeface);

        return view;
    }

    private void updatePrivacy(boolean postPri, boolean bioPri, boolean followPri) {
        DatabaseReference privacyReference = FirebaseDatabase.getInstance().getReference("Setting")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Privacy");
        if (bioPri) {
            privacyReference.child("bio").setValue(true);
        } else {
            privacyReference.child("bio").removeValue();
        }

        if (postPri) {
            privacyReference.child("posts").setValue(true);
        } else {
            privacyReference.child("posts").removeValue();
        }

        if (followPri) {
            privacyReference.child("follow").setValue(true);
        } else {
            privacyReference.child("follow").removeValue();
        }
    }

    private void updateChatRequestSetting(boolean anonFollower, boolean anonFollowing, boolean anonGuests,
                                          boolean nonAnonFollower, boolean nonAnonFollowing, boolean nonAnonGuests) {
        DatabaseReference chatSettingReference = FirebaseDatabase.getInstance().getReference("Setting")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chat");
        if (anonFollower) {
            chatSettingReference.child("Anonymous").child("Request_From_Follower").setValue(true);
        } else {
            chatSettingReference.child("Anonymous").child("Request_From_Follower").removeValue();
        }
        if (anonFollowing) {
            chatSettingReference.child("Anonymous").child("Request_From_Following").setValue(true);
        } else {
            chatSettingReference.child("Anonymous").child("Request_From_Following").removeValue();
        }
        if (anonGuests) {
            chatSettingReference.child("Anonymous").child("Request_From_Guests").setValue(true);
        } else {
            chatSettingReference.child("Anonymous").child("Request_From_Guests").removeValue();
        }
        if (nonAnonFollower) {
            chatSettingReference.child("Non-anonymous").child("Request_From_Follower").setValue(true);
        } else {
            chatSettingReference.child("Non-anonymous").child("Request_From_Follower").removeValue();
        }
        if (nonAnonFollowing) {
            chatSettingReference.child("Non-anonymous").child("Request_From_Following").setValue(true);
        } else {
            chatSettingReference.child("Non-anonymous").child("Request_From_Following").removeValue();
        }
        if (nonAnonGuests) {
            chatSettingReference.child("Non-anonymous").child("Request_From_Guests").setValue(true);
        } else {
            chatSettingReference.child("Non-anonymous").child("Request_From_Guests").removeValue();
        }
    }

    private void setUpSwitches() {
        DatabaseReference privacyReference = FirebaseDatabase.getInstance().getReference("Setting")
                .child(currentUser.getUid()).child("Privacy");
        DatabaseReference chatSettingReference = FirebaseDatabase.getInstance().getReference("Setting")
                .child(currentUser.getUid()).child("Chat");

        privacyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("bio").exists()) {
                    bioPri.setChecked(true);
                } else {
                    bioPri.setChecked(false);
                }

                if (dataSnapshot.child("posts").exists()) {
                    postPri.setChecked(true);
                } else {
                    postPri.setChecked(false);
                }

                if (dataSnapshot.child("follow").exists()) {
                    followPri.setChecked(true);
                } else {
                    followPri.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatSettingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Anonymous").child("Request_From_Follower").exists()) {
                    requestFollowerAnon.setChecked(true);
                } else {
                    requestFollowerAnon.setChecked(false);
                }
                if (dataSnapshot.child("Anonymous").child("Request_From_Following").exists()) {
                    requestFollowingAnon.setChecked(true);
                } else {
                    requestFollowingAnon.setChecked(false);
                }
                if (dataSnapshot.child("Anonymous").child("Request_From_Guests").exists()) {
                    requestGuestsAnon.setChecked(true);
                } else {
                    requestGuestsAnon.setChecked(false);
                }
                if (dataSnapshot.child("Non-anonymous").child("Request_From_Follower").exists()) {
                    requestFollowerNonAnon.setChecked(true);
                } else {
                    requestFollowerNonAnon.setChecked(false);
                }
                if (dataSnapshot.child("Non-anonymous").child("Request_From_Following").exists()) {
                    requestFollowingNonAnon.setChecked(true);
                } else {
                    requestFollowingNonAnon.setChecked(false);
                }
                if (dataSnapshot.child("Non-anonymous").child("Request_From_Guests").exists()) {
                    requestGuestsNonAnon.setChecked(true);
                } else {
                    requestFollowerAnon.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    Switch.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            DatabaseReference privacyReference = FirebaseDatabase.getInstance().getReference("Setting")
                    .child(currentUser.getUid());
            if (isAdded()) {
                privacyReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (bioPri.isChecked() == dataSnapshot.child("Privacy").child("bio").exists()
                                && postPri.isChecked() == dataSnapshot.child("Privacy").child("posts").exists()
                                && followPri.isChecked() == dataSnapshot.child("Privacy").child("follow").exists()
                                && requestFollowerAnon.isChecked() == dataSnapshot.child("Chat").child("Anonymous")
                                .child("Request_From_Follower").exists()
                                && requestFollowerNonAnon.isChecked() == dataSnapshot.child("Chat").child("Non-anonymous")
                                .child("Request_From_Follower").exists()
                                && requestFollowingAnon.isChecked() == dataSnapshot.child("Chat").child("Anonymous")
                                .child("Request_From_Following").exists()
                                && requestFollowingNonAnon.isChecked() == dataSnapshot.child("Chat").child("Non-anonymous")
                                .child("Request_From_Following").exists()
                                && requestGuestsAnon.isChecked() == dataSnapshot.child("Chat").child("Anonymous")
                                .child("Request_From_Guests").exists()
                                && requestGuestsNonAnon.isChecked() == dataSnapshot.child("Chat").child("Non-anonymous")
                                .child("Request_From_Guests").exists()) {
                            confirm.setClickable(false);
                            confirm.setTextColor(context.getResources().getColor(R.color.colorTransparentWhite));
                        } else {
                            confirm.setTextColor(context.getResources().getColor(R.color.colorFinish));
                            confirm.setClickable(true);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updatePrivacy(postPri.isChecked(), bioPri.isChecked(), followPri.isChecked());
                                    updateChatRequestSetting(requestFollowerAnon.isChecked(), requestFollowingAnon.isChecked(), requestGuestsAnon.isChecked(),
                                            requestFollowerNonAnon.isChecked(), requestFollowingNonAnon.isChecked(), requestGuestsNonAnon.isChecked());
                                    getFragmentManager().popBackStack();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    View.OnClickListener senarioOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.senarioPrivacy:
                    chatSenario.setVisibility(View.INVISIBLE);
                    privacySenario.setVisibility(View.VISIBLE);
                    backGeneralSenario.setVisibility(View.VISIBLE);
                    generalSenario.setVisibility(View.INVISIBLE);
                    break;
                case  R.id.senarioChat:
                    chatSenario.setVisibility(View.VISIBLE);
                    privacySenario.setVisibility(View.INVISIBLE);
                    backGeneralSenario.setVisibility(View.VISIBLE);
                    generalSenario.setVisibility(View.INVISIBLE);
                    break;
                case R.id.backGeneralSenario:
                    generalSenario.setVisibility(View.VISIBLE);
                    chatSenario.setVisibility(View.INVISIBLE);
                    backGeneralSenario.setVisibility(View.INVISIBLE);
                    privacySenario.setVisibility(View.INVISIBLE);
            }
        }
    };
}
