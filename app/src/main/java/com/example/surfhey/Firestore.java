package com.example.surfhey;

import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.surfhey.modelItem.itemSurf;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .whereEqualTo("userpassword", userpassword).whereEqualTo("userstatus", "active")
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

    public Task<String> getUsernamebyUserID(String userID) {
        return db.collection("logcred").document(userID)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                return document.getString("username");
                            } else {
                                throw new Exception("No matching document found");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<String> getUserPasswordbyUserID(String userID) {
        return db.collection("logcred").document(userID)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                return document.getString("userpassword");
                            } else {
                                throw new Exception("No matching document found");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<String> getSurveyCreatedbyUserID(String userID) {
        return db.collection("survey").whereEqualTo("authorid", userID)
                .count()
                .get(AggregateSource.SERVER)
                .continueWith(new Continuation<AggregateQuerySnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<AggregateQuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            return String.valueOf(snapshot.getCount());
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<Void> updateUsername(String userid, String oldPassword, String newUsername) {
        return db.collection("logcred").document(userid)
                .get()
                .continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getString("userpassword").equals(oldPassword)) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("username", newUsername);
                                updates.put("datemodified", Calendar.getInstance().getTime());
                                return db.collection("logcred").document(userid).update(updates);
                            } else {
                                throw new Exception("Invalid password");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<Void> updatePassword(String username, String userid, String newPassword) {
        return db.collection("logcred").document(userid)
                .get()
                .continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getString("username").equals(username)) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("userpassword", newPassword);
                                updates.put("datemodified", Calendar.getInstance().getTime());
                                return db.collection("logcred").document(userid).update(updates);
                            } else {
                                throw new Exception("Invalid username");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public Task<Void> deleteAccount(String username, String userid, String newPassword) {
        return db.collection("logcred").document(userid)
                .get()
                .continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getString("username").equals(username)) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("userstatus", "inactive");
                                updates.put("datemodified", Calendar.getInstance().getTime());
                                return db.collection("logcred").document(userid).update(updates);
                            } else {
                                throw new Exception("Invalid username");
                            }
                        } else {
                            throw task.getException();
                        }
                    }
                });
    }

    public void createPost(String authorid, String imageurl, String title, String description) {
        Map<String, Object> post = new HashMap<>();
        post.put("authorid", authorid);
        post.put("imageurl", imageurl);
        post.put("title", title);
        post.put("description", description);
        post.put("likes", 0);
        post.put("poststatus", "active");
        post.put("datecreated", Calendar.getInstance().getTime());
        post.put("datemodified", Calendar.getInstance().getTime());

        db.collection("post").document().set(post);
    }

    public Task<Void> updatePost(String authorid, String imageurl, String title, String description, String datecreated) {
        return db.collection("post")
                .whereEqualTo("datecreated", datecreated)
                .whereEqualTo("authorid", authorid)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0); // Assuming there's only one document
                            // Update fields in the found document
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("imageurl", imageurl);
                            updates.put("title", title);
                            updates.put("description", description);
                            updates.put("datemodified", Calendar.getInstance().getTime());

                            // Perform the update operation
                            return document.getReference().update(updates);
                        } else {
                            throw new Exception("Document not found or unauthorized access");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> deletePost(String authorid, Timestamp datecreated) {
        return db.collection("post")
                .whereEqualTo("authorid", authorid)
                .limit(1)  // Limit the query to 1 document since you expect only one document to match
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            Timestamp docTimestamp = document.getTimestamp("datecreated");

                            // Compare timestamps considering both seconds and nanoseconds
                            assert docTimestamp != null;
                            System.out.println(docTimestamp.getSeconds() + " " + datecreated.getSeconds());
                            System.out.println(docTimestamp.getNanoseconds() + " " + datecreated.getNanoseconds());
                            if (docTimestamp.getSeconds() == datecreated.getSeconds() &&
                                    docTimestamp.getNanoseconds() == datecreated.getNanoseconds()) {
                                // Update fields in the found document
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("poststatus", "inactive");
                                updates.put("datemodified", Calendar.getInstance().getTime());

                                // Perform the update operation
                                return document.getReference().update(updates);
                            } else {
                                // Log the mismatch for debugging
                                Log.w("Firestore", "Timestamp mismatch. Firestore: " + docTimestamp.toString() +
                                        ", Provided: " + datecreated.toString());
                                throw new Exception("Document found but datecreated does not match");
                            }
                        } else {
                            // Print debug information for troubleshooting
                            if (querySnapshot == null) {
                                Log.w("Firestore", "Query Snapshot is null");
                            } else if (querySnapshot.isEmpty()) {
                                Log.w("Firestore", "Query Snapshot is empty");
                            } else {
                                Log.w("Firestore", "Unexpected condition in document retrieval");
                            }
                            throw new Exception("Document not found or unauthorized access");
                        }
                    } else {
                        throw task.getException(); // Throw the original exception if the task was not successful
                    }
                });
    }

    public Task<Void> getPostAndUpdateItems() {
        return db.collection("post").orderBy("datecreated", Query.Direction.DESCENDING)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qs = task.getResult();
                        if (qs != null && !qs.isEmpty()) {
                            List<Task<String>> usernameTasks = new ArrayList<>();
                            List<String> authornames = new ArrayList<>();
                            List<String> imageURLS = new ArrayList<>();
                            List<String> dates = new ArrayList<>();
                            List<String> titles = new ArrayList<>();
                            List<String> details = new ArrayList<>();
                            List<String> likes = new ArrayList<>();
                            List<Timestamp> timeStampCreateds = new ArrayList<>();

                            for (DocumentSnapshot ds : qs.getDocuments()) {
                                if (ds.getString("poststatus").equals("active")) {
                                    imageURLS.add(ds.getString("imageurl"));
                                    Timestamp timestamp = ds.getTimestamp("datecreated");
                                    timeStampCreateds.add(ds.getTimestamp("datecreated"));
                                    if (timestamp != null) {
                                        Date date = timestamp.toDate();
                                        dates.add(date.toString()); // Convert Date to String
                                    } else {
                                        dates.add("Unknown date");
                                    }
                                    titles.add(ds.getString("title"));
                                    details.add(ds.getString("description"));
                                    likes.add(ds.getLong("likes").toString());

                                    // Create a task to fetch the username and add it to the list at the same index
                                    final int index = authornames.size();
                                    authornames.add("Loading..."); // Placeholder to maintain index order
                                    Task<String> usernameTask = getUsernamebyUserID(ds.getString("authorid"))
                                            .addOnCompleteListener(usernameTaskResult -> {
                                                if (usernameTaskResult.isSuccessful()) {
                                                    authornames.set(index, usernameTaskResult.getResult());
                                                } else {
                                                    authornames.set(index, "Unknown Author");
                                                    Log.w(TAG, "Error Retrieving User Name", usernameTaskResult.getException());
                                                }
                                            });
                                    usernameTasks.add(usernameTask);
                                }else {
                                    throw new Exception("No matching document found");
                                }
                            }

                            // Wait for all username retrievals to complete
                            return Tasks.whenAll(usernameTasks)
                                    .continueWith(task1 -> {
                                        // Update itemSurf class with retrieved data
                                        itemSurf.updateData(
                                                authornames.toArray(new String[0]),
                                                imageURLS.toArray(new String[0]),
                                                dates.toArray(new String[0]),
                                                titles.toArray(new String[0]),
                                                details.toArray(new String[0]),
                                                likes.toArray(new String[0]),
                                                timeStampCreateds.toArray(new Timestamp[0])
                                        );
                                        return null;
                                    });
                        } else {
                            throw new Exception("No matching document found");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> getCurrentUserPostAndUpdateItems(String userID) {
        return db.collection("post").orderBy("datecreated", Query.Direction.DESCENDING)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qs = task.getResult();
                        if (qs != null && !qs.isEmpty()) {
                            List<Task<String>> usernameTasks = new ArrayList<>();
                            List<String> authornames = new ArrayList<>();
                            List<String> imageURLS = new ArrayList<>();
                            List<String> dates = new ArrayList<>();
                            List<String> titles = new ArrayList<>();
                            List<String> details = new ArrayList<>();
                            List<String> likes = new ArrayList<>();
                            List<Timestamp> timeStampCreateds = new ArrayList<>();

                            for (DocumentSnapshot ds : qs.getDocuments()) {
                                if (ds.getString("poststatus").equals("active") && ds.getString("authorid").equals(userID)) {
                                    imageURLS.add(ds.getString("imageurl"));
                                    Timestamp timestamp = ds.getTimestamp("datecreated");
                                    timeStampCreateds.add(ds.getTimestamp("datecreated"));
                                    if (timestamp != null) {
                                        Date date = timestamp.toDate();
                                        dates.add(date.toString()); // Convert Date to String
                                    } else {
                                        dates.add("Unknown date");
                                    }
                                    titles.add(ds.getString("title"));
                                    details.add(ds.getString("description"));
                                    likes.add(ds.getLong("likes").toString());

                                    // Create a task to fetch the username and add it to the list at the same index
                                    final int index = authornames.size();
                                    authornames.add("Loading..."); // Placeholder to maintain index order
                                    Task<String> usernameTask = getUsernamebyUserID(ds.getString("authorid"))
                                            .addOnCompleteListener(usernameTaskResult -> {
                                                if (usernameTaskResult.isSuccessful()) {
                                                    authornames.set(index, usernameTaskResult.getResult());
                                                } else {
                                                    authornames.set(index, "Unknown Author");
                                                    Log.w(TAG, "Error Retrieving User Name", usernameTaskResult.getException());
                                                }
                                            });
                                    usernameTasks.add(usernameTask);
                                }else {
                                    throw new Exception("No matching document found");
                                }
                            }

                            // Wait for all username retrievals to complete
                            return Tasks.whenAll(usernameTasks)
                                    .continueWith(task1 -> {
                                        // Update itemSurf class with retrieved data
                                        itemSurf.updateData(
                                                authornames.toArray(new String[0]),
                                                imageURLS.toArray(new String[0]),
                                                dates.toArray(new String[0]),
                                                titles.toArray(new String[0]),
                                                details.toArray(new String[0]),
                                                likes.toArray(new String[0]),
                                                timeStampCreateds.toArray(new Timestamp[0])
                                        );
                                        return null;
                                    });
                        } else {
                            throw new Exception("No matching document found");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }
}