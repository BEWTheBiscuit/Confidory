package com.bisbizkuit.whistalk.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.Chat;
import com.bisbizkuit.whistalk.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {

    Context context;
    List<Chat> chatList;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRecyclerViewAdapter.ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Chat chat = chatList.get(position);
        // TODO: if anonity of chat == true, user.setUsername("Anonymous");
        // TODO: chat link to user && set up a database for group list and the members
        /*holder.chatUsername.setText(user.getUsername());
        if (user.getIcon().equals("")) {
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                    .into(holder.chatUserIcon);
        } else {
            Picasso.get()
                    .load(user.getIcon())
                    .into(holder.chatUserIcon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chatUserIcon;
        TextView chatUsername;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            chatUserIcon = (CircleImageView) itemView.findViewById(R.id.chatUserIcon);
            chatUsername = (TextView) itemView.findViewById(R.id.chatUsername);
        }
    }

}
