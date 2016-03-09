package com.grafixartist.gallery.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 3/9/2016.
 */
public class ImageURLResponse {
    @SerializedName("response")
    @Expose
    public String response;
}
