package com.paine.nativeApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.paine.nativeApp.PmConversationActivity;
import com.paine.nativeApp.models.PmModel;
import com.paine.nativeApp.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by bryce on 3/14/2016.
 */




/**
 * Created by Suleiman19 on 10/22/15.
 */
public class PmAdapter extends RecyclerView.Adapter<PmAdapter.CustomViewHolder> {
    private static String LOG_TAG = "MyApplication";

    private Context mContext;
    private List<PmModel> dataList;
    private String from;

    public PmAdapter(Context context, List<PmModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm, null);

        final CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        final PmModel feedItem = dataList.get(i);


//        Glide.with(mContext)
//                .load(feedItem.getProf())
//                .into(customViewHolder.profile_pic);
        Glide.with(mContext).load(feedItem.getProf()).asBitmap().centerCrop().into(new BitmapImageViewTarget(customViewHolder.profile_pic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);

                customViewHolder.profile_pic.setImageDrawable(circularBitmapDrawable);
            }
        });

        customViewHolder.pm.setText(feedItem.getPm());

        customViewHolder.name.setText(feedItem.getUserName());

        customViewHolder.time.setText(feedItem.getTimeago());

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "name clickd " + feedItem.getUserName());
                intent = new Intent(mContext.getApplicationContext(), PmConversationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i(LOG_TAG, "on click username " + feedItem.getUserName());
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });




//
//        customViewHolder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                Log.i(LOG_TAG, "name clickd");
//                intent = new Intent(mContext.getApplicationContext(), PmConversationActivity.class);
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

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        protected TextView pm;
        protected TextView name;
        protected TextView time;
        protected ImageView profile_pic;


        public CustomViewHolder(View view) {
            super(view);

            this.pm = (TextView) view.findViewById(R.id.pm);
            this.name = (TextView) view.findViewById(R.id.pm_from);
            this.time = (TextView) view.findViewById(R.id.pm_time);
            this.profile_pic = (ImageView) view.findViewById(R.id.profile);


        }
    }

}
