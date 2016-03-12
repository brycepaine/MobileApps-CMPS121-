package com.grafixartist.gallery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class ImageModel implements Parcelable {

    String user_name, url, description, image_id;
    Object timestamp;

    public ImageModel() {

    }

    protected ImageModel(Parcel in) {
        user_name = in.readString();
        url = in.readString();
        description = in.readString();
//        timestamp = in.rea
        image_id = in.readString();
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
        this.user_name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

//    public String getTimestamp(){return timestamp;}

//    public void setTimestamp(Object timestamp){this.timestamp = timestamp;}

    public String getImageID(){return image_id;}

    public void setImageID(String im){this.image_id = im;}


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
