package com.grafixartist.gallery.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryce on 3/14/2016.
 */
public class PmResult {
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("timeago")
    @Expose
    private String timeago;
    @SerializedName("pm")
    @Expose
    private String pm;

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
     * The timeago
     */
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
     * The pm
     */
    public String getPm() {
        return pm;
    }

    /**
     *
     * @param pm
     * The pm
     */
    public void setPm(String pm) {
        this.pm = pm;
    }


}
