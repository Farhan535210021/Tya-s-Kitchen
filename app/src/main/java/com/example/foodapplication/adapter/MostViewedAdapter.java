package com.example.foodapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapplication.R;
import com.example.foodapplication.model.MostViewed;

import java.util.List;

public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.MostViewedViewHolder> {

    private Context context;
    private List<MostViewed> mostViewedList;

    public MostViewedAdapter(Context context, List<MostViewed> mostViewedList) {
        this.context = context;
        this.mostViewedList = mostViewedList;
    }

    @NonNull
    @Override
    public MostViewedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_mostviewed, parent, false);
        return new MostViewedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MostViewedViewHolder holder, int position) {
        MostViewed mostViewed = mostViewedList.get(position);

        holder.tvProductName.setText(mostViewed.getName());
        holder.tvDescription.setText(mostViewed.getDescription());
        holder.tvProductViews.setText("Views: " + mostViewed.getViews());
        holder.tvProductPrice.setText(formatCurrency(mostViewed.getPrice()));

        // Ambil nilai averageRating dan log untuk debugging
        double averageRating = mostViewed.getAverageRating();
        Log.d("TrendingAdapter", "Product ID: " + mostViewed.getId() + ", Name: " + mostViewed.getName() + ", Average Rating: " + averageRating);

        // Tampilkan rating, set ke "0.0" jika nilainya 0 atau kurang
        String ratingText = averageRating > 0 ? String.valueOf(averageRating) : "0.0";
        holder.tvProductRating.setText(ratingText);

        Glide.with(context)
                .load(mostViewed.getImageUrl())
                .into(holder.imgProduct);
    }



    @Override
    public int getItemCount() {
        return mostViewedList.size();
    }

    // Method to format price as currency without decimals
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    public static class MostViewedViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductPrice, tvProductViews, tvDescription, tvProductRating;
        ImageView imgProduct, imgStar;

        public MostViewedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductViews = itemView.findViewById(R.id.tvProductViews);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvProductRating = itemView.findViewById(R.id.tvProductRating); // Rating TextView
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgStar = itemView.findViewById(R.id.imgStar); // Star ImageView for rating
        }
    }
}
