package com.bisbizkuit.whistalk.MainPages.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.Comment;
import com.bisbizkuit.whistalk.objects.Post;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reportFrag extends Fragment {

    String postId, commentId, userId;
    String[] listViewArray = new String[] {"Spam", "Inappropriate", "I don't like it"};

    ListView reportType;
    TextView reportTitle;
    ImageView reportBack;

    DatabaseReference reportReference;

    Context context;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_report, container, false);

        reportReference = FirebaseDatabase.getInstance().getReference("Reports");

        reportBack = (ImageView) view.findViewById(R.id.reportBack);
        reportType = (ListView) view.findViewById(R.id.reportType);
        reportTitle = (TextView) view.findViewById(R.id.reportTxt);

        reportBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back", "Clicked");
                getFragmentManager().popBackStack();
            }
        });

        ArrayAdapter<String> adapterReport = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listViewArray);
        reportType.setAdapter(adapterReport);
        reportType.setOnItemClickListener(itemClickListener);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.finger_paint);
        reportTitle.setTypeface(typeface);

        return view;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (postId != null) {
                checkPostData(parent.getItemAtPosition(position).toString());
            } else if (commentId != null) {
                checkCommentData(parent.getItemAtPosition(position).toString());
            } else if (userId != null) {
                checkUserData(parent.getItemAtPosition(position).toString());
            }
        }
    };

    public reportFrag(String postId, String commentId, String userId, Context context) {
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
        this.context = context;
    }

    public reportFrag(String postId, String commentId, String userId) {
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
    }

    private void checkPostData(final String reportType) {
        switch (reportType) {
            case "Spam":
                reportReference.child("posts").child(postId).child("spam")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "Inappropriate":
                reportReference.child("posts").child(postId).child("inappropriate")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "I don't like it":
                reportReference.child("posts").child(postId).child("notFavored")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
        }

    }

    private void checkCommentData(final String reportType) {
        switch (reportType) {
            case "Spam":
                reportReference.child("comments").child(commentId).child("spam")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "Inappropriate":
                reportReference.child("comments").child(commentId).child("inappropriate")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "I don't like it":
                reportReference.child("comments").child(commentId).child("notFavored")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
        }
    }

    private void checkUserData(final String reportType) {
        switch (reportType) {
            case "Spam":
                reportReference.child("users").child(userId).child("spam")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "Inappropriate":
                reportReference.child("users").child(userId).child("inappropriate")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
            case "I don't like it":
                reportReference.child("users").child(userId).child("notFavored")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                finishReport();
                break;
        }
    }

    private void finishReport() {
        getFragmentManager().popBackStack();
        Snackbar.make(view, "Thank you for letting us know about your thought. We will follow up your report.", Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        context = this.context;
    }
}
