package com.paine.nativeApp.models;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by bryce on 3/13/2016.
 */
public class UserImageModel implements Parcelable{
    String image_id, description;

    public UserImageModel(){

    }

    protected UserImageModel(Parcel in){
        image_id = in.readString();
        description = in.readString();
    }
    public static final Creator<UserImageModel> CREATOR = new Creator<UserImageModel>() {
        @Override
        public UserImageModel createFromParcel(Parcel in) {
            return new UserImageModel(in);
        }

        @Override
        public UserImageModel[] newArray(int size) {
            return new UserImageModel[size];
        }
    };

//    public String getUserName() {
//        return user_name;
//    }
//
//    public void setUserName(String name) {
//        this.user_name = name;
//    }
//
    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String id) {
        this.image_id = id;
    }

//    public String getTimeago(){return timeago;}
//
//    public void setTimeago(String t){this.timeago = t;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

//    public String getTimestamp(){return timestamp;}
//
//    public void setTimestamp(String timestamp){this.timestamp = timestamp;}
//
//    public String getImageID(){return image_id;}
//
//    public void setImageID(String im){this.image_id = im;}
//
//    public String getDistance(){return distance;}
//
//    public void setDistance(String d){this.distance = d;}
//
//    public String getProfile(){return profile_pic;}
//
//    public void setProfile(String p){this.profile_pic = p;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(user_name);
//        dest.writeString(url);
    }
}
