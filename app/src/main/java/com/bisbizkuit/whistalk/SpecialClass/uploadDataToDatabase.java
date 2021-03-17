package com.bisbizkuit.whistalk.SpecialClass;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class uploadDataToDatabase {

    HashMap<String, Object> hashMap;

    public uploadDataToDatabase(HashMap<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    public void uploadNotifData(String userId) {
        DatabaseReference notifDatabase = FirebaseDatabase.getInstance().getReference("Notifications").child(userId).push();
        notifDatabase.setValue(hashMap);
    }
}
