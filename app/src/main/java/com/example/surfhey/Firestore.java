package com.example.surfhey;

import android.icu.util.Calendar;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Firestore {

    private static final String TAG = "Firestore";
    private FirebaseFirestore db;

    public Firestore() {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    /*public void createAccount() {
        Map<String, Object> account = new HashMap<>();
        account.put("first", "Ada");
        account.put("last", "Lovelace");
        account.put("born", 1815);

        db.collection("users")
                .add(account)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }*/

    public void createAccount(String username, String userid, String userpassword) {
        Map<String, Object> account = new HashMap<>();
        account.put("username", username);
        account.put("userpassword", userpassword);
        account.put("datecreated", Calendar.getInstance().getTime());
        account.put("datemodified", Calendar.getInstance().getTime());

        db.collection("logred").document(userid)
                .add("account")
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void readAccount(String username, String password) {
        db.collection("logcred")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}