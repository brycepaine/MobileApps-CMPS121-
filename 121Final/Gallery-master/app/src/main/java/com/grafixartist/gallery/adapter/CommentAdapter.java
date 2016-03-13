package com.grafixartist.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grafixartist.gallery.CommentElement;
import com.grafixartist.gallery.R;
import com.grafixartist.gallery.popup.PopCommentDelete;
import com.grafixartist.gallery.popup.PopCommentEdit;
import android.widget.ImageView;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentElement> {

    int resource;
    Context context;
    String my_user_name;
    String image_id;
    public static String LOG_TAG = "ChatApplication";
    String comment_id;

    public CommentAdapter(Context _context, int _resource, List<CommentElement> items, String _user_name, String _image_id) {
        super(_context, _resource, items);
        resource = _resource;
        context = _context;
        my_user_name = _user_name;
        image_id = _image_id;

    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LinearLayout newView;

        CommentElement w = getItem(position);



            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(resource, newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

//        ImageView my_pf_pic = (ImageView) newView.findViewById(R.id.comment_id);
//        if(w.equals(0)) {
//
//            my_pf_pic.setVisibility(View.VISIBLE);
//            Glide.with(getContext())
//                    .load("http://imagegallery.netai.net/pictures/" + image_id + ".JPG")
//                    .into(my_pf_pic);
//
//        }else {
//            my_pf_pic.setVisibility(View.INVISIBLE);

            ImageView pf_pic = (ImageView) newView.findViewById(R.id.profile_pic_list);

            Glide.with(getContext())
                    .load("http://imagegallery.netai.net/pictures/" + w.profile_pic_id + ".JPG")
                    .into(pf_pic);

            // Fills in the view.
            TextView tv = (TextView) newView.findViewById(R.id.get_comment);
            tv.setText(w.user_name + "\n\n" + w.description);

            if (w.user_name.equals(my_user_name)) {

                ImageButton edit = (ImageButton) newView.findViewById(R.id.editicon);
                edit.setVisibility(View.VISIBLE);
                edit.setClickable(true);

                //b.setText(w.details);
                // Sets a listener for the button, and a tag for the button as well.
                edit.setTag(R.id.description, w.description);
                edit.setTag(R.id.comment_id, w.comment_id);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String edit_comment = v.getTag(R.id.description).toString();
                        String comment_id = v.getTag(R.id.comment_id).toString();
                        Intent intent = new Intent(context, PopCommentEdit.class);
                        intent.putExtra("description", edit_comment);
                        intent.putExtra("image_id", image_id);
                        intent.putExtra("comment_id", comment_id);
                        context.startActivity(intent);

                    }
                });

                ImageButton delete = (ImageButton) newView.findViewById(R.id.delete);
                delete.setTag(R.id.description, w.description);
                delete.setTag(R.id.comment_id, w.comment_id);
                delete.setVisibility(View.VISIBLE);
                delete.setClickable(true);
                // Sets a listener for the button, and a tag for the button as well.
                delete.setTag(w.description);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String edit_comment = v.getTag(R.id.description).toString();
                        String comment_id = v.getTag(R.id.comment_id).toString();
                        Intent intent = new Intent(context, PopCommentDelete.class);
                        intent.putExtra("description", edit_comment);
                        intent.putExtra("image_id", image_id);
                        intent.putExtra("comment_id", comment_id);
                        context.startActivity(intent);
                    }
                });
            }
        //}

        return newView;
    }
}