package com.example.foodapplication.model;

import com.google.android.gms.common.api.Result;

public class GeneralResponse {
    private String pesan;  // API message
    private int status;    // API status code (optional)

    public GeneralResponse(String pesan, int status) {
        this.pesan = pesan;
        this.status = status;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public int getApiStatus() {  // Ganti nama dari getStatus() ke getApiStatus()
        return status;
    }

    public void setApiStatus(int status) {  // Ganti nama setter juga jika perlu
        this.status = status;
    }
}
