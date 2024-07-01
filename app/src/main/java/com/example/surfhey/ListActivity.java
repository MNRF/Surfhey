package com.example.surfhey;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.adapter.surfListAdapter;
import com.example.surfhey.modelItem.itemSurf;
import com.example.surfhey.modelItem.modelSurf;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rv_surf;
    private ArrayList<modelSurf> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        rv_surf = findViewById(R.id.rv_post);
        if (rv_surf == null) {
            Log.e("MainActivity", "RecyclerView not found");
            return;
        }
        rv_surf.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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

        surfListAdapter surfListAdapter = new surfListAdapter(items, this);  // Pass context here
        rv_surf.setAdapter(surfListAdapter);
    }
}
