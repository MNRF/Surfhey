package com.example.surfhey;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class CloudStorage {
    private static final String TAG = "CloudStorage";
    private static final String BUCKET_NAME = "test27052024bucket"; // replace with your bucket name
    private static final String CREDENTIALS_FILE_PATH = "test27052024-4ad8bc062cc4.json"; // replace with the path to your credentials file

    private Storage storage;
    private Context context;

    public CloudStorage(Context context) {
        this.context = context;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            InputStream credentialsStream = context.getAssets().open(CREDENTIALS_FILE_PATH);
            storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build()
                    .getService();
            Log.d(TAG, "Google Cloud Storage initialized successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize Google Cloud Storage", e);
        }
    }

    public Task<String> uploadImage(Uri fileUri) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        new Thread(() -> {
            try {
                String fileName = UUID.randomUUID().toString() + ".jpg";
                BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();

                InputStream fileStream = context.getContentResolver().openInputStream(fileUri);
                if (fileStream != null) {
                    if (storage == null) {
                        taskCompletionSource.setException(new IOException("Google Cloud Storage is not initialized."));
                        Log.e(TAG, "Google Cloud Storage is not initialized.");
                        return;
                    }

                    storage.create(blobInfo, fileStream);

                    String imageUrl = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
                    taskCompletionSource.setResult(imageUrl);
                    Log.d(TAG, "Image uploaded successfully: " + imageUrl);
                } else {
                    taskCompletionSource.setException(new IOException("Failed to open input stream from URI"));
                    Log.e(TAG, "Failed to open input stream from URI");
                }
            } catch (Exception e) {
                taskCompletionSource.setException(e);
                Log.e(TAG, "Image upload failed", e);
            }
        }).start();

        return taskCompletionSource.getTask();
    }
}