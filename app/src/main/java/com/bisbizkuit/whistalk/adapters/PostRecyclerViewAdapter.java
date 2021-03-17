package com.bisbizkuit.whistalk.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bisbizkuit.whistalk.MainPages.MainActivity;
import com.bisbizkuit.whistalk.MainPages.post_details;
import com.bisbizkuit.whistalk.MainPages.setting.follow_detailFrag;
import com.bisbizkuit.whistalk.MainPages.setting.reportFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.Post;
import com.bisbizkuit.whistalk.objects.User;
import com.bisbizkuit.whistalk.objects.notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    Context context;
    List<Post> postList;

    private String postDate;

    private FirebaseUser firebaseUser;

    public PostRecyclerViewAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.posts, parent, false);
        return new PostRecyclerViewAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Post post = postList.get(position);

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left));

        if (post == null) {
            noPostContent(holder.postContent, holder.postNoContent, holder.postLayout);
        } else {
            holder.postNoContent.setVisibility(View.INVISIBLE);
            holder.postContent.setVisibility(View.VISIBLE);
            holder.postLayout.setBackground(context.getResources().getDrawable(R.drawable.post_container));
            holder.postTextContent.setText(post.getText());
            //set Date
            try {
                compareDate(post.getDate(), holder.postDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (post.getAnonity()) {
                    holder.isAnonity.setVisibility(View.VISIBLE);
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
                    holder.postUsername.setTypeface(typeface);
                    publisherInformation(holder.postUserIcon, holder.postUsername, post.getPublisher());
                } else {
                    holder.isAnonity.setVisibility(View.GONE);
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
                    holder.postUsername.setTypeface(typeface);
                    publisherInformation(holder.postUserIcon, holder.postUsername, post.getPublisher());
                }
            } else {
                if (!post.getAnonity()) {
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
                    holder.postUsername.setTypeface(typeface);
                    publisherInformation(holder.postUserIcon, holder.postUsername, post.getPublisher());
                } else {
                    Picasso.get()
                            .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                            .into(holder.postUserIcon);
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
                    holder.postUsername.setTypeface(typeface);
                    holder.postUsername.setText("Anonymous");
                }
            }

            liked(post.getPostId(), holder.like);
            getLikedAmount(holder.postLikedAmount, post.getPostId());
            saved(post.getPostId(), holder.save);

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Like", "clicked"); //not working
                    if (!holder.like.getTag().equals("liked")) {
                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                        addNotification(post.getPublisher(), post.getPostId());
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Notifications")
                                .child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    notification notification = snapshot.getValue(notification.class);
                                    if (notification.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            && notification.getText().equals("Liked your post")) {
                                        FirebaseDatabase.getInstance().getReference("Notifications")
                                                .child(post.getPublisher()).child(notification.getId()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });

            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Save", "clicked");
                    if (!holder.save.getTag().equals("saved")) {
                        FirebaseDatabase.getInstance().getReference().child("Saved")
                                .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Saved")
                                .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    }
                }
            });

            holder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, post_details.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("postId", post.getPostId());
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });

            returnPostUser(post.getPostId(), holder.setting);

            checkPublic(post.getPostId(), post.getPublisher(), holder.isPublic);

            holder.writeComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!holder.writeComment.getText().toString().trim().equals("")){
                        holder.leaveComment.setClickable(true);
                        holder.leaveComment.setImageResource(R.drawable.ic_brown_arrow_forward);
                    } else {
                        holder.leaveComment.setClickable(false);
                        holder.leaveComment.setImageResource(R.drawable.ic_grey_arrow_forward);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            holder.leaveComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadComment(post.getPublisher(), holder.writeComment.getText().toString(), post.getPostId(), holder.writeComment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView postUsername, postDate, postTextContent, postLikedAmount, postNoContent;
        public ImageView save, like, setting, leaveComment, isPublic, isAnonity;
        public CircleImageView postUserIcon;
        public EditText writeComment;
        public View includeView, postLayout, postContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postUsername = (TextView) itemView.findViewById(R.id.usernamePost);
            postDate = (TextView) itemView.findViewById(R.id.postDate);
            postUserIcon = (CircleImageView) itemView.findViewById(R.id.postUserIcon);
            postTextContent = (TextView) itemView.findViewById(R.id.postTextContent);
            save = (ImageView) itemView.findViewById(R.id.save);
            like = (ImageView) itemView.findViewById(R.id.like);
            setting = (ImageView) itemView.findViewById(R.id.postSetting);
            postLikedAmount = (TextView) itemView.findViewById(R.id.likedAmount);
            postLayout = (View) itemView.findViewById(R.id.cardviewPost);
            includeView = (View) itemView.findViewById(R.id.postLeaveComment);
            leaveComment = (ImageView) includeView.findViewById(R.id.postComment);
            writeComment = (EditText) includeView.findViewById(R.id.commentEdittext);
            isPublic = (ImageView) itemView.findViewById(R.id.isPublic);
            isAnonity = (ImageView) itemView.findViewById(R.id.isAnonity);
            postContent = (View) itemView.findViewById(R.id.postContent);
            postNoContent = (TextView) itemView.findViewById(R.id.noContent);
        }
    }

    private void publisherInformation(final CircleImageView postUserIcon, final TextView username, String userId) {
        if (userId.equals("Anonymous")) {
            username.setText("Anonymous");
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                    .into(postUserIcon);
        } else if (userId.equals("ADMIN")) {
            username.setText("Confidory Official");
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/app_icon.png?alt=media&token=a353adda-bd59-497a-b1a9-191fa207f8f4")
                    .into(postUserIcon);
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getIcon().equals("")) {
                        Picasso.get()
                                .load(user.getIcon())
                                .into(postUserIcon);
                    } else {
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                .into(postUserIcon);
                    }
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void liked(String postId, final ImageView like) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    like.setImageResource(R.drawable.ic_liked);
                    like.setTag("liked");
                } else {
                    like.setImageResource(R.drawable.ic_unliked);
                    like.setTag("unliked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikedAmount(final TextView likedAmount, final String postId) {
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    likedAmount.setVisibility(View.INVISIBLE);
                } else {
                    likedAmount.setText(dataSnapshot.getChildrenCount() + " liked");
                    likedAmount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.selectedFragView, new follow_detailFrag("LIKES", postId)).commit();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saved(String postId, final ImageView save) {
        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference()
                .child("Saved")
                .child(postId);

        saveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    save.setImageResource(R.drawable.ic_saved);
                    save.setTag("saved");
                } else {
                    save.setImageResource(R.drawable.ic_unsaved);
                    save.setTag("unsaved");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadComment(String postUserId, String comment, String postId, final EditText writeComment) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        setPostDate();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Comments");

        int reported = 0;

        String commentId = commentReference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("commentContent", comment);
        hashMap.put("date", postDate);
        hashMap.put("commentPublisher", userId);
        hashMap.put("commentId", commentId);

        commentReference.child(postId).child(commentId).setValue(hashMap);

        addCommentsNotification(postUserId, postId, writeComment);

        progressDialog.dismiss();

        Toast.makeText(context, "Comment posted", Toast.LENGTH_LONG).show();

        clearAll(writeComment);
    }

    private void setPostDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        postDate = dateFormat.format(new Date());
    }

    private void clearAll(EditText writeComment) {
        writeComment.setText("");
    }

    private void compareDate(String date, TextView postDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
        Date originalDate = originalFormat.parse(date);
        SimpleDateFormat currentFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss", Locale.getDefault());
        String currentTime = currentFormat.format(new Date());
        Date currentDate = originalFormat.parse(currentTime);

        long timeDifferenceMills = currentDate.getTime() - originalDate.getTime();

        int hourDifference = (int) ( timeDifferenceMills / (1000 * 60 * 60) );
        int minDifference = (int) ( timeDifferenceMills / (1000 * 60));
        int secondDifference = (int) ( timeDifferenceMills / 1000);
        int dateDifference = (int) ( timeDifferenceMills / (1000 * 60 * 60 * 24));

        String year = date.substring(6,10);

        if (dateDifference >= 7) {
            if (originalDate.getMonth() == 1) {
                postDate.setText(originalDate.getDate() + " Jan " + year);
            } else if (originalDate.getMonth() == 2) {
                postDate.setText(originalDate.getDate() + " Feb " + year);
            } else if (originalDate.getMonth() == 3) {
                postDate.setText(originalDate.getDate() + " Mar " + year);
            } else if (originalDate.getMonth() == 4) {
                postDate.setText(originalDate.getDate() + " Apr " + year);
            } else if (originalDate.getMonth() == 5) {
                postDate.setText(originalDate.getDate() + " May " + year);
            } else if (originalDate.getMonth() == 6) {
                postDate.setText(originalDate.getDate() + " Jun " + year);
            } else if (originalDate.getMonth() == 7) {
                postDate.setText(originalDate.getDate() + " Jul " + year);
            } else if (originalDate.getMonth() == 8) {
                postDate.setText(originalDate.getDate() + " Aug " + year);
            } else if (originalDate.getMonth() == 9) {
                postDate.setText(originalDate.getDate() + " Sep " + year);
            } else if (originalDate.getMonth() == 10) {
                postDate.setText(originalDate.getDate() + " Oct " + year);
            } else if (originalDate.getMonth() == 11) {
                postDate.setText(originalDate.getDate() + " Nov " + year);
            } else if (originalDate.getMonth() == 12) {
                postDate.setText(originalDate.getDate() + " Dec " + year);
            }
        } else if (dateDifference >= 1) {
            postDate.setText(dateDifference + " days ago");
        } else if (hourDifference >= 1) {
            postDate.setText(hourDifference + " hours ago");
        } else if (minDifference >= 1) {
            postDate.setText(minDifference + " mins ago");
        } else if (secondDifference >= 1) {
            postDate.setText(secondDifference + " seconds ago");
        }
    }

    private void checkPublic(String postId, final String postPublisher, final ImageView isPublic) {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (postPublisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if (post != null) {
                        if (post.getViewPublic()) {
                            isPublic.setVisibility(View.VISIBLE);
                        } else {
                            isPublic.setVisibility(View.GONE);
                        }
                    } else {
                        postList.remove(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

    }

    private void returnPostUser(final String postId, final ImageView setting) {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);
                if (post == null) {
                    postList.remove(post);
                } else {
                String publisher = post.getPublisher();
                if (publisher.equals(firebaseUser.getUid())) {
                    setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Setting", "clicked");
                            PopupMenu popupMenu = new PopupMenu(context, setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
                                            final Dialog dialog = new Dialog(context);
                                            dialog.setContentView(R.layout.delete_alertdialog);
                                            dialog.setTitle(R.string.delete);
                                            Button delete = (Button) dialog.findViewById(R.id.dialog_delete);
                                            Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
                                            delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase.getInstance().getReference("Posts").child(postId).removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Notifications")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        notification notification = snapshot.getValue(notification.class);
                                                                        if (notification.getPostId().equals(postId)) {
                                                                            FirebaseDatabase.getInstance().getReference("Notifications")
                                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notification.getId()).removeValue();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                    FirebaseDatabase.getInstance().getReference("Saved").child(postId).removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Likes").child(postId).removeValue();
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    context.startActivity(intent);
                                                    ((Activity) context).finish();
                                                }
                                            });
                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.show();
                                            break;
                                    }

                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });
                } else {
                    setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popupMenu = new PopupMenu(context, setting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.report:
                                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                    .replace(R.id.selectedFragView, new reportFrag(postId, null, null, context)).commit();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userId, String postId) {
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        String id = notificationReference.push().getKey();

        setPostDate();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("postId", postId);
        hashMap.put("text", "Liked your post");
        hashMap.put("isPost", true);
        hashMap.put("date", postDate);
        hashMap.put("id", id);

        notificationReference.child(id).setValue(hashMap);
    }

    private void addCommentsNotification(String userId, String postId, EditText writeComment) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        String id = notificationReference.push().getKey();

        setPostDate();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("postId", postId);
        hashMap.put("text", "commented: " + writeComment.getText().toString());
        hashMap.put("isPost", true);
        hashMap.put("date", postDate);
        hashMap.put("id", id);

        notificationReference.child(id).setValue(hashMap);
    }

    private void noPostContent(View content, TextView noContent, View postBackground) {
        content.setVisibility(View.GONE);
        noContent.setVisibility(View.VISIBLE);
        postBackground.setBackground(context.getResources().getDrawable(R.drawable.post_container_grey));
    }
}
