package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.adapters.ChatRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static maes.tech.intentanim.CustomIntent.customType;

import java.util.List;

public class nonAnonymousChat extends Fragment {

    private RecyclerView nonAnonChatRecyclerView;
    private FloatingActionButton addChat;
    private TextView noChatFound;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private List<User> userList;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_non_anonymous_chat, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        addChat = (FloatingActionButton) view.findViewById(R.id.addNonAnonChat);
        noChatFound = (TextView) view.findViewById(R.id.noChatFound);
        nonAnonChatRecyclerView = (RecyclerView) view.findViewById(R.id.nonAnonymousChat);

        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), addChat.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Anonity", false);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
                customType(getContext(), "bottom-to-up");
            }
        });

        return view;
    }

    private void setNonAnonChatList() {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chat")
                .child(firebaseUser.getUid()).child("Non_Anonymous_Chat");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
