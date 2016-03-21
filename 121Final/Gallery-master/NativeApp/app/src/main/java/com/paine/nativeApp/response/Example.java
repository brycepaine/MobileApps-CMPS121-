package com.paine.nativeApp.response;

/**
 * Created by thomasburch on 3/10/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

//import javax.annotation.Generated;

//@Generated("org.jsonschema2pojo")
public class Example {

    @SerializedName("comment_result")
    @Expose
    public List<CommentResult> commentResult = new ArrayList<CommentResult>();
    @SerializedName("response")
    @Expose
    public String response;

    /**
     *
     * @return
     * The commentResult
     */
    public List<CommentResult> getCommentResult() {
        return commentResult;
    }

    /**
     *
     * @param commentResult
     * The comment_result
     */
    public void setCommentResult(List<CommentResult> commentResult) {
        this.commentResult = commentResult;
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