package com.paine.nativeApp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 3/14/2016.
 */
public class BasicResponse {


    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The response
     */
    public String getResponse() {
        return response;
    }


}
