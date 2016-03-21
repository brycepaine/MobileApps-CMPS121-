package com.paine.nativeApp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class CommentModel implements Parcelable {

    String comment, profilePic, timeago, timestamp, commentId, userName;

    public CommentModel() {

    }

    protected CommentModel(Parcel in) {
        comment = in.readString();
        timestamp = in.readString();
        profilePic = in.readString();
        commentId = in.readString();
        timeago = in.readString();
        userName = in.readString();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public String getFromUser() {
        return userName;
    }

    public void setFromUser(String name) {
        this.userName = name;
    }


    public String getTimeagoComment(){return timeago;}

    public void setTimeago(String t){this.timeago = t;}

    public String getComment(){return comment;}

    public void setComment(String description){this.comment = description;}

    public String getTimestampComment(){return timestamp;}

    public void setTimestampComment(String timestamp){this.timestamp = timestamp;}


    public String getCommentId(){return commentId;}

    public void setCommentId(String d){this.commentId = d;}

    public String getProfileComment(){return profilePic;}

    public void setProfileComment(String p){this.profilePic = p;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);

    }
}
