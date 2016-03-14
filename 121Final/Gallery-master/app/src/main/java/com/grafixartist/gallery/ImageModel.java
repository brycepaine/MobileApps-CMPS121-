package com.grafixartist.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class ImageModel implements Parcelable {

    String user_name, url, description, image_id, timestamp, distance,timeago, profile_pic;

    public ImageModel() {

    }

    protected ImageModel(Parcel in) {
        user_name = in.readString();
        url = in.readString();
        description = in.readString();
        timestamp = in.readString();
        image_id = in.readString();
        distance = in.readString();
        timeago = in.readString();
        profile_pic = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String name) {
        this.user_name = name + ": ";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeago(){return timeago;}

    public void setTimeago(String t){this.timeago = t;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

    public String getTimestamp(){return timestamp;}

    public void setTimestamp(String timestamp){this.timestamp = timestamp;}

    public String getImageID(){return image_id;}

    public void setImageID(String im){this.image_id = im;}

    public String getDistance(){return distance;}

    public void setDistance(String d){this.distance = d;}

    public String getProfile(){return profile_pic;}

    public void setProfile(String p){this.profile_pic = p;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(url);
    }
}
