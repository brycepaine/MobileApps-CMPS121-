package com.paine.nativeApp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 2/29/2016.
 */
public class LoginResponse {
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("profile_pic")
    @Expose
    public String profile_pic;
}
