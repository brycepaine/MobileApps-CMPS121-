package com.grafixartist.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grafixartist.gallery.CommentActivity;
import com.grafixartist.gallery.ImageModel;
import com.grafixartist.gallery.R;
import com.grafixartist.gallery.UserActivity;
import com.grafixartist.gallery.UserImageModel;

import java.util.List;

/**
 * Created by bryce on 3/13/2016.
 */
public class UserGalleryAdapter  extends RecyclerView.Adapter<UserGalleryAdapter.UserCustomViewHolder>{
    private static String LOG_TAG = "MyApplication";

    private Context mContext;
    private List<UserImageModel> dataList;

    public UserGalleryAdapter(Context context, List<UserImageModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }


    @Override
    public UserCustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_item, null);

        UserCustomViewHolder viewHolder = new UserCustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(UserCustomViewHolder customViewHolder, final int i) {

        final UserImageModel feedItem = dataList.get(i);

        Log.i(LOG_TAG, "feed item " + feedItem);

        Glide.with(mContext).load("http://imagegallery.netai.net/pictures/" + feedItem.getImage_id() + ".JPG")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(customViewHolder.picture);



//        customViewHolder.textView.setText(feedItem.getUserName());
//
//        Log.i(LOG_TAG, "getuser name " + feedItem.getUserName());
//
//        customViewHolder.descriptionView.setText(feedItem.getDescription());
//
//        customViewHolder.time.setText(feedItem.getTimeago());
//
//        customViewHolder.distance.setText(feedItem.getDistance());

//        customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                Log.i(LOG_TAG, "image view clickd");
//                intent = new Intent(mContext.getApplicationContext(), CommentActivity.class);
//                intent.putExtra("image_id", feedItem.getImageID());
//                intent.putExtra("user_name", feedItem.getUserName());
//                mContext.startActivity(intent);
//            }
//        });
//
//        customViewHolder.profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                Log.i(LOG_TAG, "profile clickd");
//                intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
//                intent.putExtra("user_name", feedItem.getUserName());
//                mContext.startActivity(intent);
//            }
//        });
    }



    public void clearData() {
        int size = this.dataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dataList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class UserCustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView picture;


        public UserCustomViewHolder(View view) {
            super(view);
            this.picture = (ImageView) view.findViewById(R.id.user_picture);

        }
    }
}
