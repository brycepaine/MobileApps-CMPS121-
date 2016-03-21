package com.paine.nativeApp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 3/13/2016.
 */
public class UserImagsResult {
    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("description")
    @Expose
    private String description;

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

}
