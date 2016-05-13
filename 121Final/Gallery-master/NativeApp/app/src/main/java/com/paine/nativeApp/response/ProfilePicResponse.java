package com.paine.nativeApp.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProfilePicResponse {

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("response")
    @Expose
    private String response;

    /**
     *
     * @return
     * The profilePic
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     *
     * @param profilePic
     * The profile_pic
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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