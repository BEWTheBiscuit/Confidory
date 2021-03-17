package com.bisbizkuit.whistalk.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.Gravity;
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

import com.bisbizkuit.whistalk.MainPages.MainActivity;
import com.bisbizkuit.whistalk.MainPages.post_details;
import com.bisbizkuit.whistalk.MainPages.setting.reportFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.Comment;
import com.bisbizkuit.whistalk.objects.Post;
import com.bisbizkuit.whistalk.objects.User;
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

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    Context context;
    List<Comment> commentList;
    String postId;

    public CommentRecyclerViewAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comments, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {
        final Comment comment = commentList.get(position);
        //date
        try {
            compareDate(comment.getDate(), holder.commentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                enableSettingChoices(comment, post, holder.itemView);
                publisherInformation(holder.commentUserIcon, holder.commentUsername, post, comment.getCommentPublisher());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.commentTextComment.setText(comment.getCommentContent());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentUsername, commentDate, commentTextComment;
        CircleImageView commentUserIcon;
        View commentLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUsername = (TextView) itemView.findViewById(R.id.commentUsername);
            commentDate = (TextView) itemView.findViewById(R.id.commentDate);
            commentTextComment = (TextView) itemView.findViewById(R.id.commentContent);
            commentUserIcon = (CircleImageView) itemView.findViewById(R.id.commentUserIcon);

        }
    }

    private void publisherInformation(final CircleImageView postUserIcon, final TextView username, final Post post, final String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (post.getPublisher().equals(userId)) {
                        if (post.getAnonity()) {
                            Picasso.get()
                                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                    .into(postUserIcon);
                            username.setText("Anonymous");
                    } else {
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
                } else {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    }

    private void enableSettingChoices(final Comment comment, Post post, final View view) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (comment.getCommentPublisher().equals(firebaseUser.getUid())) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
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
                                            FirebaseDatabase.getInstance().getReference("Comments").child(postId).child(comment.getCommentId()).removeValue();
                                            Intent intent = new Intent(context, post_details.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("postId", postId);
                                            intent.putExtras(bundle);
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
                    return false;
                }
            });
        } else if (post.getPublisher().equals(firebaseUser.getUid())) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                    popupMenu.getMenu().add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                            .replace(R.id.selectedFragView, new reportFrag(null, comment.getCommentId(), null)).commit();
                                    break;
                                case R.id.delete:
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.delete_alertdialog);
                                    dialog.setTitle(R.string.delete);
                                    Button delete = (Button) dialog.findViewById(R.id.dialog_delete);
                                    Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
                                    delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FirebaseDatabase.getInstance().getReference("Comments").child(comment.getCommentId()).removeValue();
                                            Intent intent = new Intent(context, post_details.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("postId", postId);
                                            intent.putExtras(bundle);
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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenu().add(Menu.NONE, R.id.report, Menu.NONE, R.string.report);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein).addToBackStack(null)
                                            .replace(R.id.postDetail, new reportFrag(null, comment.getCommentId(), null)).commit();
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

}
