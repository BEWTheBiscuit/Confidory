package com.bisbizkuit.whistalk.adapters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.User;
import com.bisbizkuit.whistalk.objects.notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.requestViewHolder> {

    Context context;
    List<User> userList;

    public FollowRequestAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public requestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowRequestAdapter.requestViewHolder(LayoutInflater.from(context).inflate(R.layout.back_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull requestViewHolder holder, int position) {
        final User user = userList.get(position);

        setUpProfileIcon(holder.userIcon, user.getIcon());

        holder.username.setText(user.getUsername());

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Follow_Request")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getId()).removeValue();
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Follow_Request")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference("Follow")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").child(user.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference("Follow")
                        .child(user.getId()).child("following").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                editor.putString("profileId", user.getId());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadeout, R.anim.fadein, R.anim.fadeout, R.anim.fadein)
                        .addToBackStack(null)
                        .replace(R.id.selectedFragView, new profileFrag(user.getId())).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class requestViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIcon;
        TextView username;
        Button accept;
        ImageView cancel;

        public requestViewHolder(@NonNull View itemView) {
            super(itemView);

            userIcon = (CircleImageView) itemView.findViewById(R.id.requestUserIcon);
            username = (TextView) itemView.findViewById(R.id.requestUsername);
            accept = (Button) itemView.findViewById(R.id.acceptButton);
            cancel = (ImageView) itemView.findViewById(R.id.deleteRequest);

        }
    }

    private void setUpProfileIcon(CircleImageView icon, String userIcon) {
        if (userIcon.equals("")) {
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                    .into(icon);
        } else {
            Picasso.get()
                    .load(userIcon)
                    .into(icon);
        }
    }
}
