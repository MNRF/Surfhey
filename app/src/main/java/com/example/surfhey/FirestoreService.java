package com.example.surfhey;

import android.icu.util.Calendar;
import android.util.Log;

import com.example.surfhey.modelItem.itemSurf;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreService {

    private static final String TAG = "FirestoreService";
    private Firestore db;

    public FirestoreService() {
        db = FirestoreOptions.getDefaultInstance().getService();
    }

    public void createAccount(String username, String userid, String userpassword) {
        Map<String, Object> account = new HashMap<>();
        account.put("username", username);
        account.put("userpassword", userpassword);
        account.put("status", "active");
        account.put("datecreated", Calendar.getInstance().getTime());
        account.put("datemodified", Calendar.getInstance().getTime());

        db.collection("logcred").document(userid).set(account);
    }

    public boolean isUsernameExist(String username) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("logcred").whereEqualTo("username", username).get();
        QuerySnapshot querySnapshot = future.get();
        return !querySnapshot.isEmpty();
    }

    public boolean isUserIDExist(String userID) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userID).get();
        DocumentSnapshot documentSnapshot = future.get();
        return documentSnapshot.exists();
    }

    public boolean isLogCredValid(String username, String userpassword) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("logcred").whereEqualTo("username", username)
                .whereEqualTo("userpassword", userpassword).whereEqualTo("status", "active").get();
        QuerySnapshot querySnapshot = future.get();
        return !querySnapshot.isEmpty();
    }

    public String getIDbyLogCred(String username, String userpassword) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("logcred").whereEqualTo("username", username)
                .whereEqualTo("userpassword", userpassword).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            return querySnapshot.getDocuments().get(0).getId();
        } else {
            throw new RuntimeException("No matching document found");
        }
    }

    public String getUsernamebyUserID(String userID) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userID).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists()) {
            return documentSnapshot.getString("username");
        } else {
            throw new RuntimeException("No matching document found");
        }
    }

    public String getUserPasswordbyUserID(String userID) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userID).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists()) {
            return documentSnapshot.getString("userpassword");
        } else {
            throw new RuntimeException("No matching document found");
        }
    }

    public int getSurveyGoalbyPostID(String postID) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("survey").whereEqualTo("postid", postID).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            if (document.exists()) {
                return document.getLong("goal").intValue();
            } else {
                throw new RuntimeException("No matching document found");
            }
        } else {
            throw new RuntimeException("No documents found for query");
        }
    }

    public Timestamp getDateEndbyPostID(String postID) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("survey").whereEqualTo("postid", postID).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            if (document.exists()) {
                return document.getTimestamp("dateend");
            } else {
                throw new RuntimeException("No matching document found");
            }
        } else {
            throw new RuntimeException("No documents found for query");
        }
    }

    public int getCurrentSurveyTotalbyPostID(String postID) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("survey").whereEqualTo("postid", postID).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot surveyDocument = querySnapshot.getDocuments().get(0);
            ApiFuture<QuerySnapshot> answerFuture = surveyDocument.getReference().collection("answer").get();
            QuerySnapshot answerSnapshot = answerFuture.get();
            int total = 0;
            for (DocumentSnapshot document : answerSnapshot.getDocuments()) {
                if (document.contains("total") && document.getLong("total") != null) {
                    total += document.getLong("total").intValue();
                } else {
                    throw new RuntimeException("Total field is missing or null in one of the answer documents");
                }
            }
            return total;
        } else {
            throw new RuntimeException("No documents found for query");
        }
    }

    public String[][] getStatementAndAnswerByPostID(String postID) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("survey").whereEqualTo("postid", postID).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            String[][] result = new String[documents.size()][];
            List<ApiFuture<QuerySnapshot>> answerFutures = new ArrayList<>();

            for (int i = 0; i < documents.size(); i++) {
                DocumentSnapshot document = documents.get(i);
                String statement = document.getString("statement");
                CollectionReference answersRef = document.getReference().collection("answer");
                int finalI = i;

                ApiFuture<QuerySnapshot> answerFuture = answersRef.get();
                answerFutures.add(answerFuture);

                QuerySnapshot answerSnapshot = answerFuture.get();
                if (!answerSnapshot.isEmpty()) {
                    result[finalI] = new String[answerSnapshot.size() + 1];
                    result[finalI][0] = statement;
                    for (int j = 0; j < answerSnapshot.size(); j++) {
                        result[finalI][j + 1] = answerSnapshot.getDocuments().get(j).getId();
                    }
                }
            }

            ApiFutures.allAsList(answerFutures).get();
            return result;
        } else {
            throw new RuntimeException("No documents found for query");
        }
    }

    public void incrementAnswerTotal(String postID, String statement, String answer) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("survey").whereEqualTo("postid", postID).whereEqualTo("statement", statement).get();
        QuerySnapshot querySnapshot = future.get();
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot surveyDoc = querySnapshot.getDocuments().get(0);
            DocumentReference answerRef = surveyDoc.getReference().collection("answer").document(answer);

            ApiFuture<DocumentSnapshot> answerFuture = answerRef.get();
            DocumentSnapshot answerSnapshot = answerFuture.get();
            WriteBatch batch = db.batch();

            if (answerSnapshot.exists()) {
                int currentTotal = answerSnapshot.getLong("total").intValue();
                batch.update(answerRef, "total", currentTotal + 1);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("total", 1);
                batch.set(answerRef, data);
            }

            String userID = LoginActivity.userID;
            DocumentReference userRef = answerRef.collection("responses").document(userID);
            batch.set(userRef, new HashMap<String, Object>());

            ApiFuture<List<WriteResult>> commitFuture = batch.commit();
            commitFuture.get();
        } else {
            throw new RuntimeException("No documents found for query");
        }
    }

    public String getSurveyCreatedbyUserID(String userID) throws ExecutionException, InterruptedException {
        ApiFuture<AggregateQuerySnapshot> future = db.collection("survey").whereEqualTo("authorid", userID)
                .count().get();
        AggregateQuerySnapshot snapshot = future.get();
        return String.valueOf(snapshot.getCount());
    }

    public void updateUsername(String userid, String oldPassword, String newUsername) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userid).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists() && documentSnapshot.getString("userpassword").equals(oldPassword)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("username", newUsername);
            updates.put("datemodified", Calendar.getInstance().getTime());
            ApiFuture<WriteResult> updateFuture = db.collection("logcred").document(userid).update(updates);
            updateFuture.get();
        } else {
            throw new RuntimeException("Invalid password");
        }
    }

    public void updatePassword(String username, String userid, String newPassword) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userid).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists() && documentSnapshot.getString("username").equals(username)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("userpassword", newPassword);
            updates.put("datemodified", Calendar.getInstance().getTime());
            ApiFuture<WriteResult> updateFuture = db.collection("logcred").document(userid).update(updates);
            updateFuture.get();
        } else {
            throw new RuntimeException("Invalid username");
        }
    }

    public void deleteAccount(String username, String userid) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("logcred").document(userid).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists() && documentSnapshot.getString("username").equals(username)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "inactive");
            updates.put("datemodified", Calendar.getInstance().getTime());
            ApiFuture<WriteResult> updateFuture = db.collection("logcred").document(userid).update(updates);
            updateFuture.get();
        } else {
            throw new RuntimeException("Invalid username");
        }
    }

    public String createPost(String authorid, String imageurl, String title, String description) throws ExecutionException, InterruptedException {
        Map<String, Object> post = new HashMap<>();
        post.put("authorid", authorid);
        post.put("imageurl", imageurl);
        post.put("title", title);
        post.put("description", description);
        post.put("likes", 0);
        post.put("poststatus", "active");
        post.put("datecreated", Calendar.getInstance().getTime());
        post.put("datemodified", Calendar.getInstance().getTime());

        ApiFuture<DocumentReference> future = db.collection("post").add(post);
        DocumentReference documentReference = future.get();
        return documentReference.getId();
    }

    public void updatePost(String imageurl, String title, String description, String postID) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("post").document(postID).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists()) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("imageurl", imageurl);
            updates.put("title", title);
            updates.put("description", description);
            updates.put("datemodified", Calendar.getInstance().getTime());

            ApiFuture<WriteResult> updateFuture = documentSnapshot.getReference().update(updates);
            updateFuture.get();
        } else {
            throw new RuntimeException("Document not found or unauthorized access");
        }
    }

    public void deletePost(String postID) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = db.collection("post").document(postID).get();
        DocumentSnapshot documentSnapshot = future.get();
        if (documentSnapshot.exists()) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("poststatus", "inactive");
            updates.put("datemodified", Calendar.getInstance().getTime());

            ApiFuture<WriteResult> updateFuture = documentSnapshot.getReference().update(updates);
            updateFuture.get();
        } else {
            throw new RuntimeException("Document not found or unauthorized access");
        }
    }

    public void getPostAndUpdateItems() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("post").orderBy("datecreated", Query.Direction.DESCENDING).get();
        QuerySnapshot qs = future.get();
        if (qs != null && !qs.isEmpty()) {
            List<ApiFuture<DocumentSnapshot>> usernameFutures = new ArrayList<>();
            List<String> authornames = new ArrayList<>();
            List<String> imageURLS = new ArrayList<>();
            List<String> dates = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> details = new ArrayList<>();
            List<String> likes = new ArrayList<>();
            List<String> postID = new ArrayList<>();

            for (DocumentSnapshot ds : qs.getDocuments()) {
                try {
                    if ("active".equals(ds.getString("poststatus"))) {
                        imageURLS.add(ds.getString("imageurl"));
                        Timestamp timestamp = ds.getTimestamp("datecreated");
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            dates.add(date.toString());
                        } else {
                            dates.add("Unknown date");
                        }
                        titles.add(ds.getString("title"));
                        details.add(ds.getString("description"));
                        likes.add(ds.getLong("likes").toString());
                        postID.add(ds.getId());

                        final int index = authornames.size();
                        authornames.add("Loading...");
                        ApiFuture<DocumentSnapshot> usernameFuture = db.collection("logcred").document(ds.getString("authorid")).get();
                        usernameFutures.add(usernameFuture);
                        MoreExecutors.directExecutor().execute(() -> {
                            try {
                                DocumentSnapshot usernameDoc = usernameFuture.get();
                                if (usernameDoc.exists()) {
                                    authornames.set(index, usernameDoc.getString("username"));
                                } else {
                                    authornames.set(index, "Unknown Author");
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                authornames.set(index, "Unknown Author");
                                Log.w(TAG, "Error Retrieving User Name", e);
                            }
                        });
                    } else {
                        throw new RuntimeException("No matching document found");
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            ApiFutures.allAsList(usernameFutures).get();
            itemSurf.updateData(
                    authornames.toArray(new String[0]),
                    imageURLS.toArray(new String[0]),
                    dates.toArray(new String[0]),
                    titles.toArray(new String[0]),
                    details.toArray(new String[0]),
                    likes.toArray(new String[0]),
                    postID.toArray(new String[0])
            );
        } else {
            throw new RuntimeException("No matching document found");
        }
    }

    public void getCurrentUserPostAndUpdateItems(String userID) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("post").orderBy("datecreated", Query.Direction.DESCENDING).get();
        QuerySnapshot qs = future.get();
        if (qs != null && !qs.isEmpty()) {
            List<ApiFuture<DocumentSnapshot>> usernameFutures = new ArrayList<>();
            List<String> authornames = new ArrayList<>();
            List<String> imageURLS = new ArrayList<>();
            List<String> dates = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> details = new ArrayList<>();
            List<String> likes = new ArrayList<>();
            List<String> postID = new ArrayList<>();

            for (DocumentSnapshot ds : qs.getDocuments()) {
                try {
                    if ("active".equals(ds.getString("poststatus")) && userID.equals(ds.getString("authorid"))) {
                        imageURLS.add(ds.getString("imageurl"));
                        Timestamp timestamp = ds.getTimestamp("datecreated");
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            dates.add(date.toString());
                        } else {
                            dates.add("Unknown date");
                        }
                        titles.add(ds.getString("title"));
                        details.add(ds.getString("description"));
                        likes.add(ds.getLong("likes").toString());
                        postID.add(ds.getId());

                        final int index = authornames.size();
                        authornames.add("Loading...");
                        ApiFuture<DocumentSnapshot> usernameFuture = db.collection("logcred").document(ds.getString("authorid")).get();
                        usernameFutures.add(usernameFuture);
                        MoreExecutors.directExecutor().execute(() -> {
                            try {
                                DocumentSnapshot usernameDoc = usernameFuture.get();
                                if (usernameDoc.exists()) {
                                    authornames.set(index, usernameDoc.getString("username"));
                                } else {
                                    authornames.set(index, "Unknown Author");
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                authornames.set(index, "Unknown Author");
                                Log.w(TAG, "Error Retrieving User Name", e);
                            }
                        });
                    } else {
                        throw new RuntimeException("No matching document found");
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            ApiFutures.allAsList(usernameFutures).get();
            itemSurf.updateData(
                    authornames.toArray(new String[0]),
                    imageURLS.toArray(new String[0]),
                    dates.toArray(new String[0]),
                    titles.toArray(new String[0]),
                    details.toArray(new String[0]),
                    likes.toArray(new String[0]),
                    postID.toArray(new String[0])
            );
        } else {
            throw new RuntimeException("No matching document found");
        }
    }

    public void createSurvey(String authorid, Timestamp dateend, long goal, String postID, String statement, List<String> choices) {
        Map<String, Object> survey = new HashMap<>();
        survey.put("authorid", authorid);
        survey.put("dateend", dateend);
        survey.put("goal", goal);
        survey.put("postid", postID);
        survey.put("statement", statement);
        survey.put("datecreated", Calendar.getInstance().getTime());
        survey.put("datemodified", Calendar.getInstance().getTime());

        DocumentReference surveyRef = db.collection("survey").document();
        surveyRef.set(survey);

        for (String choice : choices) {
            Map<String, Object> choiceMap = new HashMap<>();
            choiceMap.put("total", 0);
            surveyRef.collection("answer").document(choice).set(choiceMap);
        }
    }

    public void updateSurvey(String authorid, Timestamp dateend, long goal, String surveyID, String statement, List<String> choices) throws ExecutionException, InterruptedException {
        DocumentReference surveyRef = db.collection("survey").document(surveyID);

        surveyRef.update("authorid", authorid,
                "dateend", dateend,
                "goal", goal,
                "statement", statement,
                "datemodified", Calendar.getInstance().getTime()).get();

        ApiFuture<QuerySnapshot> future = surveyRef.collection("answer").get();
        QuerySnapshot querySnapshot = future.get();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            document.getReference().delete();
        }

        for (String choice : choices) {
            Map<String, Object> choiceMap = new HashMap<>();
            surveyRef.collection("answer").document(choice).set(choiceMap);
        }
    }
}