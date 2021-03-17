package com.bisbizkuit.whistalk.adapters;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.profileFrag;
import com.bisbizkuit.whistalk.MainPages.post_details;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.User;
import com.bisbizkuit.whistalk.objects.notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {

    Context context;
    List<notification> notificationList;

    public notificationAdapter(Context context, List<notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("notification", "create view");
        View view = LayoutInflater.from(context).inflate(R.layout.notification_items, parent, false);
        return new notificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("notifications", "layoutSetUp");
        final notification notification = notificationList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getIsPost()) {
                    Intent intent = new Intent();
                    intent.setClass(context, post_details.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("postId", notification.getPostId());
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            }
        });

        holder.notificationSubtitle.setText(notification.getText());

        getUserInfo(holder.notificationIcon, holder.notificationTitle, notification.getUserId());

        try {
            compareDate(notification.getDate(), holder.notificationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getIsPost()) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("postId", notification.getPostId());
                    editor.apply();

                    Intent intent = new Intent(context, post_details.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("postId", notification.getPostId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTitle, notificationSubtitle, notificationDate;
        CircleImageView notificationIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationIcon = (CircleImageView) itemView.findViewById(R.id.notificationIcon);
            notificationSubtitle = (TextView) itemView.findViewById(R.id.notificationSubtitle);
            notificationTitle = (TextView) itemView.findViewById(R.id.notificationTitle);
            notificationDate = (TextView) itemView.findViewById(R.id.notificationDate);
        }
    }

    private void getUserInfo(final ImageView notificationIcon, final TextView notificationTitle, String publisherId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("notifications", "layoutsetUp");
                if (user.getIcon().equals("")) {
                    Picasso.get()
                            .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                            .into(notificationIcon);
                } else {
                    Picasso.get()
                            .load(user.getIcon())
                            .into(notificationIcon);
                }
                notificationTitle.setText(user.getUsername());
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
}
