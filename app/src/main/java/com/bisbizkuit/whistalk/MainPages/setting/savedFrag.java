package com.bisbizkuit.whistalk.MainPages.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.adapters.PostRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class savedFrag extends Fragment {

    private TextView savedTxt, noPostFound;
    private ImageView savedBack;
    private RecyclerView savedPost;
    private Context context;

    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        savedBack = (ImageView) view.findViewById(R.id.savedBack);
        noPostFound = (TextView) view.findViewById(R.id.noPostFoundSaved);
        savedTxt = (TextView) view.findViewById(R.id.savedTxt);
        savedPost = (RecyclerView) view.findViewById(R.id.savedPost);
        savedPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        savedPost.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        readPost();
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext(), postList);
        savedPost.setAdapter(postRecyclerViewAdapter);

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.finger_paint);
        savedTxt.setTypeface(typeface);

        savedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void readPost() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    checkSavedPost(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSavedPost(final Post post) {
        DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference("Saved").child(post.getPostId());
        savedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                        postList.add(post);
                    }
                }
                if (postList.isEmpty()) {
                    noPostFound.setVisibility(View.VISIBLE);
                } else {
                    noPostFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postRecyclerViewAdapter.notifyDataSetChanged();

    }

    public savedFrag(Context context) {
        context = this.context;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        context = this.context;
    }
}
