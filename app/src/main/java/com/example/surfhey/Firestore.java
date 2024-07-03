package com.example.surfhey;

import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Firestore {

    private static final String TAG = "Firestore";
    private FirebaseFirestore db;

    public Firestore() {
        db = FirebaseFirestore.getInstance();
    }

    public void createAccount(String username, String userid, String userpassword) {
        Map<String, Object> account = new HashMap<>();
        account.put("username", username);
        account.put("userpassword", userpassword);
        account.put("userstatus", "active");
        account.put("datecreated", Calendar.getInstance().getTime());
        account.put("datemodified", Calendar.getInstance().getTime());

        db.collection("logcred").document(userid).set(account);
    }

    public Task<Boolean> isUsernameExist(String username) {
        return db.collection("logcred").whereEqualTo("username", username).get()
                .continueWith(new Continuation<QuerySnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    return !document.isEmpty();
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public Task<Boolean> isUserIDExist(String userID) {
        return db.collection("logcred").document(userID).get().continueWith
                (new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    return document.exists();
                } else {
                    throw task.getException();
                }
            }
        });
    }

    public Task<Boolean> isLogCredValid(String username, String userpassword) {
        return db.collection("logcred").whereEqualTo("username", username)
                .whereEqualTo("userpassword", userpassword)
                .get()
                .continueWith(new Continuation<QuerySnapshot, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            return !document.isEmpty();
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<String> getIDbyLogCred(String username, String userpassword) {
        return db.collection("logcred").whereEqualTo("username", username)
                .whereEqualTo("userpassword", userpassword)
                .get()
                .continueWith(new Continuation<QuerySnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                return document.getDocuments().get(0).getId();
                            } else {
                                throw new Exception("No matching document found");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }
}