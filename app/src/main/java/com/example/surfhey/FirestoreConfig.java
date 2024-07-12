// File: com/example/surfhey/config/FirestoreConfig.java

package com.example.surfhey;

import android.content.Context;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import java.io.InputStream;

public class FirestoreConfig {
    private static Firestore firestore;
    private static final String TAG = "Firestore";
    private static final String CREDENTIALS_FILE_PATH = "test27052024-4ad8bc062cc4.json";

    public static void initialize(Context context) {
        if (firestore == null) {
            try {
                InputStream credentialsStream = context.getAssets().open(CREDENTIALS_FILE_PATH);
                GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
                FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
                        .setCredentials(credentials)
                        .build();
                firestore = firestoreOptions.getService();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize Firestore", e);
            }
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            throw new IllegalStateException("Firestore has not been initialized. Call initialize() first.");
        }
        return firestore;
    }
}
