package com.bisbizkuit.whistalk.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bisbizkuit.whistalk.activity.login.register.fragments.emailFrag;
import com.bisbizkuit.whistalk.activity.login.register.fragments.namesFrag;
import com.bisbizkuit.whistalk.activity.login.register.fragments.passwordFrag;
import com.bisbizkuit.whistalk.activity.login.register.fragments.realnamesFrag;
import com.bisbizkuit.whistalk.activity.login.register.fragments.term_of_policyFrag;

public class SignUpViewPagerAdapter extends FragmentStatePagerAdapter {

    public SignUpViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                namesFrag namesFrag = new namesFrag();
                return namesFrag;
            case 1:
                realnamesFrag realnamesFrag = new realnamesFrag();
                return realnamesFrag;
            case 2:
                emailFrag emailFrag = new emailFrag();
                return emailFrag;
            case 3:
                passwordFrag passwordFrag = new passwordFrag();
                return passwordFrag;
            case 4:
                term_of_policyFrag term_of_policyFrag = new term_of_policyFrag();
                return term_of_policyFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
