package com.example.surfhey.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.OptionActivity;
import com.example.surfhey.R;
import com.example.surfhey.adapter.surfGridAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recycleGrid;
    private ArrayList<modelSurf> surfList;
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
        recycleGrid = view.findViewById(R.id.recycleGrid);
        setupRecyclerView();
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

    private void setupRecyclerView() {
        // Set up the RecyclerView
        recycleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        surfList = new ArrayList<>();
        for (int i = 0; i< itemSurf.posterItem.length; i++){
            modelSurf modelSurf = new modelSurf(
                    itemSurf.judulItem[i],
                    itemSurf.dateItem[i],
                    itemSurf.posterItem[i],
                    itemSurf.detailItem[i]
            );
            surfList.add(modelSurf);
        }
        surfGridAdapter surfGridAdapter = new surfGridAdapter(surfList, getContext());
        recycleGrid.setAdapter(surfGridAdapter);
    }
}
