package com.paine.nativeApp.response;

/**
 * Created by thomasburch on 3/10/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class CommentResult {

    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("timeago")
    @Expose
    private String timeago;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @SerializedName("user_name")
    @Expose
    private String userName;

    public String getTimeago() {
        return timeago;
    }

    /**
     *
     * @param timeago
     * The timeago
     */
    public void setTimeago(String timeago) {
        this.timeago = timeago;
    }
    /**
     *
     * @return
     * The profile_pic
     */
    public String getProfilePic() {
        return profilePic;
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