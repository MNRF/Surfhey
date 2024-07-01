package com.example.surfhey.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.surfhey.R;
import com.example.surfhey.adapter.surfListAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recycleViewSurf;
    private Button btnCategory;
    private ArrayList<modelSurf> items;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recycleViewSurf = view.findViewById(R.id.rv_post);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        btnCategory = view.findViewById(R.id.btn_category);
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_category, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        setupRecyclerView();
        return view;
    }



    private void setupRecyclerView() {
        if (recycleViewSurf == null) {
            Log.e("MainActivity", "RecyclerView not found");
            return;
        }
        recycleViewSurf.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        items = new ArrayList<>();
        for (int i = 0; i < itemSurf.posterItem.length; i++) {
            modelSurf modelSurf = new modelSurf(
                    itemSurf.judulItem[i],
                    itemSurf.dateItem[i],
                    itemSurf.posterItem[i],
                    itemSurf.detailItem[i]

            );
            items.add(modelSurf);
        }
        if (items.isEmpty()) {
            Log.e("MainActivity", "surfList is empty");
        }

        surfListAdapter surfListAdapter = new surfListAdapter(items, getContext());  // Pass context here // Pass context here
        recycleViewSurf.setAdapter(surfListAdapter);
    }
}