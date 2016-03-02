package com.grafixartist.gallery.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by luca on 28/1/2016.
 */
public class RegistrationResponse {

    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("name")
    @Expose
    public String name;

}
