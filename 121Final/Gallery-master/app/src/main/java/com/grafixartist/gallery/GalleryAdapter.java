package com.grafixartist.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
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
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

            ImageModel feedItem = dataList.get(i);

            Glide.with(mContext).load(feedItem.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(customViewHolder.imageView);

            customViewHolder.textView.setText(feedItem.getUserName());

            Log.i(LOG_TAG, "getuser name " + feedItem.getUserName());

            customViewHolder.buttonView.setText(feedItem.getDescription());

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textView;
        protected Button buttonView;

        public CustomViewHolder(View view) {
            super(view);

            this.imageView = (ImageView) view.findViewById(R.id.item_img);
            this.textView = (TextView) view.findViewById(R.id.username);
            this.buttonView = (Button) view.findViewById(R.id.btn);
        }
    }

}
