package com.paine.nativeApp.response;

/**
 * Created by bryce on 3/9/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResult {

    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("description")
    @Expose
    private String description;


    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("timestamp")
    @Expose
    private Object timestamp;

    @SerializedName("timeago")
    @Expose
    private String ago;

    @SerializedName("profile_pic")
    @Expose
    private String profile_pic;

    /**
     *
     * @return
     * The imageId
     */
    public String getImageId() {
        return imageId;
    }

    /**
     *
     * @param imageId
     * The image_id
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
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

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The timestamp
     */
    public Object getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getDistance(){return distance;}

    public String getTimeago(){return ago;}

    public void setTimeago(String a){this.ago = a;}

    public String getProfPic(){return profile_pic;}



}