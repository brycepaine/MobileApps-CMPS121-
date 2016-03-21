package com.paine.nativeApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paine.nativeApp.CommentActivity;
import com.paine.nativeApp.models.CommentModel;
import com.paine.nativeApp.R;
import com.paine.nativeApp.UserActivity;

import java.util.List;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {
    private static String LOG_TAG = "MyApplication";

    private Context mContext;
    private List<CommentModel> dataList;
    private String user_name;
    private SharedPreferences settings;

    public CommentAdapter(Context context, List<CommentModel> dataList, String user_name) {
        this.mContext = context;
        this.dataList = dataList;
        this.user_name = user_name;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_comment, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {

        final CommentModel feedItem = dataList.get(i);

        Glide.with(mContext)
                .load(feedItem.getProfileComment())
                .into(customViewHolder.profile);

        customViewHolder.from.setText(feedItem.getFromUser());
        customViewHolder.comment.setText(feedItem.getComment());
        customViewHolder.time.setText(feedItem.getTimeagoComment());


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
//

        customViewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "profile clickd");
                intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
                intent.putExtra("user_name", feedItem.getFromUser());
                mContext.startActivity(intent);
            }
        });
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        user_name = settings.getString("user_name", null);
        Log.i("APP USERNAME", user_name);
        if (user_name.equals(feedItem.getFromUser())) {
            customViewHolder.delete.setVisibility(View.VISIBLE);
            customViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;
                    Log.i(LOG_TAG, "name clickd");
                    ((CommentActivity) mContext).DeleteComment(feedItem.getCommentId());


                }
            });
        }
//
//        customViewHolder.sendpm.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                intent = new Intent(mContext.getApplicationContext(), SendPmActivity.class);
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

        protected TextView from;
        protected ImageView profile;
        protected TextView comment;
        protected TextView time;
        protected ImageView delete;


        public CustomViewHolder(View view) {
            super(view);

            this.profile = (ImageView) view.findViewById(R.id.prof_pic);
            this.from = (TextView) view.findViewById(R.id.comment_from);
            this.comment = (TextView) view.findViewById(R.id.comment);
            this.time = (TextView) view.findViewById(R.id.comment_time);
            this.delete = (ImageView) view.findViewById(R.id.delete_comment);
        }
    }

}
