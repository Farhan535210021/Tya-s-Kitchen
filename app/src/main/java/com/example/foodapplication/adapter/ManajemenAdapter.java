package com.example.foodapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapplication.ManajemenActivity;
import com.example.foodapplication.R;
import com.example.foodapplication.model.GeneralResponse;
import com.example.foodapplication.model.ProdukManajemen;
import com.example.foodapplication.model.UploadResponse;
import com.example.foodapplication.network.ApiService;
import com.example.foodapplication.connection.ApiServer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManajemenAdapter extends RecyclerView.Adapter<ManajemenAdapter.ManajemenViewHolder> {
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<ProdukManajemen> produkList;
    private Context context;
    private Uri selectedImageUri;  // To store the selected image URI

    public ManajemenAdapter(Context context, List<ProdukManajemen> produkList) {
        this.context = context;
        this.produkList = produkList;
    }

    @NonNull
    @Override
    public ManajemenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_manajemen, parent, false);
        return new ManajemenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManajemenViewHolder holder, int position) {
        ProdukManajemen produk = produkList.get(position);
        holder.tvProductName.setText(produk.getName());
        DecimalFormat df = new DecimalFormat("#");
        holder.tvProductPrice.setText(formatCurrency(produk.getPrice()));
        holder.tvCategory.setText(getCategoryName(produk.getCategoryId()));
        holder.tvDescription.setText(produk.getDescription());

        // Load image from URL using Glide
        Glide.with(context).load(produk.getImageUrl()).into(holder.imgProduct);

        // Handle Edit Button Click
        holder.btnEditProduct.setOnClickListener(v -> showEditDialog(produk, true));

        // Handle Delete Button Click (ikon X)
        holder.btnDeleteProduct.setOnClickListener(v -> {
            // Panggil API untuk menghapus produk berdasarkan ID
            deleteProduct(produk.getId(), position);
        });
    }

    private void deleteProduct(int productId, int position) {
        ApiService apiService = ApiServer.getApiService();
        apiService.deleteProduk(productId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    // Hapus item dari list dan notify adapter
                    produkList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Function to set the selected image URI from the activity
    public void setSelectedImageUri(Uri imageUri) {
        this.selectedImageUri = imageUri;
    }

    // Convert category_id to category name
    private String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 1:
                return "Paket Makanan";
            case 2:
                return "Makanan Berat";
            case 3:
                return "Jajanan Kue";
            case 4:
                return "Desserts";
            case 5:
                return "Special Dish";
            default:
                return "Kategori Tidak Diketahui";
        }
    }

    // Modifikasi showEditDialog agar bisa digunakan untuk Edit dan Tambah Produk
    public void showEditDialog(ProdukManajemen produk, boolean isEdit) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        // Get references to dialog views
        TextView title = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etProductName = dialogView.findViewById(R.id.etProductName);
        TextInputEditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        TextInputEditText etProductDescription = dialogView.findViewById(R.id.etProductDescription);
        RadioGroup radioGroupCategory = dialogView.findViewById(R.id.radioGroupCategory);
        MaterialButton btnUploadImage = dialogView.findViewById(R.id.btnUploadImage);
        MaterialButton btnSaveProduct = dialogView.findViewById(R.id.btnSaveProduct);
        ImageView ivProductImagePreview = dialogView.findViewById(R.id.ivProductImagePreview); // ImageView for preview

        // Set judul dialog berdasarkan apakah ini edit atau tambah produk
        title.setText(isEdit ? "EDIT PRODUK" : "TAMBAH PRODUK");

        // Jika ini edit produk, pre-fill data produk
        if (isEdit && produk != null) {
            etProductName.setText(produk.getName());
            DecimalFormat df = new DecimalFormat("#");
            etProductPrice.setText(df.format(produk.getPrice()));
            etProductDescription.setText(produk.getDescription());

            // Pre-select category based on product's category_id
            if (produk.getCategoryId() == 1) {
                radioGroupCategory.check(R.id.rbCategoryPaketMakanan);
            } else if (produk.getCategoryId() == 2) {
                radioGroupCategory.check(R.id.rbCategoryMakananBerat);
            } else if (produk.getCategoryId() == 3) {
                radioGroupCategory.check(R.id.rbCategoryJajananKue);
            } else if (produk.getCategoryId() == 4) {
                radioGroupCategory.check(R.id.rbCategoryDesserts);
            } else if (produk.getCategoryId() == 5) {
                radioGroupCategory.check(R.id.rbCategorySpecialDish);
            }

            // Load existing image into preview if available
            Glide.with(context).load(produk.getImageUrl()).into(ivProductImagePreview);
            ivProductImagePreview.setVisibility(View.VISIBLE);
        } else {
            // Jika ini tambah produk, kosongkan input
            etProductName.setText("");
            etProductPrice.setText("");
            etProductDescription.setText("");
            radioGroupCategory.clearCheck(); // Tidak ada kategori yang dipilih
            ivProductImagePreview.setVisibility(View.GONE);
        }

        btnUploadImage.setOnClickListener(v -> {
            ((ManajemenActivity) context).openImageSelector();

            // Set listener untuk menampilkan preview gambar yang baru dipilih
            ((ManajemenActivity) context).setOnImageSelectedListener(uri -> {
                selectedImageUri = uri;
                ivProductImagePreview.setVisibility(View.VISIBLE);
                Glide.with(context).load(selectedImageUri).into(ivProductImagePreview); // Preview the selected image
            });
        });

        btnSaveProduct.setOnClickListener(v -> {
            String newName = etProductName.getText().toString();
            String newPrice = etProductPrice.getText().toString();
            String newDescription = etProductDescription.getText().toString();

            // Get the selected category id from RadioGroup
            int selectedCategoryId = -1;
            int checkedRadioButtonId = radioGroupCategory.getCheckedRadioButtonId();

            if (checkedRadioButtonId == R.id.rbCategoryPaketMakanan) {
                selectedCategoryId = 1;
            } else if (checkedRadioButtonId == R.id.rbCategoryMakananBerat) {
                selectedCategoryId = 2;
            } else if (checkedRadioButtonId == R.id.rbCategoryJajananKue) {
                selectedCategoryId = 3;
            } else if (checkedRadioButtonId == R.id.rbCategoryDesserts) {
                selectedCategoryId = 4;
            } else if (checkedRadioButtonId == R.id.rbCategorySpecialDish) {
                selectedCategoryId = 5;
            }

            if (isEdit && produk != null) {
                // Update produk jika mode edit
                if (selectedImageUri != null) {
                    uploadImageToServer(selectedImageUri, newName, newPrice, newDescription, selectedCategoryId, produk.getId(), dialog);
                } else {
                    updateProduct(produk.getId(), newName, newPrice, newDescription, produk.getImageUrl(), selectedCategoryId, dialog);
                }
            } else {
                // Tambah produk baru jika mode tambah
                if (selectedImageUri != null) {
                    uploadImageToServerForNewProduct(selectedImageUri, newName, newPrice, newDescription, selectedCategoryId, dialog);
                } else {
                    addNewProduct(newName, newPrice, newDescription, null, selectedCategoryId, dialog);
                }
            }
        });

        dialog.show();
    }


    // Fungsi untuk menambah produk baru ke server
    private void addNewProduct(String name, String price, String description, String imageUrl, int categoryId, AlertDialog dialog) {
        ApiService apiService = ApiServer.getApiService();

        // Prepare product data to send
        JsonObject productData = new JsonObject();
        productData.addProperty("name", name);
        productData.addProperty("price", price);
        productData.addProperty("description", description);
        productData.addProperty("image_url", imageUrl);
        productData.addProperty("category_id", categoryId);

        apiService.addProduk(productData).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    ((ManajemenActivity) context).refreshProductList();  // Refresh produk setelah tambah
                } else {
                    Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk upload gambar saat tambah produk baru
    private void uploadImageToServerForNewProduct(Uri imageUri, String name, String price, String description, int categoryId, AlertDialog dialog) {
        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ApiService apiService = ApiServer.getApiService();
        apiService.uploadImage(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getImageUrl();
                    // Call addNewProduct with imageUrl
                    addNewProduct(name, price, description, imageUrl, categoryId, dialog);
                } else {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Upload image to server
    private void uploadImageToServer(Uri imageUri, String newName, String newPrice, String newDescription, int categoryId, int productId, AlertDialog dialog) {
        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ApiService apiService = ApiServer.getApiService();
        apiService.uploadImage(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getImageUrl();
                    // Pass correct parameters to updateProduct
                    updateProduct(productId, newName, newPrice, newDescription, imageUrl, categoryId, dialog);
                } else {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct(int productId, String newName, String newPrice, String newDescription, String imageUrl, int categoryId, AlertDialog dialog) {
        ApiService apiService = ApiServer.getApiService();

        // Prepare product data to send
        JsonObject productData = new JsonObject();
        productData.addProperty("name", newName);
        productData.addProperty("price", newPrice);
        productData.addProperty("description", newDescription);
        productData.addProperty("image_url", imageUrl);
        productData.addProperty("category_id", categoryId);  // Pass category ID

        // Make the PUT request to update the product
        apiService.updateProduk(productId, productData).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    // Notify success and refresh product list
                    dialog.dismiss();
                    ((ManajemenActivity) context).refreshProductList();
                } else {
                    Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper function to get the real path from URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private String formatCurrency(double value) {
        return String.format("Rp %,.0f", value);
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ManajemenViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnDeleteProduct;
        TextView tvProductName, tvProductPrice, tvCategory, tvDescription;
        MaterialButton btnEditProduct;

        public ManajemenViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvCategory = itemView.findViewById(R.id.tv_categorie);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnEditProduct = itemView.findViewById(R.id.btn_edit_product);
            btnDeleteProduct = itemView.findViewById(R.id.btn_delete_product);
        }
    }
}

