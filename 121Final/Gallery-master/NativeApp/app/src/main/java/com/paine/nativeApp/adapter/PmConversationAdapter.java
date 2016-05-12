package com.paine.nativeApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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
public class PmConversationAdapter extends RecyclerView.Adapter<PmConversationAdapter.CustomViewHolder> {
    private static String LOG_TAG = "MyApplication";

    private Context mContext;
    private List<PmModel> dataList;
    private String from;
    private String user_name;
    private Integer count;
    private final int ME = 1;
    private final int YOU = 0;



    public PmConversationAdapter(Context context, List<PmModel> dataList, String user_name) {
        this.mContext = context;
        this.dataList = dataList;
        this.user_name = user_name;
        this.count = 0;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        Log.i(LOG_TAG, "i " + i);
        if(i == ME){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm_conversation_right, null);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm_conversation, null);
//            Log.i(LOG_TAG, "not insed equal username " + dataList.get(count).getUserName() + " user " + user_name);

        }


//
//        Log.i(LOG_TAG, "int i : " + count  + " i + " + i + " user : " + dataList.get(count).getUserName() + "userloggedin " + user_name);
//        if(user_name.equals(dataList.get(count).getUserName())){
//            Log.i(LOG_TAG, "insed equal username " + dataList.get(count).getUserName() + " user " + user_name);
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm_conversation_right, null);
//
//        }else{
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm_conversation, null);
//            Log.i(LOG_TAG, "not insed equal username " + dataList.get(count).getUserName() + " user " + user_name);
//
//        }


        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getUserName().equals(user_name)) {
            return ME;
        } else {
            return YOU;
        }

    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
//        switch(customViewHolder.getItemViewType()){
//            case ME:
//                ViewHolder1 vh1 = (ViewHolder1) customViewHolder;
//        }

        final PmModel feedItem = dataList.get(i);

        from = "From: " + feedItem.getUserName();

        customViewHolder.pm.setText(feedItem.getPm());


        customViewHolder.time.setText(feedItem.getTimeago());

        Log.i(LOG_TAG, " profile from conversation adapter " + feedItem.getProf());

        Log.i(LOG_TAG, "right before crash feed item " + feedItem.getProf());

        Glide.with(mContext).load(feedItem.getProf()).asBitmap().centerCrop().into(new BitmapImageViewTarget(customViewHolder.prof) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);

                customViewHolder.prof.setImageDrawable(circularBitmapDrawable);
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
        protected TextView time;
        protected ImageView prof;



        public CustomViewHolder(View view) {
            super(view);

            this.pm = (TextView) view.findViewById(R.id.pm);
            this.time = (TextView) view.findViewById(R.id.pm_time);
            this.prof = (ImageView) view.findViewById(R.id.profile);

        }
    }

}
