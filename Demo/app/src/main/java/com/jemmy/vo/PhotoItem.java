package com.jemmy.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhotoItem implements Serializable{

    @SerializedName("largeImageURL")
    String largeUrl;
    @SerializedName("webformatURL")
    String previewUrl;
    @SerializedName("id")
    Integer photoid;

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Integer getPhotoid() {
        return photoid;
    }

    public void setPhotoid(Integer photoid) {
        this.photoid = photoid;
    }
}
