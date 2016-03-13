package com.grafixartist.gallery.response;

/**
 * Created by thomasburch on 3/10/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class CommentResult {

    @SerializedName("comment")
    @Expose
    public String comment;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;
    @SerializedName("comment_id")
    @Expose
    public String commentId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("profile_pic")
    @Expose
    public String profile_pic;

    /**
     *
     * @return
     * The profile_pic
     */
    public String getProfilePic() {
        return profile_pic;
    }

    /**
     *
     * @return
     * The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     * The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     *
     * @return
     * The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     * The commentId
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     *
     * @param commentId
     * The comment_id
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     *
     * @return
     * The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The user_name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

}