package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionGroup {

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("tanggal_transaksi") // Disesuaikan dengan JSON
    private String transactionDate;

    @SerializedName("products")
    private List<TransactionDetail> products;

    // Getter dan Setter untuk transactionId
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Getter dan Setter untuk transactionDate
    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Getter dan Setter untuk products
    public List<TransactionDetail> getProducts() {
        return products;
    }

    public void setProducts(List<TransactionDetail> products) {
        this.products = products;
    }
}
