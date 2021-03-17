package com.bisbizkuit.whistalk.MainPages.MainActivityFragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.adapters.UserRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchFrag extends Fragment {

    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private List<User> userList;

    private EditText searchBar;
    private TextView noUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = (EditText) view.findViewById(R.id.searchBar);
        noUser = (TextView) view.findViewById(R.id.noUser);

        userList = new ArrayList<>();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    readUsers();
                } else {
                    searchUsers(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), userList, 0);
        recyclerView.setAdapter(userRecyclerViewAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        return view;
    }

    private void searchUsers(String s) {
        Log.d("Search", "search");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        userList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                            checkBlocked(user);
                            Log.d("data", "inserted");
                        } else {
                            Log.d("data", "user account");
                        }
                    }
                } else {
                    userList.clear();
                    if (userList.isEmpty()) {
                        noUser.setVisibility(View.VISIBLE);
                    } else {
                        noUser.setVisibility(View.INVISIBLE);
                    }
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        Log.d("Search", "read");
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().trim().equals("")) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                            checkBlocked(user);
                            Log.d("data", "inserted");
                        } else {
                            Log.d("data", "user account");
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
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
                    noUser.setVisibility(View.VISIBLE);
                } else {
                    noUser.setVisibility(View.INVISIBLE);
                }

                userRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        userList.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(0);
                }
                catch (Exception e) { } // Just catch the InterruptedException

                // Now we use the Handler to post back to the main thread
                handler.post(new Runnable() {
                    public void run() {
                        readUsers();
                    }
                });
            }
        }).start();
    }
}
