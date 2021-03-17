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
import android.widget.AbsListView;
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

public class publicpostFrag extends Fragment {

    private RecyclerView publicPostRecyclerView;
    private SwipeRefreshLayout publicPostRefreshLayout;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private TextView noPost;
    private ProgressBar loadMore;

    private Boolean isScrolling = false;
    private List<Post> publicPostList,  displayPostList;
    private int currentItems, totalItems, scrollOutItems;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicpost, container, false);

        publicPostRecyclerView = (RecyclerView) view.findViewById(R.id.publicPostRecyclerView);
        publicPostRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        publicPostRecyclerView.setLayoutManager(linearLayoutManager);
        publicPostRecyclerView.setAdapter(postRecyclerViewAdapter);

        loadMore = (ProgressBar) view.findViewById(R.id.publicProgressBar);
        noPost = (TextView) view.findViewById(R.id.noPublicPost);
        publicPostRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.publicPostRefreshLayout);

        publicPostRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearItem();
                readPublicPosts();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        publicPostRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    private void loadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    displayPostList.add(publicPostList.get(position));
                    position++;
                    postRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }, 1000);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void emptyList() {
        if (publicPostList.isEmpty()) {
            noPost.setVisibility(View.VISIBLE);
        } else {
            noPost.setVisibility(View.GONE);
        }
    }

    private void readPublicPosts() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicPostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getViewPublic()) {
                        if (!post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            publicPostList.add(post);
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

    private void clearItem() {
        for (int item = publicPostList.size() - 1; item >= 0 ; item--) {
            publicPostList.remove(item);
            postRecyclerViewAdapter.notifyItemRemoved(item);
            postRecyclerViewAdapter.notifyItemRangeChanged(item, publicPostList.size());
        }
    }

    private void checkUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            publicPostList = new ArrayList<>();
            postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext(), publicPostList);
            readPublicPosts();
        } else {
            getActivity().finish();
            startActivity(new Intent(getContext(), sign_in.class));
        }
    }
}
