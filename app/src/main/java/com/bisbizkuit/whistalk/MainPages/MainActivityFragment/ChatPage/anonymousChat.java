package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static maes.tech.intentanim.CustomIntent.customType;

public class anonymousChat extends Fragment {

    private FloatingActionButton addAnonChat;
    private TextView noChatFound;
    private RecyclerView anonChatRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anonymous_chat, container, false);

        addAnonChat = (FloatingActionButton) view.findViewById(R.id.addAnonChat);
        noChatFound = (TextView) view.findViewById(R.id.noChatFound);
        anonChatRecyclerView = (RecyclerView) view.findViewById(R.id.anonymousChat);

        addAnonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), addChat.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Anonity", true);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
                customType(getContext(), "bottom-to-up");
            }
        });

        return view;
    }

}
