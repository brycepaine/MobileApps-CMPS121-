package com.paine.nativeApp.response;

/**
 * Created by bryce on 3/9/2016.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageURLResponse {

    @SerializedName("image_result")
    @Expose
    private List<ImageResult> imageResult = new ArrayList<ImageResult>();
    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The imageResult
     */
    public List<ImageResult> getImageResult() {
        return imageResult;
    }

    /**
     *
     * @param imageResult
     * The image_result
     */
    public void setImageResult(List<ImageResult> imageResult) {
        this.imageResult = imageResult;
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
