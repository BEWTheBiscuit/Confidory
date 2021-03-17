package com.bisbizkuit.whistalk.MainPages.setting;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bisbizkuit.whistalk.MainPages.MainActivityFragment.profileFrag;
import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.ParsedUrl;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class editprofileFrag extends Fragment {

    private EditText usernameEdit, bioEdit;
    private TextView confirm;
    private CircleImageView userIconEdit;
    private View editProfileLayout;

    private String usernameCheck;
    private String bioCheck;

    private Context context;

    private Uri selectedUri;
    private String url, originalIcon;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int SELECT_IMAGE = 2;

    private StorageReference storageUserIcon;
    private DatabaseReference userReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        usernameEdit = (EditText) view.findViewById(R.id.usernameEdit);
        TextView textTitle = (TextView) view.findViewById(R.id.editProfileTitle);
        bioEdit = (EditText) view.findViewById(R.id.bioEdit);
        TextView cancel = (TextView) view.findViewById(R.id.cancelEdit);
        confirm = (TextView) view.findViewById(R.id.confirmEdit);
        editProfileLayout = (View) view.findViewById(R.id.editProfileLayout);
        userIconEdit = (CircleImageView) view.findViewById(R.id.userIconEdit);

        storageUserIcon = FirebaseStorage.getInstance().getReference("UserIcon");
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        usernameEdit.addTextChangedListener(textWatcher);
        bioEdit.addTextChangedListener(textWatcher);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        userIconEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.edit_icon);
                bottomSheetDialog.show();
                Button defaultIcon = (Button) bottomSheetDialog.findViewById(R.id.defaultIcon);
                Button customIcon = (Button) bottomSheetDialog.findViewById(R.id.customIcon);
                Button cancelIconEdit = (Button) bottomSheetDialog.findViewById(R.id.cancelIconEdit);
                TextView title = (TextView) bottomSheetDialog.findViewById(R.id.iconEditTitle);
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.finger_paint);
                defaultIcon.setTypeface(typeface);
                customIcon.setTypeface(typeface);
                cancelIconEdit.setTypeface(typeface);
                title.setTypeface(typeface);
                defaultIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                .into(userIconEdit);
                        url = "";
                        confirm.setTextColor(context.getResources().getColor(R.color.colorFinish));
                        confirm.setClickable(true);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateUserProfile();
                                updateUserIcon(null, FirebaseAuth.getInstance().getCurrentUser());
                                getFragmentManager().popBackStack();
                            }
                        });
                        bottomSheetDialog.dismiss();
                    }
                });
                customIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 22) {
                            checkAndRequestForPermission();
                        } else {
                            openGallery();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                cancelIconEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.finger_paint);
        textTitle.setTypeface(typeface);

        setUpCreatedData();

        return view;
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), "Please accept to grant us the permission.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedUri = data.getData();
            userIconEdit.setImageURI(selectedUri);
            confirm.setTextColor(context.getResources().getColor(R.color.colorFinish));
            confirm.setClickable(true);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUserProfile();
                    updateUserIcon(selectedUri, FirebaseAuth.getInstance().getCurrentUser());
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setUpCreatedData() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bioEdit.setText(user.getBio());
                usernameEdit.setText(user.getUsername());
                if (!user.getIcon().trim().equals("")) {
                    Picasso.get()
                            .load(user.getIcon())
                            .into(userIconEdit);
                    originalIcon = user.getIcon();
                } else {
                    url = "";
                    Picasso.get()
                            .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                            .into(userIconEdit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserProfile() {
        String newUsername = usernameEdit.getText().toString();
        String newBio = bioEdit.getText().toString();

        userReference.child("username").setValue(newUsername);
        userReference.child("bio").setValue(newBio);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            enableConfirm(usernameEdit.getText().toString(), bioEdit.getText().toString());
            usernameCheck = usernameEdit.getText().toString();
            bioCheck = bioEdit.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void enableConfirm(final String newUsername, final String newBio) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (isAdded()) {
                    if ((newUsername.equals(user.getUsername()) && newBio.equals(user.getBio()))
                            || newUsername.trim().equals("")) {
                        confirm.setTextColor(context.getResources().getColor(R.color.colorTransparentWhite));
                        confirm.setClickable(false);
                    } else {
                        confirm.setTextColor(context.getResources().getColor(R.color.colorFinish));
                        confirm.setClickable(true);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateUserProfile();
                                getFragmentManager().popBackStack();

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserIcon(Uri selectedUri, final FirebaseUser currentUser) {
        if (selectedUri == null) {
            FirebaseStorage.getInstance().getReference("UserIcon").child(currentUser.getUid()).delete();
            userReference.child("icon").setValue("");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            final StorageReference imageFilePath = storageUserIcon.child(currentUser.getUid());
            imageFilePath.putFile(selectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            Log.d("URI", uri.toString());
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(usernameCheck)
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Auth", "Uploaded auth default info");
                                            url = uri.toString();
                                            userReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    if (!user.getIcon().trim().equals("")) {
                                                        userReference.child("icon").setValue(url);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("icon").setValue(url);
                                            progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Snackbar.make(editProfileLayout, "We cannot open your photo gallery as permission is not granted.", Snackbar.LENGTH_LONG).show();
                }
                return;
        }
    }
}

//https://www.youtube.com/watch?v=pDjlFq6zTJg
//https://www.youtube.com/watch?v=ZGNt_UM5x-4
//https://www.youtube.com/watch?v=MVB3P5ZmkQc