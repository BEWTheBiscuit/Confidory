package com.bisbizkuit.whistalk.MainPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.MainPages.setting.reportFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.SpecialClass.uploadDataToDatabase;
import com.bisbizkuit.whistalk.adapters.CommentRecyclerViewAdapter;
import com.bisbizkuit.whistalk.adapters.PostRecyclerViewAdapter;
import com.bisbizkuit.whistalk.objects.Comment;
import com.bisbizkuit.whistalk.objects.Post;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class post_details extends AppCompatActivity {

    View background;
    RecyclerView recyclerView;
    CircleImageView postUserIcon;
    TextView postText, postUsername, postLikedAmount, postDate, noComment;
    ImageView /*save,*/ like, setting, back, postComment, toolBarSetting;
    EditText writeComment;
    RecyclerView commentRecyclerView;

    String postId, postdateDate, publisher;

    List<Comment> commentList;
    List<Post> postList;
    CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    PostRecyclerViewAdapter postRecyclerViewAdapter;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        postId = bundle.getString("postId");

        background = (View) findViewById(R.id.postDetail);

        recyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        postList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                if (post.getAnonity()) {
                    background.setBackgroundColor(getResources().getColor(R.color.fillIn));
                } else {
                    background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(this, postList);
        recyclerView.setLayoutManager(linearLayoutManager1);
        recyclerView.setAdapter(postRecyclerViewAdapter);

        setting = (ImageView) recyclerView.findViewById(R.id.postSetting);
        back = (ImageView) findViewById(R.id.postDetailBackButton);
        noComment = (TextView) findViewById(R.id.noComment);
        //postComment = (ImageView) findViewById(R.id.uploadComment);
        //writeComment = (EditText) findViewById(R.id.commentDetailEdittext);

        commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        commentList = new ArrayList<>();
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(this, commentList, postId);
        commentRecyclerView.setAdapter(commentRecyclerViewAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //returnSetting(postId);

        //setUpPost();
        //liked(postId, like);
        //getLikedAmount(postLikedAmount, postId);
        //saved(postId, save);
        readComments();
        emptyList();

        /*like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Like", "clicked"); //not working
                if (!like.getTag().equals("liked")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postId).child(firebaseUser.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference("Posts")
                            .child(postId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (!post.getPublisher().equals(firebaseUser.getUid())) {
                                addNotification(post.getPublisher(), post.getPostId());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postId).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Save", "clicked");
                if (!save.getTag().equals("saved")) {
                    FirebaseDatabase.getInstance().getReference().child("Saved")
                            .child(postId).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saved")
                            .child(postId).child(firebaseUser.getUid()).removeValue();
                }
            }
        });*/

        /*writeComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!writeComment.getText().toString().trim().equals("")){
                    postComment.setClickable(true);
                    postComment.setImageResource(R.drawable.ic_blue_arrow_forward);
                    postComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadComment(writeComment.getText().toString(), postId, writeComment);
                        }
                    });
                } else {
                    postComment.setClickable(false);
                    postComment.setImageResource(R.drawable.ic_arrow_forward);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

    }
/*
    private void setUpPost() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                //set date
                try {
                    compareDate(post.getDate(), postDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                postText.setText(post.getText());
                if (!post.getAnonity()) {
                    publisherInformation(postUserIcon, postUsername, post.getPublisher());
                } else {
                    Picasso.get()
                            .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                            .into(postUserIcon);
                    postUsername.setText("Anonymous");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    /*private void publisherInformation(final CircleImageView postUserIcon, final TextView username, String userId) {
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


    }*/

    /*private void liked(String postId, final ImageView like) {
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
    }*/

    private void getLikedAmount(final TextView likedAmount, String postId) {
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likedAmount.setText(dataSnapshot.getChildrenCount() + " people liked");
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

    /*private void uploadComment(String comment, String postId, final EditText writeComment) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        setPostDate();

        int reported = 0;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("commentContent", comment);
        hashMap.put("date", postdateDate);
        hashMap.put("commentPublisher", userId);
        hashMap.put("spam", Integer.toString(reported));
        hashMap.put("inappropriate", Integer.toString(reported));
        hashMap.put("notFavored", Integer.toString(reported));

        commentReference.child(postId).child(userId).setValue(hashMap);

        addCommentsNotification(userId, postId, comment);

        progressDialog.dismiss();

        Toast.makeText(this, "Comment posted", Toast.LENGTH_LONG).show();

        clearAll(writeComment);
    }*/

    private void setPostDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        postdateDate = dateFormat.format(new Date());
    }

    /*private void clearAll(EditText writeComment) {
        writeComment.setText("");
    }*/

    private void readComments() {
        DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postId);
        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                emptyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        commentRecyclerViewAdapter.notifyDataSetChanged();
    }

    /*private void compareDate(String date, TextView postDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date originalDate = originalFormat.parse(date);
        SimpleDateFormat currentFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        String currentTime = currentFormat.format(new Date());
        Date currentDate = originalFormat.parse(currentTime);

        long timeDifferenceMills = currentDate.getTime() - originalDate.getTime();

        int hourDifference = (int) ( timeDifferenceMills / (1000 * 60 * 60) );
        int minDifference = (int) ( timeDifferenceMills / (1000 * 60));
        int secondDifference = (int) ( timeDifferenceMills / 1000);
        int dateDifference = (int) ( timeDifferenceMills / (1000 * 60 * 60 * 24));

        if (dateDifference >= 7) {
            if (originalDate.getMonth() == 1) {
                postDate.setText(originalDate.getDate() + " Jan " + originalDate.getYear());
            } else if (originalDate.getMonth() == 2) {
                postDate.setText(originalDate.getDate() + " Feb " + originalDate.getYear());
            } else if (originalDate.getMonth() == 3) {
                postDate.setText(originalDate.getDate() + " Mar " + originalDate.getYear());
            } else if (originalDate.getMonth() == 4) {
                postDate.setText(originalDate.getDate() + " Apr " + originalDate.getYear());
            } else if (originalDate.getMonth() == 5) {
                postDate.setText(originalDate.getDate() + " May " + originalDate.getYear());
            } else if (originalDate.getMonth() == 6) {
                postDate.setText(originalDate.getDate() + " Jun " + originalDate.getYear());
            } else if (originalDate.getMonth() == 7) {
                postDate.setText(originalDate.getDate() + " Jul " + originalDate.getYear());
            } else if (originalDate.getMonth() == 8) {
                postDate.setText(originalDate.getDate() + " Aug " + originalDate.getYear());
            } else if (originalDate.getMonth() == 9) {
                postDate.setText(originalDate.getDate() + " Sep " + originalDate.getYear());
            } else if (originalDate.getMonth() == 10) {
                postDate.setText(originalDate.getDate() + " Oct " + originalDate.getYear());
            } else if (originalDate.getMonth() == 11) {
                postDate.setText(originalDate.getDate() + " Nov " + originalDate.getYear());
            } else if (originalDate.getMonth() == 12) {
                postDate.setText(originalDate.getDate() + " Dec " + originalDate.getYear());
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
    }*/

    private void emptyList() {
        if (commentList.isEmpty()) {
            noComment.setVisibility(View.VISIBLE);
        } else {
            noComment.setVisibility(View.GONE);
        }
    }

    /*private void returnSetting(final String postId) {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);
                publisher = post.getPublisher();
                if (publisher.equals(firebaseUser.getUid())) {
                    toolBarSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Setting", "clicked");
                            PopupMenu popupMenu = new PopupMenu(post_details.this, toolBarSetting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
                                            final Dialog dialog = new Dialog(post_details.this);
                                            dialog.setContentView(R.layout.delete_alertdialog);
                                            dialog.setTitle(R.string.delete);
                                            Button delete = (Button) dialog.findViewById(R.id.dialog_delete);
                                            Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
                                            TextView title = (TextView) dialog.findViewById(R.id.deleteTitle);
                                            title.setText(R.string.deletePost);
                                            delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase.getInstance().getReference("Posts").child(postId).removeValue();
                                                    Intent intent = new Intent(post_details.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
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
                    toolBarSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popupMenu = new PopupMenu(post_details.this, toolBarSetting);
                            popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.report:
                                            getSupportFragmentManager().beginTransaction()
                                                    .setCustomAnimations(R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                                    .replace(R.id.postDetail, new reportFrag(postId, null, null)).commit();
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
    }*/

    private void addNotification(String userId, String postId) {
        DatabaseReference notificationReference1 = FirebaseDatabase.getInstance().getReference("Notifications").child(userId).push();
        String id = notificationReference1.getKey();

        setPostDate();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("postId", postId);
        hashMap.put("text", "Liked your post");
        hashMap.put("isPost", true);
        hashMap.put("date", postDate);
        hashMap.put("id", id);

        uploadDataToDatabase uploadDataToDatabase = new uploadDataToDatabase(hashMap);
        uploadDataToDatabase.uploadNotifData(userId);
    }

    private void addCommentsNotification(String userId, String postId, String writeComment) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId).push();
        String id = notificationReference.getKey();

        setPostDate();
        HashMap<String , Object> notificationHashMap = new HashMap<>();
        notificationHashMap.put("userId", firebaseUser.getUid());
        notificationHashMap.put("postId", postId);
        notificationHashMap.put("text", "commented: " + writeComment);
        notificationHashMap.put("isPost", false);
        notificationHashMap.put("date", postDate);
        notificationHashMap.put("id", id);

        uploadDataToDatabase uploadDataToDatabase = new uploadDataToDatabase(notificationHashMap);
        uploadDataToDatabase.uploadNotifData(userId);
    }

}
