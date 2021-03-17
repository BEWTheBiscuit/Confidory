package com.bisbizkuit.whistalk.MainPages.MainActivityFragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class uploadFrag extends Fragment implements View.OnClickListener, TextWatcher {

    private EditText uploadText;
    private TextView postButton;
    private Switch anonity, publicViewable;

    private boolean anonymous, viewPublic;
    private String uploadTextContent, userId, postDate;

    private DatabaseReference postReference;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadText = (EditText) view.findViewById(R.id.uploadText);
        postButton = (TextView) view.findViewById(R.id.uploadPost);
        anonity = (Switch) view.findViewById(R.id.anonity);
        publicViewable = (Switch) view.findViewById(R.id.publicViewable);

        uploadText.addTextChangedListener(this);

        postButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadPost:
                setPostAnonity();
                setPostPublic();
                setUpload();
                break;
        }
    }

    private void setPostAnonity() {
        if (anonity.isChecked()) {
            anonymous = true;
        } else {
            anonymous = false;
        }
    }

    private void setPostPublic() {
        if (publicViewable.isChecked()) {
            viewPublic = true;
        } else {
            viewPublic = false;
        }
    }

    private void setUpload() {

        uploadTextContent = uploadText.getText().toString();
        userId = firebaseUser.getUid();
        setPostDate();
        uploadPostData(userId, uploadTextContent);

    }

    private void activatePostButton(String uploadContent) {
        if (!uploadContent.trim().equals("")) {
            postButton.setEnabled(true);
            postButton.setClickable(true);
            postButton.setTextColor(getResources().getColor(R.color.colorFinish));
        } else {
            postButton.setClickable(false);
            postButton.setEnabled(false);
            postButton.setTextColor(getResources().getColor(R.color.colorTransparentWhite));
        }
    }

    private void uploadPostData(String userId, String uploadTextContent) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.show();

        int reported = 0;

        postReference = FirebaseDatabase.getInstance().getReference("Posts");
        String postId = postReference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("anonity", anonymous);
        hashMap.put("viewPublic", viewPublic);
        hashMap.put("publisher", userId);
        hashMap.put("text", uploadTextContent);
        hashMap.put("date", postDate);

        postReference.child(postId).setValue(hashMap);

        progressDialog.dismiss();

        Toast.makeText(getContext(), "Post uploaded", Toast.LENGTH_LONG).show();

        clearAll();
    }

    private void clearAll() {
        uploadText.setText("");
        anonity.setChecked(false);
        publicViewable.setChecked(false);
    }

    private void setPostDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        postDate = dateFormat.format(new Date());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        uploadTextContent = uploadText.getText().toString();
        activatePostButton(uploadTextContent);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
