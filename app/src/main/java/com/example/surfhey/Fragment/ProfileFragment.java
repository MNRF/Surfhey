package com.example.surfhey.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.Firestore;
import com.example.surfhey.LoginActivity;
import com.example.surfhey.OptionActivity;
import com.example.surfhey.R;
import com.example.surfhey.adapter.surfGridAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private static final String TAG = "Firestore";
    private Firestore FSdb;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recycleGrid;
    private ArrayList<modelSurf> surfList;
    private TextView userName; // Define userName here

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.tvProfile); // Initialize userName here
        FSdb = new Firestore();
        FSdb.getUsernamebyUserID(LoginActivity.userID).addOnCompleteListener
                (new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            userName.setText(task.getResult());
                        } else {
                            Log.w(TAG, "Error retrieving username", task.getException());
                        }
                    }
                });

        recycleGrid = view.findViewById(R.id.recycleGrid);
        fetchDataAndSetupRecyclerView();

        // Find the ImageView and set up the click listener
        ImageView optionImageView = view.findViewById(R.id.Option_btn);
        optionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start OptionActivity
                Intent intent = new Intent(getActivity(), OptionActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void fetchDataAndSetupRecyclerView() {
        FSdb.getCurrentUserPostAndUpdateItems(LoginActivity.userID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setupRecyclerView();
                } else {
                    Log.e("HomeFragment", "Failed to fetch data from Firestore", task.getException());
                }
            }
        });
    }

    private void setupRecyclerView() {
        // Set up the RecyclerView
        recycleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        surfList = new ArrayList<>();
        for (int i = 0; i < itemSurf.itemImageURL.length; i++) {
            modelSurf modelSurf = new modelSurf(
                    itemSurf.itemAuthorname[i],
                    itemSurf.itemTitle[i],
                    itemSurf.itemDate[i],
                    itemSurf.itemImageURL[i],
                    itemSurf.itemDetail[i],
                    itemSurf.itemLikes[i]
            );
            surfList.add(modelSurf);
        }
        surfGridAdapter surfGridAdapter = new surfGridAdapter(surfList, getContext());
        recycleGrid.setAdapter(surfGridAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data here when fragment resumes
        FSdb.getUsernamebyUserID(LoginActivity.userID).addOnCompleteListener
                (new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            userName.setText(task.getResult());
                        } else {
                            Log.w(TAG, "Error retrieving username", task.getException());
                        }
                    }
                });
    }
}