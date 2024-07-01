package com.example.surfhey.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.surfhey.DetailActivity;
import com.example.surfhey.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.modelItem.modelSurf;

import java.util.ArrayList;

public class surfListAdapter extends RecyclerView.Adapter<surfListAdapter.ViewHolder> {
    private ArrayList<modelSurf> items;
    private Context context;

    public surfListAdapter(ArrayList<modelSurf> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_surf, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.tvDetail.setText(items.get(position).getDetail());
        // Use context.getResources().getIdentifier
        String posterResourceName = items.get(position).getPoster();

        int drawableResourceId = context.getResources().getIdentifier(posterResourceName, "drawable", context.getPackageName());
        holder.ivPoster.setImageResource(drawableResourceId);
        if (drawableResourceId != 0) {
            holder.ivPoster.setImageResource(drawableResourceId);
        } else {
            Log.e("surfListAdapter", "Drawable resource not found: " + posterResourceName);
            // Optionally set a default image or placeholder
            holder.ivPoster.setImageResource(R.drawable.img1);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("date", items.get(position).getDate());
            intent.putExtra("poster", items.get(position).getPoster());
            intent.putExtra("detail", items.get(position).getDetail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDetail;
         ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetail = itemView.findViewById(R.id.tv_description);
            ivPoster = itemView.findViewById(R.id.iv_poster);
        }
    }
}
