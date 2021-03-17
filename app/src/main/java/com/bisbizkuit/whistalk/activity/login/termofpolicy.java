package com.bisbizkuit.whistalk.activity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;

public class termofpolicy extends AppCompatActivity {

    ImageView lastPageButton;
    TextView termOfPolicyContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termofpolicy);

        lastPageButton = (ImageView) findViewById(R.id.lastPagePolicy);
        termOfPolicyContent = (TextView) findViewById(R.id.termofpolicy);

        setTermOfPolicyContent();

        lastPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                ds.setUnderlineText(true);
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
                ds.setUnderlineText(true);
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
                ds.setUnderlineText(true);
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
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan1, 1217, 1237, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2,1239, 1257, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan3, 4581, 4607, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan4, 4634, 4662, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        termOfPolicyContent.setText(spannableString);
        termOfPolicyContent.setMovementMethod(LinkMovementMethod.getInstance());
        termOfPolicyContent.setHighlightColor(getResources().getColor(R.color.colorAccent));
    }
}
