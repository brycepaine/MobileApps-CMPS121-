package com.paine.nativeApp.adapter;

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
import com.paine.nativeApp.CommentActivity;
import com.paine.nativeApp.models.ImageModel;
import com.paine.nativeApp.R;
import com.paine.nativeApp.SendPmActivity;
import com.paine.nativeApp.UserActivity;

import java.util.List;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.CustomViewHolder> {
    private static String LOG_TAG = "MyApplication";

    private Context mContext;
    private List<ImageModel> dataList;

    public GalleryAdapter(Context context, List<ImageModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {

            final ImageModel feedItem = dataList.get(i);

            Glide.with(mContext).load(feedItem.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(customViewHolder.imageView);

        Glide.with(mContext)
                .load(feedItem.getProfile())
                .into(customViewHolder.profile);

        customViewHolder.textView.setText(feedItem.getUserName());

        Log.i(LOG_TAG, "getuser name " + feedItem.getUserName());

        customViewHolder.descriptionView.setText(feedItem.getDescription());

        customViewHolder.time.setText(feedItem.getTimeago());

        customViewHolder.distance.setText(feedItem.getDistance());

        customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "image view clickd");
                intent = new Intent(mContext.getApplicationContext(), CommentActivity.class);
                intent.putExtra("image_id", feedItem.getImageID());
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });


        customViewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "profile clickd");
                intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });

        customViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "name clickd");
                intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });

        customViewHolder.sendpm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent = new Intent(mContext.getApplicationContext(), SendPmActivity.class);
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });





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

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textView;
        protected TextView distance;
        protected TextView descriptionView;
        protected TextView time;
        protected ImageView profile;
        protected TextView sendpm;

        public CustomViewHolder(View view) {
            super(view);

            this.descriptionView = (TextView) view.findViewById(R.id.description);
            this.imageView = (ImageView) view.findViewById(R.id.item_img);
            this.textView = (TextView) view.findViewById(R.id.username);
            this.distance = (TextView) view.findViewById(R.id.miles_away);
            this.time = (TextView) view.findViewById(R.id.timestamp);
            this.profile = (ImageView) view.findViewById(R.id.prof_pic);
            this.sendpm = (TextView) view.findViewById(R.id.sendpm);
        }
    }

}
