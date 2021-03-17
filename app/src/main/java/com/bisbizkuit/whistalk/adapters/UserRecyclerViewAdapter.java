package com.bisbizkuit.whistalk.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage.chatRoom;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.profileFrag;
import com.bisbizkuit.whistalk.MainPages.setting.reportFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static maes.tech.intentanim.CustomIntent.customType;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    Context context;
    List<User> userList;

    private FirebaseUser firebaseUser;
    private String postDate;
    private int USER_PROFILE = 0;
    private int ADDCHAT = 1;
    private int CHAT = 2; //...
    private int CHAT_REQUEST = 3;
    private int Situation;
    private boolean chatAnonity;

    public UserRecyclerViewAdapter(Context context, List<User> userList, int Situation) {
        this.context = context;
        this.userList = userList;
        this.Situation = Situation;
    }

    public UserRecyclerViewAdapter(Context context, List<User> userList, int Situation, boolean anonity) {
        this.context = context;
        this.userList = userList;
        this.Situation = Situation;
        this.chatAnonity = anonity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserRecyclerViewAdapter.UserViewHolder(LayoutInflater.from(context).inflate(R.layout.users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = userList.get(position);

        Log.d("Layout", "data inserted");

        holder.userUsername.setText(user.getUsername());
        Log.d("User", holder.userUsername.getText().toString());
        if (user.getIcon().equals("")) {
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                    .into(holder.userIcon);
        } else {
            Picasso.get()
                    .load(user.getIcon())
                    .into(holder.userIcon);
        }

        if (Situation == ADDCHAT || Situation == CHAT) {
            holder.userFollow.setVisibility(View.GONE);
            holder.userFollowed.setVisibility(View.GONE);
            holder.userRequested.setVisibility(View.GONE);
        } else if (user.getId().equals(firebaseUser.getUid())) {
            holder.userFollow.setVisibility(View.GONE);
            holder.userFollowed.setVisibility(View.GONE);
            holder.userRequested.setVisibility(View.GONE);
        } else {
            isBacking(user.getId(), holder.userFollow, holder.userRequested, holder.userFollowed);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Bundle bundle;
                switch (Situation) {
                    case 0: //USER_PROFILE
                        SharedPreferences.Editor editor = context.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", user.getId());
                        editor.apply();
                        Log.d("User", "clickable");
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                .replace(R.id.selectedFragView, new profileFrag(user.getId())).commit();
                        userList.remove(user);
                        break;
                    case 1: //ADDCHAT
                        addChat(user);
                        break;
                    case 2: //CHAT
                        intent = new Intent(context, chatRoom.class);
                        bundle = new Bundle();
                        bundle.putString("ChatType", "Individual");
                        bundle.putString("ID", user.getId());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        customType(context, "left-to-right");
                        break;
                    case 3: //CHAT_REQUEST
                        intent = new Intent(context, chatRoom.class);
                        bundle = new Bundle();
                        bundle.putString("ChatType", "Individual");
                        bundle.putString("ID", user.getId());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        customType(context, "left-to-right");
                        break;
                }
            }
        });

        if (Situation == USER_PROFILE) {
            FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid())
                    .child("followers").child(user.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
                    if (dataSnapshot.exists()) {
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                popupMenu.getMenu().add(Menu.NONE, R.id.deleteFollower, Menu.NONE, R.string.deleteFollower);
                                popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                                popupMenu.getMenu().add(Menu.NONE, R.id.block, Menu.NONE, R.string.block);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        final BottomSheetDialog bottomSheetDialog;
                                        switch (item.getItemId()) {
                                            case R.id.deleteFollower:
                                                bottomSheetDialog = new BottomSheetDialog(context);
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
                                                        deleteFollower(user.getId());
                                                        bottomSheetDialog.dismiss();
                                                    }
                                                });
                                                break;
                                            case R.id.report:
                                                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                                        .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                        .replace(R.id.selectedFragView, new reportFrag(null, null, user.getId())).commit();
                                                break;
                                            case R.id.block:
                                                bottomSheetDialog = new BottomSheetDialog(context);
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
                                                        deleteFollower(user.getId());
                                                        deleteFollowing(user.getId());
                                                        blockUser(user.getId());
                                                        bottomSheetDialog.dismiss();

                                                    }
                                                });
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                                return false;
                            }
                        });
                    } else {
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                                popupMenu.getMenu().add(Menu.NONE, R.id.block, Menu.NONE, R.string.block);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        final BottomSheetDialog bottomSheetDialog;
                                        switch (item.getItemId()) {
                                            case R.id.report:
                                                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                                        .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                        .replace(R.id.selectedFragView, new reportFrag(null, null, user.getId())).commit();
                                                break;
                                            case R.id.block:
                                                bottomSheetDialog = new BottomSheetDialog(context);
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
                                                        deleteFollower(user.getId());
                                                        deleteFollowing(user.getId());
                                                        blockUser(user.getId());
                                                        bottomSheetDialog.dismiss();

                                                    }
                                                });
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                                return false;
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        holder.userFollow.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBack(user.getId());
                holder.userFollow.setVisibility(View.GONE);
                holder.userFollowed.setVisibility(View.GONE);
                holder.userRequested.setVisibility(View.VISIBLE);
                Log.d("follow", "follow");
            }
        });

        holder.userFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.bottomdialog_cancel_back);
                bottomSheetDialog.show();
                Button confirmCancelBacking = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBacking);
                Button cancelCancelBacking = (Button) bottomSheetDialog.findViewById(R.id.dialog_resumeBacking);
                cancelCancelBacking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                confirmCancelBacking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(user.getId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                        holder.userFollowed.setVisibility(View.GONE);
                        holder.userFollow.setVisibility(View.VISIBLE);
                        holder.userRequested.setVisibility(View.GONE);
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });

        holder.userRequested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRequestBack(user.getId());
                holder.userRequested.setVisibility(View.GONE);
                holder.userFollowed.setVisibility(View.GONE);
                holder.userFollow.setVisibility(View.VISIBLE);
                Log.d("follow", "requested");
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIcon;
        TextView userUsername;
        Button userFollow, userRequested, userFollowed;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userIcon = (CircleImageView) itemView.findViewById(R.id.userlistIcon);
            userUsername = (TextView) itemView.findViewById(R.id.userlistUsername);
            userFollow = (Button) itemView.findViewById(R.id.follow);
            userRequested = (Button) itemView.findViewById(R.id.requested);
            userFollowed = (Button) itemView.findViewById(R.id.followed);

        }
    }

    private void isBacking(final String userId, final Button userBack, final Button userRequested, final Button userBacked) {
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
        Log.d("User", "follow");
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

    private void addChat(final User user) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("ChatType", "Individual");
        bundle.putString("ID", user.getId());
        intent.putExtras(bundle);
        intent.setClass(context, chatRoom.class);
        context.startActivity(intent);
        customType(context, "left-to-right");
        checkFollowAddChat(user);

    }

    private void checkFollowAddChat(final User user) {
        DatabaseReference followRequest = FirebaseDatabase.getInstance().getReference("Follow");
        followRequest.child(firebaseUser.getUid()).child("followers").child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    // TODO: check existence of data in setting of request
                    // TODO: check request needed user type, and user and currentuser relationship type
                    DatabaseReference chatSettingReference = FirebaseDatabase.getInstance().getReference("Setting")
                            .child(user.getId()).child("Chat").child(checkAnonity(chatAnonity));

                    chatSettingReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Request_From_Follower").exists()) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Chat_request");
                    DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference("Chat").child("Contact");
                    // chatID = requestID
                    String chatId = contactReference.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("requestedID", user.getId());
                    map.put("requestingID", firebaseUser.getUid());
                    map.put("ID", chatId);
                    map.put("anonity", chatAnonity);

                    requestReference.child(chatId).setValue(map);

                    // chat request by currentuser to the User
                    Log.d("Request", "Requested " + user.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFollower(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(firebaseUser.getUid()).child("followers").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(firebaseUser.getUid()).child("followers").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("following").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("following").child(firebaseUser.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFollowing(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(firebaseUser.getUid()).child("following").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(firebaseUser.getUid()).child("following").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("followers").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blockUser(String userId) {
        FirebaseDatabase.getInstance().getReference("Block").child(firebaseUser.getUid()).child(userId).setValue(true);
    }

    private String checkAnonity(boolean chatAnonity) {
        if (chatAnonity) {
            return "Anonymous";
        } else {
            return "Non-anonymous";
        }
    }


}
