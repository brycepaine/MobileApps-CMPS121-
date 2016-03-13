package com.grafixartist.gallery;

/**
 * Created by shobhit on 1/24/16.
 */
public class ListElement {


    public String image_id;

    public String user_name;

    public String description;

    public Object timestamp;


    ListElement() {};

    ListElement(Object ts, String ms, String nn, String ui) {
        timestamp = ts;
        image_id = ms;
        user_name = nn;
        description = ui;
    }
}
