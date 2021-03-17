package com.bisbizkuit.whistalk.MainPages.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
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

import java.util.ArrayList;
import java.util.List;

public class follow_detailFrag extends Fragment {

    TextView searchTypeDisplay, noFollow, numberFollow;
    EditText searchBar;
    ImageView followDetailsBack;
    RecyclerView searchResult;
    ProgressBar progressBar;

    UserRecyclerViewAdapter userRecyclerViewAdapter;
    List<User> followingList, followerList, likeList, blockList, chatRequestList;
    DatabaseReference userReference;
    DatabaseReference backReference;

    String searchType, postId;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_back_detail, container, false);

        userReference = FirebaseDatabase.getInstance().getReference("Users");
        backReference = FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        searchTypeDisplay = (TextView) view.findViewById(R.id.followText);
        searchBar = (EditText) view.findViewById(R.id.searchFollow);
        searchResult = (RecyclerView) view.findViewById(R.id.userSearchResult);
        noFollow = (TextView) view.findViewById(R.id.noFollow);
        numberFollow = (TextView) view.findViewById(R.id.numberFollow);
        followDetailsBack = (ImageView) view.findViewById(R.id.followDetailBack);
        progressBar = (ProgressBar) view.findViewById(R.id.followDetailLoading);

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

        searchResult.setHasFixedSize(true);
        searchResult.setLayoutManager(new LinearLayoutManager(getContext()));

        followingList = new ArrayList<>();
        followerList = new ArrayList<>();
        likeList = new ArrayList<>();
        chatRequestList = new ArrayList<>();
        blockList = new ArrayList<>();

        setNumberBackText();

        Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
        searchTypeDisplay.setTypeface(typeface);

        if (searchType.equals("FOLLOWING")) {
            readFollowing();
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), followingList, 0);
            searchTypeDisplay.setText(R.string.following);
        } else if (searchType.equals("FOLLOWERS")) {
            readFollowers();
            searchTypeDisplay.setText(R.string.followers);
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), followerList, 0);
        } else if (searchType.equals("LIKES")) {
            readLikes();
            searchTypeDisplay.setText(R.string.likes);
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), likeList, 0);
        } else if (searchType.equals("CHAT_REQUEST")) {
            readChatRequest();
            searchTypeDisplay.setText(R.string.chatRequestUpper);
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), chatRequestList, 2);
        } else if (searchType.equals("BLOCK")) {
            readBlock();
            searchTypeDisplay.setText(R.string.blockUpper);
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(getContext(), blockList, 0);
        }

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchType.equals("FOLLOWING")) {
                    searchFollowing(s.toString().trim());
                } else if (searchType.equals("FOLLOWERS")) {
                    searchFollowers(s.toString().trim());
                } else if (searchType.equals("LIKES")) {
                    searchLikes(s.toString().trim());
                } else if (searchType.equals("CHAT_REQUEST")) {
                    searchChatRequest(s.toString().trim());
                } else if (searchType.equals("BLOCK")) {
                    searchBlock(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchResult.setAdapter(userRecyclerViewAdapter);

        followDetailsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public follow_detailFrag(String searchType) {
        this.searchType = searchType;
    }

    public follow_detailFrag(String searchType, String postId) {
        this.searchType = searchType;
        this.postId = postId;
    }

    private void readFollowing() {
        Log.d("Search", "read");
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().trim().equals("")) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                            checkFollowing(user);
                            Log.d("data", "inserted");
                        } else {
                            Log.d("data", "user account");
                        }
                    }

                }

                userRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
    }

    private void searchFollowing(String s) {
        Log.d("Search", "following");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                        checkFollowing(user);
                        Log.d("data", "following user inserted");
                    } else {
                        Log.d("data", "following || current user account");
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (followerList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchFollowers(String s) {
        Log.d("Search", "followers");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                        checkFollowers(user);
                        Log.d("data", "followers user inserted");
                    } else {
                        Log.d("data", "followers || current user account");
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (followerList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readFollowers() {
        Log.d("Search", "read");
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().trim().equals("")) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                            checkFollowers(user);
                            Log.d("data", "inserted");
                        } else {
                            Log.d("data", "user account");
                        }
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
    }

    private void checkFollowing(final User user) {
        backReference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                if (dataSnapshot.child(user.getId()).exists()) {
                    followingList.add(user);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (followingList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowers(final User user) {
        backReference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerList.clear();
                if (dataSnapshot.child(user.getId()).exists()) {
                    followerList.add(user);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (followerList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNumberBackText() {
        DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (searchType.equals("FOLLOWING")) {
            backReference.child("following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    numberFollow.setText(dataSnapshot.getChildrenCount() + " Users");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (searchType.equals("FOLLOWERS")) {
            backReference.child("followers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    numberFollow.setText(dataSnapshot.getChildrenCount() + " Users");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void readLikes() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    checkLikesUsers(postId, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchLikes(String s) {
        Log.d("Search", "likes");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!currentUser.getUid().equals(user.getId())) {
                        checkLikesUsers(postId, user);
                        Log.d("data", "like user inserted");
                    } else {
                        Log.d("data", "like || current user account");
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (followerList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkLikesUsers(String postId, final User user) {
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("Likes").child(postId).child(user.getId());
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    likeList.add(user);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (likeList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChatRequest() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    checkChatRequestList(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkChatRequestList(final User user) {
        FirebaseDatabase.getInstance().getReference("Chat_request")
                .child(currentUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatRequestList.add(user);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (chatRequestList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchChatRequest(String s) {
        Log.d("Search", "chat_request");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRequestList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!currentUser.getUid().equals(user.getId())) {
                        checkChatRequestList(user);
                        Log.d("data", "chat request user inserted");
                    } else {
                        Log.d("data", "chat request || current user account");
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (chatRequestList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readBlock() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    checkBlock(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkBlock(final User user) {
        FirebaseDatabase.getInstance().getReference("Block")
                .child(currentUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    blockList.add(user);
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (blockList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchBlock(String s) {
        Log.d("Search", "Block");
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blockList.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    if (!currentUser.getUid().equals(user.getId())) {
                        checkBlock(user);
                        Log.d("data", "block user inserted");
                    } else {
                        Log.d("data", "block || current user account");
                    }
                }
                userRecyclerViewAdapter.notifyDataSetChanged();
                if (blockList.isEmpty()) {
                    noFollow.setVisibility(View.VISIBLE);
                } else {
                    noFollow.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
