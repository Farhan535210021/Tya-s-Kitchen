package com.example.foodapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodapplication.OrderActivity;
import com.example.foodapplication.R;
import com.example.foodapplication.model.MostViewed;

import java.text.DecimalFormat;
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    private Context context;
    private List<MostViewed> mostViewedList;
    private OnItemClickListener listener;

    // Constructor with an added listener
    public TrendingAdapter(Context context, List<MostViewed> mostViewedList, OnItemClickListener listener) {
        this.context = context;
        this.mostViewedList = mostViewedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_mostviewed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MostViewed mostViewed = mostViewedList.get(position);

        holder.tvProductName.setText(mostViewed.getName());
        holder.tvProductPrice.setText(formatCurrency(mostViewed.getPrice()));
        holder.tvDescription.setText(mostViewed.getDescription());
        holder.tvProductViews.setText(mostViewed.getViews() + " views");

        // Format averageRating to 1 decimal place without trailing zeroes
        double averageRating = mostViewed.getAverageRating();
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedRating = df.format(averageRating);
        holder.tvProductRating.setText(formattedRating);

        Glide.with(context)
                .load(mostViewed.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imgProduct);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(mostViewed));
        }

        // Set up click listener for CardView to navigate to OrderActivity
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("CATEGORY_ID", mostViewed.getCategoryId());
            intent.putExtra("PRODUCT_ID", mostViewed.getId());
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return mostViewedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgProduct, imgStar;
        public TextView tvProductName, tvProductPrice, tvProductViews, tvDescription, tvProductRating;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductViews = itemView.findViewById(R.id.tvProductViews);
            tvProductRating = itemView.findViewById(R.id.tvProductRating); // Add tvProductRating
            imgStar = itemView.findViewById(R.id.imgStar); // Add imgStar for the rating icon
            cardView = itemView.findViewById(R.id.listTrending);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }

    // Helper method to format currency without decimals
    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    // Interface listener to handle item click events
    public interface OnItemClickListener {
        void onItemClick(MostViewed mostViewed);
    }
}
