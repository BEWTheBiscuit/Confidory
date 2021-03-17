package com.bisbizkuit.whistalk.MainPages.MainActivityFragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.MainPages.setting.editprofileFrag;
import com.bisbizkuit.whistalk.MainPages.setting.follow_detailFrag;
import com.bisbizkuit.whistalk.MainPages.setting.reportFrag;
import com.bisbizkuit.whistalk.MainPages.setting.savedFrag;
import com.bisbizkuit.whistalk.MainPages.setting.settingFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.SpecialClass.CallBacks;
import com.bisbizkuit.whistalk.SpecialClass.CallBacksBoolean;
import com.bisbizkuit.whistalk.activity.login.sign_in;
import com.bisbizkuit.whistalk.adapters.PostRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.Post;
import com.bisbizkuit.whistalk.objects.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFrag extends Fragment {

    private CircleImageView userIcon, actionBarUserIcon;
    private RecyclerView recyclerViewPost;
    private ImageView setting;
    private TextView actionBarUsername, profileUsername, followingNumber, followerNumber, noPostFound, profileBio, followingTxt, followerTxt;
    private View followingDetail, followerDetail, view, followLayout, profileInfoBox;
    private ProgressBar progressBar;
    private Button followButton, requestButton, followedButton;
    //private SwipeRefreshLayout refreshProfile;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReferencePost;
    private FirebaseUser firebaseUser;

    private String userId, currentUserId, postDate;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = firebaseUser.getUid();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReferencePost = FirebaseDatabase.getInstance().getReference().child("Posts");

        userIcon = (CircleImageView) view.findViewById(R.id.userIcon);
        recyclerViewPost = (RecyclerView) view.findViewById(R.id.postView_profile);
        actionBarUserIcon = (CircleImageView) view.findViewById(R.id.actionBarUserIcon);
        actionBarUsername = (TextView) view.findViewById(R.id.actionBarUsername);
        profileUsername = (TextView) view.findViewById(R.id.profileUsername);
        setting = (ImageView) view.findViewById(R.id.profileSetting);
        followerNumber = (TextView) view.findViewById(R.id.followerNum);
        followingNumber = (TextView) view.findViewById(R.id.followingNum);
        followerTxt = (TextView) view.findViewById(R.id.followerTxt);
        followingTxt = (TextView) view.findViewById(R.id.followingTxt);
        followerDetail = (View) view.findViewById(R.id.follower);
        followingDetail = (View) view.findViewById(R.id.following);
        noPostFound = (TextView) view.findViewById(R.id.noPostFound);
        profileBio = (TextView) view.findViewById(R.id.bio);
        progressBar = (ProgressBar) view.findViewById(R.id.progressLoading);
        followButton = (Button) view.findViewById(R.id.profileBack);
        followedButton = (Button) view.findViewById(R.id.profileBacked);
        requestButton = (Button) view.findViewById(R.id.profileRequested);
        followLayout = (View) view.findViewById(R.id.follow);
        profileInfoBox = (View) view.findViewById(R.id.profileInfoBox);

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

        postList = new ArrayList<>();
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext(), postList);
        recyclerViewPost.setAdapter(postRecyclerViewAdapter);

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.finger_paint);
        profileUsername.setTypeface(typeface);
        followingTxt.setTypeface(typeface);
        followerTxt.setTypeface(typeface);

        setUpProfile();

        return view;
    }

    private void setUserInformation() {
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);
                if (currentUserId.equals(userId)) {
                    if (!user.getIcon().equals("")) {
                        Picasso.get()
                                .load(user.getIcon())
                                .into(userIcon);
                    } else {
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                .into(userIcon);
                    }

                    followingDetail.setVisibility(View.VISIBLE);
                    followerDetail.setVisibility(View.VISIBLE);
                    setFollowDetails();
                } else {
                    Picasso.get()
                            .load(R.drawable.ic_arrowback_brown)
                            .into(actionBarUserIcon);
                    if (!user.getIcon().equals("")) {
                        Picasso.get()
                                .load(user.getIcon())
                                .into(userIcon);
                    } else {
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                .into(userIcon);
                    }
                    followLayout.setVisibility(View.INVISIBLE);
                    setFollowDetails();
                }
                actionBarUsername.setText(user.getUsername());
                profileUsername.setText(user.getUsername());

                DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(userId).child("followers").child(currentUserId);
                backReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (currentUserId.equals(userId)) {
                            profileBio.setText(user.getBio());

                            noPostFound.setVisibility(View.GONE);
                            readPost(false);
                            followButton.setVisibility(View.INVISIBLE);
                            followedButton.setVisibility(View.INVISIBLE);
                            requestButton.setVisibility(View.INVISIBLE);
                        } else if (dataSnapshot.exists()) {
                            checkPrivacy();

                            isFollowing(user.getId(), followButton, requestButton, followedButton);
                            followButton.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestBack(user.getId());
                                    followButton.setVisibility(View.GONE);
                                    followedButton.setVisibility(View.GONE);
                                    requestButton.setVisibility(View.VISIBLE);
                                    Log.d("follow", "follow");
                                }
                            });

                            followedButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                                    bottomSheetDialog.setContentView(R.layout.bottomdialog_cancel_back);
                                    bottomSheetDialog.show();
                                    Button confirmCancelFollowing = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBacking);
                                    Button cancelCancelFollowing = (Button) bottomSheetDialog.findViewById(R.id.dialog_resumeBacking);
                                    cancelCancelFollowing.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    confirmCancelFollowing.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteFollowing(user.getId());
                                            followedButton.setVisibility(View.GONE);
                                            followButton.setVisibility(View.VISIBLE);
                                            requestButton.setVisibility(View.GONE);
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                            });

                            requestButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteRequestBack(user.getId());
                                    requestButton.setVisibility(View.GONE);
                                    followedButton.setVisibility(View.GONE);
                                    followButton.setVisibility(View.VISIBLE);
                                    Log.d("follow", "requested");
                                }
                            });
                        } else {
                            checkPrivacy();
                            isFollowing(user.getId(), followButton, requestButton, followedButton);
                            followButton.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestBack(user.getId());
                                    followButton.setVisibility(View.GONE);
                                    followedButton.setVisibility(View.GONE);
                                    requestButton.setVisibility(View.VISIBLE);
                                    Log.d("follow", "follow");
                                }
                            });

                            followedButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                                    bottomSheetDialog.setContentView(R.layout.bottomdialog_cancel_back);
                                    bottomSheetDialog.show();
                                    Button confirmCancelFollowBacking = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBacking);
                                    Button cancelCancelFollowing = (Button) bottomSheetDialog.findViewById(R.id.dialog_resumeBacking);
                                    cancelCancelFollowing.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    confirmCancelFollowBacking.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteFollowing(user.getId());
                                            followedButton.setVisibility(View.GONE);
                                            followButton.setVisibility(View.VISIBLE);
                                            requestButton.setVisibility(View.GONE);
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                            });

                            requestButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteRequestBack(user.getId());
                                    requestButton.setVisibility(View.GONE);
                                    followedButton.setVisibility(View.GONE);
                                    followButton.setVisibility(View.VISIBLE);
                                    Log.d("follow", "requested");
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPost(final boolean postPri) {
        databaseReferencePost = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReferencePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Post post = snapshot.getValue(Post.class);
                    DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                            .child(userId).child("followers").child(currentUserId);
                    backReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (post.getViewPublic() && !post.getAnonity()) {
                                    if (post.getPublisher().equals(userId)){
                                        postList.add(post);
                                    }
                                } else if (dataSnapshot.exists()) {
                                    if (!post.getAnonity()) {
                                        if (post.getPublisher().equals(userId)) {
                                            postList.add(post);
                                        }
                                    }
                                } else if (currentUserId.equals(userId)) {
                                    if (post.getPublisher().equals(userId)) {
                                        postList.add(post);
                                    }
                                } else {
                                    if (!post.getAnonity() && !postPri) {
                                        if (post.getPublisher().equals(userId)) {
                                            postList.add(post);
                                        }
                                    }
                                }
                            if (postList.isEmpty()) {
                                noPostFound.setVisibility(View.VISIBLE);
                            } else {
                                noPostFound.setVisibility(View.GONE);
                            }
                            postRecyclerViewAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setToolBarIcon() {
        DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(userId).child("followers").child(currentUserId);
        backReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (currentUserId.equals(userId)) {
                    databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            StorageReference storageUserIcon = FirebaseStorage.getInstance().getReference("uploads").child("userIcon").child(userId);
                            if (!user.getIcon().trim().equals("")) {
                                Picasso.get().load(user.getIcon()).into(actionBarUserIcon);
                            } else {
                                Picasso.get()
                                        .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                        .into(actionBarUserIcon);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    actionBarUserIcon.setClickable(false);
                    setting.setImageResource(R.drawable.ic_profilesetting);
                    setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popupMenu = new PopupMenu(getContext(), setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.setting, Menu.NONE, R.string.setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.editProfile, Menu.NONE, R.string.editProfile);
                            popupMenu.getMenu().add(Menu.NONE, R.id.saved, Menu.NONE, R.string.saved);
                            popupMenu.getMenu().add(Menu.NONE, R.id.logout, Menu.NONE, R.string.logout);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.setting:
                                            getFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein)
                                                    .addToBackStack(null).replace(R.id.selectedFragView, new settingFrag()).commit();
                                            break;
                                        case R.id.editProfile:
                                            getFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein)
                                                    .addToBackStack(null).replace(R.id.selectedFragView, new editprofileFrag()).commit();
                                            break;
                                        case R.id.saved:
                                            getFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein)
                                                    .addToBackStack(null).replace(R.id.selectedFragView, new savedFrag(getContext())).commit();
                                            break;
                                        case R.id.logout:
                                            firebaseAuth.signOut();
                                            startActivity(new Intent(getContext(), sign_in.class));
                                            getActivity().finish();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });

                    noPostFound.setVisibility(View.GONE);
                } else if (dataSnapshot.exists()) {
                    Glide.with(getContext()).load(getResources().getDrawable(R.drawable.ic_arrowback_brown)).into(actionBarUserIcon);
                    actionBarUserIcon.setClickable(true);
                    actionBarUserIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getFragmentManager().popBackStack();
                            Log.d("Follow", "success");
                        }
                    });
                    setting.setImageResource(R.drawable.ic_setting);
                    setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popupMenu = new PopupMenu(getContext(), setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.deleteFollower, Menu.NONE, R.string.deleteFollower);
                            popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                            popupMenu.getMenu().add(Menu.NONE, R.id.block, Menu.NONE, R.string.block);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    final BottomSheetDialog bottomSheetDialog;
                                    switch (item.getItemId()) {
                                        case R.id.deleteFollower:
                                            bottomSheetDialog = new BottomSheetDialog(getContext());
                                            bottomSheetDialog.setContentView(R.layout.delete_follower_alert);
                                            bottomSheetDialog.show();
                                            Button confirmDeleteFollower = (Button) bottomSheetDialog.findViewById(R.id.dialog_deleteFollower);
                                            Button cancelDeleteFollower = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelDeleteFollower);
                                            cancelDeleteFollower.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            confirmDeleteFollower.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    deleteFollower(userId);
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            break;
                                        case R.id.report:
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                    .replace(R.id.selectedFragView, new reportFrag(null, null, userId)).commit();
                                            break;
                                        case R.id.block:
                                            bottomSheetDialog = new BottomSheetDialog(getContext());
                                            bottomSheetDialog.setContentView(R.layout.block_alert);
                                            bottomSheetDialog.show();
                                            Button confirmBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_blockUser);
                                            Button cancelBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBlock);
                                            cancelBlockUser.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            confirmBlockUser.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    deleteFollower(userId);
                                                    deleteFollowing(userId);
                                                    blockUser(userId);
                                                    bottomSheetDialog.dismiss();

                                                }
                                            });
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });

                    noPostFound.setVisibility(View.GONE);
                } else {
                    Glide.with(getContext()).load(getResources().getDrawable(R.drawable.ic_arrowback_brown)).into(actionBarUserIcon);
                    actionBarUserIcon.setClickable(true);
                    actionBarUserIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getFragmentManager().popBackStack();
                            Log.d("Follow", "success");
                        }
                    });
                    setting.setImageResource(R.drawable.ic_setting);
                    setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PopupMenu popupMenu = new PopupMenu(getContext(), setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.deleteFollower, Menu.NONE, R.string.deleteFollower);
                            popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                            checkBlocked(new CallBacksBoolean<Boolean>() {
                                @Override
                                public void callback(Boolean data) {
                                    if (data == true) {
                                        popupMenu.getMenu().add(Menu.NONE, R.id.unblock, Menu.NONE, R.string.unblock);
                                    } else if (data == false) {
                                        popupMenu.getMenu().add(Menu.NONE, R.id.block, Menu.NONE, R.string.block);
                                    }
                                }
                            });
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    final BottomSheetDialog bottomSheetDialog;
                                    switch (item.getItemId()) {
                                        case R.id.deleteFollower:
                                            bottomSheetDialog = new BottomSheetDialog(getContext());
                                            bottomSheetDialog.setContentView(R.layout.delete_follower_alert);
                                            bottomSheetDialog.show();
                                            Button confirmDeleteFollower = (Button) bottomSheetDialog.findViewById(R.id.dialog_deleteFollower);
                                            Button cancelDeleteFollower = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelDeleteFollower);
                                            cancelDeleteFollower.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            confirmDeleteFollower.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    deleteFollower(userId);
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            break;
                                        case R.id.report:
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                    .replace(R.id.selectedFragView, new reportFrag(null, null, userId)).commit();
                                            break;
                                        case R.id.block:
                                            bottomSheetDialog = new BottomSheetDialog(getContext());
                                            bottomSheetDialog.setContentView(R.layout.block_alert);
                                            bottomSheetDialog.show();
                                            Button confirmBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_blockUser);
                                            Button cancelBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBlock);
                                            cancelBlockUser.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });
                                            confirmBlockUser.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    deleteFollower(userId);
                                                    deleteFollowing(userId);
                                                    blockUser(userId);
                                                    bottomSheetDialog.dismiss();
                                                    getFragmentManager().popBackStack();
                                                }
                                            });
                                            break;
                                        case R.id.unblock:
                                            FirebaseDatabase.getInstance().getReference("Block")
                                                    .child(currentUserId).child(userId).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "User unblocked", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public profileFrag(String userId) {
        this.userId = userId;
    }

    private void setFollowDetails() {
        setNumberFollowText();
        followerDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.selectedFragView, new follow_detailFrag("FOLLOWERS")).commit();
            }
        });
        followingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.selectedFragView, new follow_detailFrag("FOLLOWING")).commit();
            }
        });
    }

    private void setNumberFollowText() {
        DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        backReference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingNumber.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        backReference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerNumber.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpProfile() {
        setToolBarIcon();
        setUserInformation();
    }

    private void checkPrivacy() {
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                final User user = dataSnapshot2.getValue(User.class);
                DatabaseReference backReference = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(userId).child("followers").child(currentUserId);
                backReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {
                        DatabaseReference privacyReference = FirebaseDatabase.getInstance().getReference("Setting").child("Privacy")
                                .child(userId);
                        privacyReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot1.exists()) {
                                    profileBio.setText(user.getBio());
                                    noPostFound.setVisibility(View.GONE);
                                    readPost(false);
                                } else if (userId.equals(currentUserId)) {
                                    readPost(false);
                                } else if (dataSnapshot.child("bio").exists() && dataSnapshot.child("follow").exists()) {
                                    profileInfoBox.setVisibility(View.GONE);
                                    RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.addRule(RelativeLayout.BELOW, R.id.userProfile);
                                    recyclerViewPost.setLayoutParams(params);
                                } else {
                                    profileInfoBox.setVisibility(View.VISIBLE);
                                    if (dataSnapshot.child("bio").exists()) {
                                        profileBio.setVisibility(View.GONE);
                                    } else {
                                        profileBio.setVisibility(View.VISIBLE);
                                        profileBio.setText(user.getBio());
                                    }

                                    if (dataSnapshot.child("posts").exists()) {
                                        noPostFound.setVisibility(View.VISIBLE);
                                        recyclerViewPost.setVisibility(View.GONE);
                                    } else {
                                        noPostFound.setVisibility(View.GONE);
                                        readPost(true);
                                    }

                                    if (dataSnapshot.child("follow").exists()) {
                                        followLayout.setVisibility(View.GONE);
                                    } else {
                                        followLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isFollowing(final String userId, final Button userBack, final Button userRequested, final Button userBacked) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()) {
                    userBacked.setVisibility(View.VISIBLE);
                    userBack.setVisibility(View.GONE);
                    userRequested.setVisibility(View.GONE);
                } else {
                    DatabaseReference back_requestReference = FirebaseDatabase.getInstance()
                            .getReference("Follow_Request")
                            .child(userId);
                    back_requestReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                                userBacked.setVisibility(View.GONE);
                                userBack.setVisibility(View.GONE);
                                userRequested.setVisibility(View.VISIBLE);
                            } else {
                                userBacked.setVisibility(View.GONE);
                                userBack.setVisibility(View.VISIBLE);
                                userRequested.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("User", "back");
    }

    private void requestBack(String useId) {
        DatabaseReference back_requestReference = FirebaseDatabase.getInstance().getReference("Follow_Request")
                .child(useId).child(firebaseUser.getUid());

        back_requestReference.setValue(true);
    }

    private void deleteRequestBack(String useId) {
        DatabaseReference back_requestReference = FirebaseDatabase.getInstance().getReference("Follow_Request")
                .child(useId).child(firebaseUser.getUid());
        back_requestReference.removeValue();
    }

    private void deleteFollower(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(currentUserId).child("followers").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(currentUserId).child("followers").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("following").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("following").child(currentUserId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFollowing(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(currentUserId).child("following").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(currentUserId).child("following").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("followers").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("followers").child(currentUserId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blockUser(String userId) {
        FirebaseDatabase.getInstance().getReference("Block").child(currentUserId).child(userId).setValue(true);
    }

    private void checkBlocked(final CallBacksBoolean callBacks) {
        FirebaseDatabase.getInstance().getReference("Block").child(currentUserId).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callBacks.callback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}