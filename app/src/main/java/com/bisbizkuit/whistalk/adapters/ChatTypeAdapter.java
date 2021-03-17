package com.bisbizkuit.whistalk.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage.anonymousChat;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage.nonAnonymousChat;

public class ChatTypeAdapter extends FragmentStatePagerAdapter {

    public ChatTypeAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                nonAnonymousChat nonAnonymousChat = new nonAnonymousChat();
                return nonAnonymousChat;
            case 1:
                anonymousChat anonymousChat = new anonymousChat();
                return anonymousChat;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
