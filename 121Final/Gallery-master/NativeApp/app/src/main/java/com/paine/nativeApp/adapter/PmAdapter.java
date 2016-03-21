package com.paine.nativeApp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paine.nativeApp.models.PmModel;
import com.paine.nativeApp.R;

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

    public PmAdapter(Context context, List<PmModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pm, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {

        final PmModel feedItem = dataList.get(i);

        customViewHolder.pm.setText(feedItem.getPm());

        customViewHolder.name.setText(feedItem.getUserName());





//        customViewHolder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                Log.i(LOG_TAG, "name clickd");
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

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        protected TextView pm;
        protected TextView name;


        public CustomViewHolder(View view) {
            super(view);

            this.pm = (TextView) view.findViewById(R.id.pm);
            this.name = (TextView) view.findViewById(R.id.pm_from);

        }
    }

}
