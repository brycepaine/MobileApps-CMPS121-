package com.grafixartist.gallery.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 3/1/2016.
 */
public class UploadResponse {

    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("name")
    @Expose
    public String name;
}
