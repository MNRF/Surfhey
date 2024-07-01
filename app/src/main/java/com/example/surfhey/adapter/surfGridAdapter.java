package com.example.surfhey.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.surfhey.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.DetailActivity;
import com.example.surfhey.modelItem.modelSurf;

import java.util.ArrayList;

public class surfGridAdapter extends RecyclerView.Adapter<surfGridAdapter.ViewHolder> {
    ArrayList<modelSurf> items;
    Context context;

    public surfGridAdapter(ArrayList<modelSurf> items,Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public surfGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_surf,parent,false);
        context= parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull surfGridAdapter.ViewHolder holder, int position) {

        String posterResourceName = items.get(position).getPoster();
        int drawableResourceId =context.getResources().getIdentifier(posterResourceName,"drawable",context.getPackageName());
        holder.imageGrid.setImageResource(drawableResourceId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public  class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageGrid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageGrid = itemView.findViewById(R.id.ivGrid);
        }
    }
}
