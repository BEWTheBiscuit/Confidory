package com.bisbizkuit.whistalk.MainPages.MainActivityFragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.adapters.FollowRequestAdapter;
import com.bisbizkuit.whistalk.adapters.notificationAdapter;
import com.bisbizkuit.whistalk.objects.User;
import com.bisbizkuit.whistalk.objects.notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class notificationFrag extends Fragment {

    private TextView notificationTitle;
    private RecyclerView recyclerView, followRequestRecyclerView;
    private notificationAdapter notificationAdapter;
    private FollowRequestAdapter followRequestAdapter;
    private List<notification> notificationList;
    private List<User> userList;
    private ImageView followButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.notifications);
        followRequestRecyclerView = (RecyclerView) view.findViewById(R.id.backRequestRecyclerView);
        followRequestRecyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        followRequestRecyclerView.setLayoutManager(linearLayoutManager1);

        userList = new ArrayList<>();
        notificationList = new ArrayList<>();
        readNotifications();
        readBackRequest();

        followButton = (ImageView) view.findViewById(R.id.notificationBackButton);
        notificationTitle = (TextView) view.findViewById(R.id.notificationTxt);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        notificationAdapter = new notificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        notificationAdapter.notifyDataSetChanged();

        followRequestAdapter = new FollowRequestAdapter(getContext(), userList);
        followRequestRecyclerView.setAdapter(followRequestAdapter);
        followRequestAdapter.notifyDataSetChanged();

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.finger_paint);
        notificationTitle.setTypeface(typeface);

        return view;
    }

    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("notifications", "setUp");
                    notification notification = snapshot.getValue(notification.class);
                    notificationList.add(notification);
                }

                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readBackRequest() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userReference .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    checkFollowRequest(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowRequest(final User user) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference back_requestReference = FirebaseDatabase.getInstance().getReference("Follow_Request").child(firebaseUser.getUid());
        back_requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getId()).exists()) {
                    userList.add(user);
                } else {
                    userList.remove(user);
                }
                Collections.reverse(userList);
                followRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
