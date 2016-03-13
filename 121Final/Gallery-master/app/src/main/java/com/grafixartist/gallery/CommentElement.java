package com.grafixartist.gallery;

/**
 * Created by shobhit on 1/24/16.
 */
public class CommentElement {

    public String timestamp;

    public String image_id;

    public String user_name;

    public String description;

    public String comment_id;

    public String profile_pic_id;


    CommentElement() {};

    CommentElement(String ts, String ii, String un, String dm, String cm, String pf) {
        timestamp = ts;
        image_id = ii;
        user_name = un;
        description = dm;
        comment_id = cm;
        profile_pic_id = pf;
    }
}
