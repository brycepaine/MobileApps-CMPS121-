package com.paine.nativeApp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryce on 3/13/2016.
 */
public class UserImagesResponse {
    @SerializedName("user_imags_result")
    @Expose
    private List<UserImagsResult> userImagsResult = new ArrayList<UserImagsResult>();
    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The userImagsResult
     */
    public List<UserImagsResult> getUserImagsResult() {
        return userImagsResult;
    }

    /**
     *
     * @param userImagsResult
     * The user_imags_result
     */
    public void setUserImagsResult(List<UserImagsResult> userImagsResult) {
        this.userImagsResult = userImagsResult;
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

