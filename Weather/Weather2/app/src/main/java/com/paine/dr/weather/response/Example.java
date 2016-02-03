package com.paine.dr.weather.response;

/**
 * Created by bryce on 2/1/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Example {

    @SerializedName("response")
    @Expose
    public Response response;

}

