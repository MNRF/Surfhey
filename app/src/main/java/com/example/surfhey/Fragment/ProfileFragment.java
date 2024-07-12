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

import com.example.surfhey.FirestoreConfig;
import com.example.surfhey.FirestoreService;
import com.example.surfhey.LoginActivity;
import com.example.surfhey.OptionActivity;
import com.example.surfhey.R;
import com.example.surfhey.adapter.surfGridAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {
    private static final String TAG = "FirestoreService";
    private FirestoreService FSdb;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recycleGrid;
    private ArrayList<modelSurf> surfList;
    private surfGridAdapter surfGridAdapter;
    private TextView userName;
    private TextView userID;
    private TextView surveyCreated;

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
        try {
            FirestoreConfig.initialize(getActivity());
            FSdb = new FirestoreService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firestore", e);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.tvProfile);
        FSdb = new FirestoreService();

        fetchUsername();

        userID = view.findViewById(R.id.textView15);
        userID.setText(LoginActivity.userID);

        surveyCreated = view.findViewById(R.id.textView16);
        fetchSurveyCreated();

        recycleGrid = view.findViewById(R.id.recycleGrid);
        recycleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        surfList = new ArrayList<>();
        surfGridAdapter = new surfGridAdapter(surfList, getContext());
        recycleGrid.setAdapter(surfGridAdapter);

        fetchDataAndSetupRecyclerView();

        ImageView optionImageView = view.findViewById(R.id.Option_btn);
        optionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OptionActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void fetchUsername() {
        new Thread(() -> {
            try {
                DocumentSnapshot document = FSdb.getUsernamebyUserID(LoginActivity.userID).get();
                if (document.exists()) {
                    requireActivity().runOnUiThread(() -> userName.setText(document.getString("username")));
                } else {
                    Log.w(TAG, "No such document");
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG, "Error getting documents.", e);
            }
        }).start();
    }

    private void fetchSurveyCreated() {
        new Thread(() -> {
            try {
                String surveyCount = FSdb.getSurveyCreatedbyUserID(LoginActivity.userID);
                requireActivity().runOnUiThread(() -> surveyCreated.setText(surveyCount));
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error getting documents.", e);
            }
        }).start();
    }

    private void fetchDataAndSetupRecyclerView() {
        new Thread(() -> {
            try {
                FSdb.getCurrentUserPostAndUpdateItems(LoginActivity.userID);
                updateRecyclerViewData();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Failed to fetch data from FirestoreService", e);
            }
        }).start();
    }

    private void updateRecyclerViewData() {
        new Thread(() -> {
            surfList.clear();
            for (int i = 0; i < itemSurf.itemImageURL.length; i++) {
                modelSurf modelSurf = new modelSurf(
                        itemSurf.itemAuthorname[i],
                        itemSurf.itemTitle[i],
                        itemSurf.itemDate[i],
                        itemSurf.itemImageURL[i],
                        itemSurf.itemDetail[i],
                        itemSurf.itemLikes[i],
                        itemSurf.itemPostID[i]
                );
                surfList.add(modelSurf);
            }

            requireActivity().runOnUiThread(() -> {
                if (surfList.isEmpty()) {
                    Log.d(TAG, "RecyclerView Data: No data available");
                } else {
                    Log.d(TAG, "RecyclerView Data: " + surfList.size() + " items loaded");
                }
                surfGridAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsername();
    }
}