package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.HomePage_PostDisplay;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.activity.login.sign_in;
import com.bisbizkuit.whistalk.adapters.PostRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class privatepostFrag extends Fragment {

    private RecyclerView privatePostRecyclerView;
    private SwipeRefreshLayout privatePostRefreshLayout;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private TextView noPost;
    private ProgressBar loadMore;

    private List<Post> privatePostList;
    private List<String> backingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privatepost, container, false);

        privatePostRecyclerView = (RecyclerView) view.findViewById(R.id.privatePostRecyclerView);
        privatePostRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        privatePostRecyclerView.setLayoutManager(linearLayoutManager);

        privatePostRecyclerView.setAdapter(postRecyclerViewAdapter);

        loadMore = (ProgressBar) view.findViewById(R.id.privateProgressBar);
        noPost = (TextView) view.findViewById(R.id.noPrivatePost);
        privatePostRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.privatePostRefreshLayout);

        privatePostRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearItem();
                checkFollowing();
                emptyList();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        privatePostRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void checkFollowing() {
        backingList = new ArrayList<>();
        DatabaseReference trackReference;
        trackReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        trackReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                backingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    backingList.add(snapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = firebaseUser.getUid();
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                privatePostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    for (String id : backingList) {
                        if (post.getPublisher().equals(id)) {
                            if (post.getPublisher().equals("ADMIN")) {
                                privatePostList.add(post);
                            } else if (!post.getViewPublic()) {
                                privatePostList.add(post);
                                noPost.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                emptyList();
                postRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void emptyList() {
        if (privatePostList.isEmpty()) {
            noPost.setVisibility(View.VISIBLE);
        } else {
            noPost.setVisibility(View.GONE);
        }
    }

    private void clearItem() {
        for (int item = privatePostList.size() - 1; item >= 0 ; item--) {
            privatePostList.remove(item);
            postRecyclerViewAdapter.notifyItemRemoved(item);
            postRecyclerViewAdapter.notifyItemRangeChanged(item, privatePostList.size());
        }
    }

    private void checkUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            privatePostList = new ArrayList<>();
            postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext(), privatePostList);
            checkFollowing();
        } else {
            getActivity().finish();
            startActivity(new Intent(getContext(), sign_in.class));
        }
    }
}
