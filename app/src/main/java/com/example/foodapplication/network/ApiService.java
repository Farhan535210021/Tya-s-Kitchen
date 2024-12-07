package com.example.foodapplication.network;

import com.example.foodapplication.model.MostViewed;
import com.example.foodapplication.model.ProductData;
import com.example.foodapplication.model.ProductRating;
import com.example.foodapplication.model.Produk;
import com.example.foodapplication.model.CartItem;
import com.example.foodapplication.model.CartRequest;
import com.example.foodapplication.model.GeneralResponse;
import com.example.foodapplication.model.LoginRequest;
import com.example.foodapplication.model.LoginResponse;
import com.example.foodapplication.model.OrderHistory;
import com.example.foodapplication.model.ProdukManajemen;
import com.example.foodapplication.model.SalesReport;
import com.example.foodapplication.model.Transaksi;
import com.example.foodapplication.model.UploadResponse;
import com.example.foodapplication.model.User;
import com.google.gson.JsonObject;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    // Register new user
    @POST("/register")
    Call<Void> registerUser(@Body User user);

    // Login user
    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/produk")
    Call<List<Produk>> getProdukByCategory(
            @Query("category_id") int categoryId,
            @Query("user_id") int userId
    );


    // Add product to cart
    @POST("/cart/add")  // This should match your Flask route exactly
    Call<GeneralResponse> addToCart(@Body CartRequest cartRequest);

    // Remove a specific item from the cart
    @DELETE("/cart/{user_id}/{produk_id}")  // Ensure this path matches your Flask DELETE route
    Call<GeneralResponse> removeFromCart(@Path("user_id") int userId, @Path("produk_id") int produkId);

    // Get cart items for a specific user
    @GET("/get_cart_items")
    Call<List<CartItem>> getCartItems(@Query("user_id") int userId);



    @POST("/get_recommendations")
    Call<JsonObject> getRecommendations(@Body JsonObject cartItems);

    // Tambahkan di ApiService
    @POST("checkout")
    Call<JsonObject> checkout(@Body JsonObject checkoutBody);  // Ubah ini agar menerima JsonObject

    // Tambahkan endpoint untuk mengambil riwayat pesanan
    @GET("/api/order/history")
    Call<List<OrderHistory>> getOrderHistory(@Query("user_id") int userId, @Query("role_id") int roleId);


    @POST("/api/order/update_status")
    Call<GeneralResponse> updateOrderStatus(@Body JsonObject statusUpdateRequest);

    @POST("cart/update_quantity")
    Call<JsonObject> updateCartItemQuantity(@Body JsonObject body);

    @DELETE("cart/remove")
    Call<GeneralResponse> removeFromCart(@Body JsonObject cartData);

    @GET("/api/order/rating")
    Call<List<Transaksi>> getRatingList(
            @Query("status_transaksi") String status,
            @Query("user_id") int userId,
            @Query("role_id") int roleId
    );

    @POST("/api/order/rating")
    Call<GeneralResponse> submitRating(@Body JsonObject ratingData);


    @GET("/api/produk/manajemen")
    Call<List<ProdukManajemen>> getProdukManajemen();

    @Multipart
    @POST("/api/produk/upload-image")
    Call<UploadResponse> uploadImage(@Part MultipartBody.Part file);

    @PUT("/api/produk/update/{product_id}")
    Call<GeneralResponse> updateProduk(@Path("product_id") int productId, @Body JsonObject productData);

    @POST("/api/produk")
    Call<GeneralResponse> addProduk(@Body JsonObject productData);

    @DELETE("/api/produk/delete/{product_id}")
    Call<GeneralResponse> deleteProduk(@Path("product_id") int productId);


    // Endpoint untuk mendapatkan data profil pengguna
    @GET("api/user/profile/{userId}")
    Call<User> getUserProfile(@Path("userId") String userId);

    // Endpoint untuk memperbarui profil pengguna
    @PUT("api/user/profile/{userId}")
    Call<Void> updateUserProfile(@Path("userId") String userId, @Body User user);

    @GET("/api/products/most_viewed")
    Call<List<MostViewed>> getMostViewedProducts();

    // Panggil endpoint untuk memperbarui jumlah views produk
    @POST("api/product/{product_id}/increment_views")
    Call<GeneralResponse> incrementProductViews(@Path("product_id") int productId);

    @GET("product/average_rating")
    Call<List<ProductRating>> getProductAverageRatings();

    @GET("/api/sales_report")
    Call<SalesReport> getSalesReport(
            @Query("tahun") String tahun,
            @Query("bulan") String bulan
    );

    @GET("/api/product_sales_report")
    Call<Map<String, List<ProductData>>> getGroupedProductSalesReport(
            @Query("tahun") String tahun,
            @Query("bulan") String bulan
    );

}
