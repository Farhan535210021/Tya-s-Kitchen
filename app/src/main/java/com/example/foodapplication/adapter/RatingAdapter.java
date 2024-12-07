package com.example.foodapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapplication.R;
import com.example.foodapplication.RatingActivity;
import com.example.foodapplication.connection.ApiServer;
import com.example.foodapplication.model.GeneralResponse;
import com.example.foodapplication.model.Produk;
import com.example.foodapplication.model.Transaksi;
import com.example.foodapplication.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private List<Transaksi> transaksiList;
    private Context context;

    public RatingAdapter(Context context, List<Transaksi> transaksiList) {
        this.context = context;
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Transaksi transaksi = transaksiList.get(position);

        // Set nilai rating rata-rata jika ada
        if (transaksi.getAverageRating() != null && transaksi.getAverageRating() > 0) {
            double averageRating = transaksi.getAverageRating();
            String formattedRating;

            // Format rating untuk menghapus .0 jika bilangan bulat
            if (averageRating == Math.floor(averageRating)) {
                formattedRating = String.valueOf((int) averageRating); // Konversi ke integer jika tidak ada desimal
            } else {
                DecimalFormat df = new DecimalFormat("#.#"); // Satu desimal
                formattedRating = df.format(averageRating);
            }

            holder.tvRating.setText(formattedRating + "/5");
        } else {
            holder.tvRating.setText("/5");
        }

        // Set informasi lainnya
        holder.tvName.setText(transaksi.getUserName());
        holder.tvPrice.setText(formatCurrency(transaksi.getTotalHarga()));
        holder.tvJml.setText("Jumlah: " + transaksi.getTotalQty());
        holder.tvDate.setText("Tanggal: " + convertToWIB(transaksi.getTanggalTransaksi()));
        holder.tvStatus.setText(transaksi.getStatusTransaksi());

        // Periksa apakah rating sudah diberikan
        boolean isReadOnly = transaksi.getAverageRating() != null && transaksi.getAverageRating() > 0;
        if (isReadOnly) {
            holder.btnRating.setEnabled(true);
            holder.btnRating.setText("Rating Diberikan");
            holder.btnRating.setOnClickListener(v -> showRatingDialog(transaksi));
        } else {
            holder.btnRating.setEnabled(true);
            holder.btnRating.setText("Berikan Rating");
            holder.btnRating.setOnClickListener(v -> showRatingDialog(transaksi));

        }
    }





    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    // Fungsi untuk konversi waktu dari GMT ke WIB
    private String convertToWIB(String gmtTime) {
        SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat wibFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        wibFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        try {
            Date date = gmtFormat.parse(gmtTime);
            return wibFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gmtTime;
    }

    private void showRatingDialog(Transaksi transaksi) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        LinearLayout productContainer = dialogView.findViewById(R.id.productContainer);

        List<Produk> productList = transaksi.getProductList();
        boolean isReadOnly = transaksi.getAverageRating() != null && transaksi.getAverageRating() > 0;

        for (Produk produk : productList) {
            Log.d("RatingAdapter", "Product ID: " + produk.getProduk_id() + ", Product Name: " + produk.getName() +
                    ", Rating: " + produk.getRatingProduk() + ", Review: " + produk.getDeskripsiRating());

            View productRatingView = inflater.inflate(R.layout.item_produk_rating, productContainer, false);

            TextView tvProductName = productRatingView.findViewById(R.id.tvProductName);
            RatingBar ratingBarProduct = productRatingView.findViewById(R.id.ratingBarProduct);
            TextInputEditText etReviewDescriptionProduct = productRatingView.findViewById(R.id.etReviewDescriptionProduct);

            tvProductName.setText(produk.getName());

            // Set nilai rating dan deskripsi jika review sudah ada
            if (produk.getRatingProduk() != null && produk.getDeskripsiRating() != null) {
                ratingBarProduct.setRating(produk.getRatingProduk());
                etReviewDescriptionProduct.setText(produk.getDeskripsiRating());
                if (isReadOnly) {
                    ratingBarProduct.setIsIndicator(true);  // Set agar tidak bisa diubah
                    etReviewDescriptionProduct.setEnabled(false);  // Set agar tidak bisa diubah
                }
            }

            productContainer.addView(productRatingView);
        }

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        if (isReadOnly) {
            btnSave.setText("Close");
            btnSave.setOnClickListener(v -> dialog.dismiss());
        } else {
            btnSave.setText("Save");
            btnSave.setOnClickListener(v -> {
                JsonObject ratingData = new JsonObject();
                ratingData.addProperty("transaction_id", transaksi.getTransactionId());

                JsonArray productsArray = new JsonArray();

                for (int i = 0; i < productContainer.getChildCount(); i++) {
                    View productRatingView = productContainer.getChildAt(i);
                    RatingBar ratingBarProduct = productRatingView.findViewById(R.id.ratingBarProduct);
                    TextInputEditText etReviewDescriptionProduct = productRatingView.findViewById(R.id.etReviewDescriptionProduct);

                    int ratingValue = (int) ratingBarProduct.getRating();
                    String reviewText = etReviewDescriptionProduct.getText().toString();

                    JsonObject productJson = new JsonObject();
                    Produk product = productList.get(i);

                    Log.d("RatingAdapter", "Sending Product ID: " + product.getProduk_id() + ", Rating: " + ratingValue + ", Description: " + reviewText);

                    productJson.addProperty("produk_id", product.getProduk_id());
                    productJson.addProperty("deskripsi_rating", reviewText);
                    productJson.addProperty("ratingproduk", ratingValue);

                    productsArray.add(productJson);
                }

                ratingData.add("products", productsArray);
                Log.d("RatingAdapter", "Final JSON to send: " + ratingData.toString());

                submitRatings(ratingData, dialog);
            });
        }

        dialog.show();
    }






    private void submitRatings(JsonObject ratingData, AlertDialog dialog) {
        ApiService apiService = ApiServer.getApiService();

        apiService.submitRating(ratingData).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Ratings submitted successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (context instanceof RatingActivity) {
                        ((RatingActivity) context).refreshRatings(); // Call a method to refresh the data
                    }
                } else {
                    try {
                        Log.e("RatingAdapter", "Failed to submit ratings. Response code: "
                                + response.code() + ", Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Failed to submit ratings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e("RatingAdapter", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvJml, tvDate, tvStatus, tvRating;
        MaterialButton btnRating;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvJml = itemView.findViewById(R.id.tvJml);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnRating = itemView.findViewById(R.id.btnRating);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
