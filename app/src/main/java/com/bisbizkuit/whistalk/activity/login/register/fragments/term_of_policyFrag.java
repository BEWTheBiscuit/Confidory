package com.bisbizkuit.whistalk.activity.login.register.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bisbizkuit.whistalk.SpecialClass.FragmentToActivityCommunicator;
import com.bisbizkuit.whistalk.R;

public class term_of_policyFrag extends Fragment {

    private Button createAc;

    private FragmentToActivityCommunicator fragmentCommunicator;
    private Context context;

    private Animation animation;
    private Boolean accept;

    private TextView termOfPolicy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_term_of_policy, container, false);

        createAc = view.findViewById(R.id.CreateAccount);
        termOfPolicy = view.findViewById(R.id.term_of_policy);

        createAc.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonAnimations(createAc, R.anim.fadeout);
                accept = true;
                fragmentCommunicator.passCreateBooleanToActivity(accept);
            }
        });

        setTermOfPolicyContent();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        fragmentCommunicator = (FragmentToActivityCommunicator) context;
    }

    private void setButtonAnimations(Button button, int id) {
        animation = AnimationUtils.loadAnimation(getContext(),id);
        button.startAnimation(animation);
    }

    private void setTermOfPolicyContent() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.term_of_policy));
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = "https://www.google.com/policies/privacy/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = "https://firebase.google.com/policies/analytics";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = "https://privacypolicytemplate.net/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan4 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = "https://app-privacy-policy-generator.firebaseapp.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan1, 1213, 1234, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2,1235, 1254, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan3, 4577, 4603, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan4, 4630, 4658, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        termOfPolicy.setText(spannableString);
        termOfPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        termOfPolicy.setHighlightColor(getResources().getColor(R.color.colorAccent));
    }
}
