package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.SpecialClass.RecyclerTouchListener;
import com.bisbizkuit.whistalk.adapters.UserRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static maes.tech.intentanim.CustomIntent.customType;

import java.util.ArrayList;
import java.util.List;

public class addChat extends AppCompatActivity {

    private EditText searchUser;
    private RecyclerView addChatRecyclerView;
    private TextView noAddChatUser;
    private TextView addChatBack, addGroupChat;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private List<User> userList;
    private boolean anonityChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        anonityChat = bundle.getBoolean("Anonity");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        noAddChatUser = (TextView) findViewById(R.id.noAddChatUser);
        addChatBack = (TextView) findViewById(R.id.addChatBack);
        searchUser = (EditText) findViewById(R.id.addChatSearchBar);
        addGroupChat = (TextView) findViewById(R.id.addGroupChat);
        addChatRecyclerView = (RecyclerView) findViewById(R.id.addChatRecyclerView);

        addChatRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        addChatRecyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(this, userList, 1, anonityChat);
        addChatRecyclerView.setAdapter(userRecyclerViewAdapter);

        readAddChatUser();

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    readAddChatUser();
                } else {
                    searchUsers(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addChatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                customType(addChat.this, "up-to-bottom");
            }
        });

        addGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chat");
            }
        });

    }

    private void searchUsers(String s) {
        Log.d("Search", "search");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                        checkBlocked(user);
                        Log.d("data", "inserted");
                    } else {
                        Log.d("data", "user account");
                    }
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAddChatUser() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchUser.getText().toString().trim().equals("")) {
                    userList.clear();
                    for (DataSnapshot s: dataSnapshot.getChildren()) {
                        User user = s.getValue(User.class);
                        checkFollow(user);
                    }
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(final User user) {
        DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");
        followReference.child(firebaseUser.getUid()).child("following")
                .child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userList.add(user);
                }
                if (userList.isEmpty()) {
                    noAddChatUser.setVisibility(View.VISIBLE);
                } else {
                    noAddChatUser.setVisibility(View.INVISIBLE);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkBlocked(final User user) {
        FirebaseDatabase.getInstance().getReference("Block").child(user.getId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    userList.add(user);
                }
                if (userList.isEmpty()) {
                    noAddChatUser.setVisibility(View.VISIBLE);
                } else {
                    noAddChatUser.setVisibility(View.INVISIBLE);
                }

                userRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
