package com.paine.nativeApp.response;



import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatUsersResponse {

    @SerializedName("chat_result")
    @Expose
    private List<String> chatResult = new ArrayList<String>();
    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The chatResult
     */
    public List<String> getChatResult() {
        return chatResult;
    }

    /**
     *
     * @param chatResult
     * The chat_result
     */
    public void setChatResult(List<String> chatResult) {
        this.chatResult = chatResult;
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