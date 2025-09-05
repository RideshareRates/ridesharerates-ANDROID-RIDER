package com.rideshare.app.pojo.spend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stops {
    @SerializedName("stop_id")
    @Expose
    private String stopId;
    @SerializedName("midstop_address")
    @Expose
    private String midstopAddress;

    // Constructor
    public Stops(String stopId, String midstopAddress) {
        this.stopId = stopId;
        this.midstopAddress = midstopAddress;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getMidstopAddress() {
        return midstopAddress;
    }

    public void setMidstopAddress(String midstopAddress) {
        this.midstopAddress = midstopAddress;
    }
}
