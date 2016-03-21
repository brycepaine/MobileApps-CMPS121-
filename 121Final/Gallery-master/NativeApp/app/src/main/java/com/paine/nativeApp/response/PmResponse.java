package com.paine.nativeApp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryce on 3/14/2016.
 */
public class PmResponse {
    @SerializedName("pm_result")
    @Expose
    private List<PmResult> pmResult = new ArrayList<PmResult>();
    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The pmResult
     */
    public List<PmResult> getPmResult() {
        return pmResult;
    }

    /**
     *
     * @param pmResult
     * The pm_result
     */
    public void setPmResult(List<PmResult> pmResult) {
        this.pmResult = pmResult;
    }

    /**
     *
     * @return
     * The response
     */
    public String getResponse() {
        return response;
    }

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(String response) {
        this.response = response;
    }

}


