package com.bisbizkuit.whistalk.MainPages.MainActivityFragment.ChatPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisbizkuit.whistalk.R;
import com.bisbizkuit.whistalk.SpecialClass.CallBacks;
import com.bisbizkuit.whistalk.objects.ChatRequest;
import com.bisbizkuit.whistalk.objects.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import static maes.tech.intentanim.CustomIntent.customType;

public class chatRoom extends AppCompatActivity {

    private TextView chatUsername, verificationAccept, verificationBlock, verificationDelete;
    private CircleImageView chatUserIcon;
    private ImageView chatRoomBackButton;
    private View includeChatInput, chatVerification;
    private EditText chatMessageEditText;
    private ImageView chatSendMessage, chatOtherMedia;

    private String chatUserID, chatGroupID, chatType;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        chatType = bundle.getString("ChatType");
        if (chatType.equals("Group")) {
            chatGroupID = bundle.getString("ID");
            chatUserID = null;
        } else if (chatType.equals("Individual")) {
            chatUserID = bundle.getString("ID");
            chatGroupID = null;
        }

        chatUsername = (TextView) findViewById(R.id.chatRoomActionBarUsername);
        chatUserIcon = (CircleImageView) findViewById(R.id.chatRoomActionBarUserIcon);
        chatRoomBackButton = (ImageView) findViewById(R.id.chatRoomBack);
        includeChatInput = (View) findViewById(R.id.chatMessageInput);
        chatMessageEditText = (EditText) includeChatInput.findViewById(R.id.chatMessageEditText);
        chatSendMessage = (ImageView) includeChatInput.findViewById(R.id.chatMessageSend);
        chatOtherMedia = (ImageView) includeChatInput.findViewById(R.id.otherOptionsMessage);
        chatVerification = (View) findViewById(R.id.chatVerification);
        verificationAccept = (TextView) chatVerification.findViewById(R.id.verifyAcceptChat);
        verificationBlock = (TextView) chatVerification.findViewById(R.id.verifyBlockChat);
        verificationDelete = (TextView) chatVerification.findViewById(R.id.verifyDeleteChat);

        checkChatRequest();

        chatRoomBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                customType(chatRoom.this, "right-to-left");
            }
        });

        chatMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkMessageEditText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (chatUserID == null && chatGroupID != null) {

        } else if (chatGroupID == null && chatUserID != null) {
            setUpChatUser(true);
        }

        verificationDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Chat").child("Contact").child(chatUserID).child(currentUser.getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid()).child(chatUserID).removeValue();
            }
        });

        verificationBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getApplicationContext());
                bottomSheetDialog.setContentView(R.layout.block_alert);
                bottomSheetDialog.show();
                Button confirmBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_blockUser);
                Button cancelBlockUser = (Button) bottomSheetDialog.findViewById(R.id.dialog_cancelBlock);
                cancelBlockUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                confirmBlockUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference("Chat").child("Contact").child(chatUserID).child(currentUser.getUid()).removeValue();
                        FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid()).child(chatUserID).removeValue();
                        deleteFollowing(chatUserID);
                        deleteFollower(chatUserID);
                        blockUser(chatUserID);
                        bottomSheetDialog.dismiss();

                    }
                });
                FirebaseDatabase.getInstance().getReference("Chat").child("Contact").child(chatUserID).child(currentUser.getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid()).child(chatUserID).removeValue();
                deleteFollowing(chatUserID);
                deleteFollower(chatUserID);
                blockUser(chatUserID);
            }
        });

        verificationAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createContactData();

                FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid()).child(chatUserID).removeValue();


                //TODO: create database chats.groups reference
                // create chatAnonity.chatsID/GroupsID.userId.boolean
                // TODO: anonymous & non anonymous list and chat database (access anonity in contact or in chat?)

                FirebaseDatabase.getInstance().getReference("Chat_request").child(currentUser.getUid()).child(chatUserID).removeValue();
            }
        });
    }

    private void setUpChatUser(boolean anonity) {
        if (anonity) {
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                    .into(chatUserIcon);
            chatUsername.setText("Anonymous");
        } else {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(chatUserID);
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    chatUsername.setText(user.getUsername());
                    if (user.getIcon().equals("")) {
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/whistalk-5461f.appspot.com/o/user_icon.png?alt=media&token=7466a283-06c7-4145-beb9-a1eb4fa5a39b")
                                .into(chatUserIcon);
                    } else {
                        Picasso.get()
                                .load(user.getIcon())
                                .into(chatUserIcon);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkMessageEditText(String input) {
        if (input.trim().equals("")) {
            Picasso.get()
                    .load(R.drawable.ic_arrow_forward)
                    .into(chatSendMessage);
            chatSendMessage.setOnClickListener(sendAudioClickListener);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_brown_arrow_forward)
                    .into(chatSendMessage);
            chatSendMessage.setOnClickListener(sendTextClickListener);
        }
    }

    private View.OnClickListener sendAudioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener sendTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String textMessage = chatMessageEditText.getText().toString();

            //DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference("Chat").child();

            Map messageContent = new HashMap();
            //messageContent.put("")
        }
    };

    private void checkChatRequest() {
        if (chatType.equals("Individual")) {
            final DatabaseReference chatRequestRef = FirebaseDatabase.getInstance().getReference("Chat_request");
            chatRequestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        if (s.child(currentUser.getUid()).child(chatUserID).exists()) {
                            chatVerification.setVisibility(View.VISIBLE);
                            includeChatInput.setVisibility(View.GONE);
                        } else {
                            chatVerification.setVisibility(View.GONE);
                            includeChatInput.setVisibility(View.VISIBLE);
                        }
                    }
                    if (dataSnapshot.exists()) {
                        chatVerification.setVisibility(View.VISIBLE);
                        includeChatInput.setVisibility(View.GONE);
                    } else {
                        chatVerification.setVisibility(View.GONE);
                        includeChatInput.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void deleteFollower(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(currentUser.getUid()).child("followers").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(currentUser.getUid()).child("followers").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("following").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("following").child(currentUser.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFollowing(final String userId) {
        final DatabaseReference followReference = FirebaseDatabase.getInstance().getReference("Follow");

        followReference.child(currentUser.getUid()).child("following").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(currentUser.getUid()).child("following").child(userId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followReference.child(userId).child("followers").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followReference.child(userId).child("followers").child(currentUser.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blockUser(String userId) {
        FirebaseDatabase.getInstance().getReference("Block").child(currentUser.getUid()).child(userId).setValue(true);
    }

    private void checkChatRequestAnonity(final CallBacks callBacks) {
        FirebaseDatabase.getInstance().getReference("Chat_request")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    ChatRequest chatRequest = s.getValue(ChatRequest.class);
                    if (chatRequest.getRequestedID().equals(currentUser.getUid())
                            && chatRequest.getRequestingID().equals(chatUserID)) {
                        String ID = chatRequest.getChatID();
                        boolean anonity = chatRequest.getAnonity();
                        callBacks.callback(anonity, ID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createContactData() {
        final DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference("Chat").child("Contact");
        final String chatId = contactReference.push().getKey();
        checkChatRequestAnonity(new CallBacks<Boolean, String>() {
            @Override
            public void callback(Boolean data, String ID) {
                if (data) {
                    // if true, anonymous || if false, non-anonymous
                    contactReference.child(chatId).child(currentUser.getUid()).setValue(true);


                    //contactReference.child(contactId)
                } else {

                }
            }
        });
    }

    private void saveMessageToDatabase() {
        String messageContent = chatMessageEditText.getText().toString();
        if (TextUtils.isEmpty(messageContent.trim())) {

        }
    }
}
