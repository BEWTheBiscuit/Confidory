package com.bisbizkuit.whistalk.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.HomePage_PostDisplay.privatepostFrag;
import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.HomePage_PostDisplay.publicpostFrag;

public class HomePostDisplayAdapter extends FragmentStatePagerAdapter {

    public HomePostDisplayAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                publicpostFrag publicpostFrag = new publicpostFrag();
                return publicpostFrag;
            case 1:
                privatepostFrag privatepostFrag = new privatepostFrag();
                return privatepostFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
