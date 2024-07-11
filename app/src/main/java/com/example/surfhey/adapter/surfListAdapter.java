package com.example.surfhey.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.surfhey.DetailActivity;
import com.example.surfhey.NewPostActivity;
import com.example.surfhey.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfhey.modelItem.modelSurf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        holder.tvAuthorname.setText(items.get(position).getAuthorname());
        holder.tvDetail.setText(items.get(position).getDetail());
        holder.tvDateCreated.setText(getRelativeTime(items.get(position).getDate()));
        holder.tvLikes.setText(items.get(position).getLikes());

        // Retrieve poster resource identifier if posterResourceName is not null
        String imageUrl = items.get(position).getImageURL(); // Replace with your actual method to get the URL

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img1) // Placeholder image while loading
                    .error(R.drawable.img1) // Error image if the URL fails to load
                    .into(holder.ivPoster);
        } else {
            Log.e("surfListAdapter", "Null or empty image URL at position: " + position);
            holder.ivPoster.setImageResource(R.drawable.img1); // Fallback to local image if URL is null or empty
        }


        // Handle item click to start DetailActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("authorname", items.get(position).getAuthorname());
            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("date", items.get(position).getDate());
            intent.putExtra("dateAgo", getRelativeTime(items.get(position).getDate()));
            intent.putExtra("image", items.get(position).getImageURL());
            intent.putExtra("detail", items.get(position).getDetail());
            intent.putExtra("likes", items.get(position).getLikes());
            intent.putExtra("postID", items.get(position).getPostID());
            NewPostActivity.update = true;
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorname;
        TextView tvDateCreated;
        TextView tvDetail;
        TextView tvLikes;
         ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorname = itemView.findViewById(R.id.textView4);
            tvDetail = itemView.findViewById(R.id.tv_description);
            tvDateCreated = itemView.findViewById(R.id.textView5);
            tvLikes = itemView.findViewById(R.id.textView10);
            ivPoster = itemView.findViewById(R.id.iv_poster);
        }
    }

    public static String getRelativeTime(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
        try {
            Date date = sdf.parse(dateString);
            long timeDiff = new Date().getTime() - date.getTime();

            if (timeDiff < TimeUnit.MINUTES.toMillis(1)) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff);
                return seconds + (seconds == 1 ? " second ago" : " seconds ago");
            } else if (timeDiff < TimeUnit.HOURS.toMillis(1)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else if (timeDiff < TimeUnit.DAYS.toMillis(1)) {
                long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (timeDiff < TimeUnit.DAYS.toMillis(7)) {
                long days = TimeUnit.MILLISECONDS.toDays(timeDiff);
                return days + (days == 1 ? " day ago" : " days ago");
            } else if (timeDiff < TimeUnit.DAYS.toMillis(30)) {
                long weeks = TimeUnit.MILLISECONDS.toDays(timeDiff) / 7;
                return weeks + (weeks == 1 ? " week ago" : " weeks ago");
            } else if (timeDiff < TimeUnit.DAYS.toMillis(365)) {
                long months = TimeUnit.MILLISECONDS.toDays(timeDiff) / 30;
                return months + (months == 1 ? " month ago" : " months ago");
            } else {
                long years = TimeUnit.MILLISECONDS.toDays(timeDiff) / 365;
                return years + (years == 1 ? " year ago" : " years ago");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown time";
        }
    }
}
