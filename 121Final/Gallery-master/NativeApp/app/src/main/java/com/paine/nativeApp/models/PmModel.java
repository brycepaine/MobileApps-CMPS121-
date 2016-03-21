package com.paine.nativeApp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bryce on 3/14/2016.
 */
public class PmModel implements Parcelable {

    String user_name, pm, timeago;

    public PmModel() {

    }

    protected PmModel(Parcel in) {
        user_name = in.readString();
        pm = in.readString();
        timeago = in.readString();

    }

    public static final Creator<PmModel> CREATOR = new Creator<PmModel>() {
        @Override
        public PmModel createFromParcel(Parcel in) {
            return new PmModel(in);
        }

        @Override
        public PmModel[] newArray(int size) {
            return new PmModel[size];
        }
    };

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String name) {
        this.user_name = name;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String url) {
        this.pm = url;
    }

    public String getTimeago(){return timeago;}

    public void setTimeago(String t){this.timeago = t;}




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);

    }
}