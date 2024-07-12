package com.example.surfhey.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.surfhey.FirestoreConfig;
import com.example.surfhey.FirestoreService;
import com.example.surfhey.R;
import com.example.surfhey.adapter.surfListAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recycleViewSurf;
    private Button btnCategory;
    private ArrayList<modelSurf> items;
    private String mParam1;
    private String mParam2;
    FirestoreService FSdb;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recycleViewSurf = view.findViewById(R.id.rv_post);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        btnCategory = view.findViewById(R.id.btn_category);
        btnCategory.setOnClickListener(v -> bottomSheetDialog.show());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_category, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Fetch FirestoreService data and then setup RecyclerView
        fetchDataAndSetupRecyclerView();

        return view;
    }

    private void fetchDataAndSetupRecyclerView() {
        try {
            FSdb.getPostAndUpdateItems();
            setupRecyclerView();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("HomeFragment", "Failed to fetch data from FirestoreService", e);
        }
    }

    private void setupRecyclerView() {
        if (recycleViewSurf == null) {
            Log.e("HomeFragment", "RecyclerView not found");
            return;
        }
        recycleViewSurf.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        items = new ArrayList<>();

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
            items.add(modelSurf);
        }
        if (items.isEmpty()) {
            Log.e("HomeFragment", "surfList is empty");
        }

        surfListAdapter surfListAdapter = new surfListAdapter(items, getContext());
        recycleViewSurf.setAdapter(surfListAdapter);
    }
}