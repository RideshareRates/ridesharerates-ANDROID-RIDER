package com.rideshare.app.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("stripe_pubish_key")
    @Expose
    private String stripe_pubish_key;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStripe_pubish_key() {
        return stripe_pubish_key;
    }

    public void setStripe_pubish_key(String stripe_pubish_key) {
        this.stripe_pubish_key = stripe_pubish_key;
    }
}
