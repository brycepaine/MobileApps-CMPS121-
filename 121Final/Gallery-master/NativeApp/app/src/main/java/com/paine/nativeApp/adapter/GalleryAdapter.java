package com.paine.nativeApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.paine.nativeApp.CommentActivity;
import com.paine.nativeApp.MainActivity;
import com.paine.nativeApp.MainFragment;
import com.paine.nativeApp.PmConversationActivity;
import com.paine.nativeApp.models.ImageModel;
import com.paine.nativeApp.R;
import com.paine.nativeApp.SendPmActivity;
import com.paine.nativeApp.UserActivity;
import com.paine.nativeApp.response.Example;

import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

            final ImageModel feedItem = dataList.get(i);

            Glide.with(mContext).load(feedItem.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(customViewHolder.imageView);

        Glide.with(mContext)
                .load(feedItem.getProfile())
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(customViewHolder.profile);



        Log.i(LOG_TAG, "description length " + feedItem.getDescription().length());
        if (feedItem.getDescription().length()<2){
            customViewHolder.descriptionView.setVisibility(View.GONE);
        }

        if(feedItem.getUserVote() == -1){
            customViewHolder.downvote.setColorFilter(Color.argb(255, 165, 42, 42));
        }

        if(feedItem.getUserVote() == 1){
            customViewHolder.upvote.setColorFilter(Color.argb(255, 0, 0, 128));
        }

        customViewHolder.textView.setText(feedItem.getUserName());

        Log.i(LOG_TAG, "getuser name " + feedItem.getUserName());

        customViewHolder.descriptionView.setText(feedItem.getDescription());

        customViewHolder.time.setText(feedItem.getTimeago());

        customViewHolder.distance.setText(feedItem.getDistance());

        Log.i(LOG_TAG, "votecount " + feedItem.getVotes());
        customViewHolder.votecount.setText(Integer.toString(feedItem.getVotes()));

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

        customViewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "comment btn clickd");
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


//     FIX THIS
        customViewHolder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewHolder.downvote.setColorFilter(Color.argb(255, 165, 42, 42));
                customViewHolder.upvote.setColorFilter(Color.argb(255, 29, 29, 29));
                customViewHolder.votecount.setText(Integer.toString(feedItem.getVotes() - 1));
                Log.i(LOG_TAG, "name clickd");
                Vote(feedItem.getImageID(), "down");
            }
        });

        customViewHolder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewHolder.upvote.setColorFilter(Color.argb(255, 0, 0, 128));
                customViewHolder.downvote.setColorFilter(Color.argb(255, 29, 29, 29));
                customViewHolder.votecount.setText(Integer.toString(feedItem.getVotes() + 1));
                Log.i(LOG_TAG, "name clickd");
                Vote(feedItem.getImageID(), "up");


            }
        });

        customViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                Log.i(LOG_TAG, "name clickd " + feedItem.getUserName());
                intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
                intent.putExtra("user_name", feedItem.getUserName());
                mContext.startActivity(intent);
            }
        });

//        customViewHolder.sendpm.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                final Intent intent;
//                intent = new Intent(mContext.getApplicationContext(), SendPmActivity.class);
//                intent.putExtra("user_name", feedItem.getUserName());
//                mContext.startActivity(intent);
//            }
//        });

        customViewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "clicked menu more");
                PopupMenu popup = new PopupMenu(mContext.getApplicationContext(), customViewHolder.more);

                popup.getMenuInflater()
                        .inflate(R.menu.more_menu, popup.getMenu());

                popup.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Send Message")) {
                            final Intent intent;
                            intent = new Intent(mContext.getApplicationContext(), PmConversationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.i(LOG_TAG, "on click username " + feedItem.getUserName());
                            intent.putExtra("user_name", feedItem.getUserName());
                            mContext.startActivity(intent);

                        } else if (item.getTitle().equals("View Profile")) {
                            final Intent intent;
                            Log.i(LOG_TAG, "profile clickd");
                            intent = new Intent(mContext.getApplicationContext(), UserActivity.class);
                            intent.putExtra("user_name", feedItem.getUserName());
                            mContext.startActivity(intent);
                        } else if (item.getTitle().equals("Comment")) {
                            final Intent intent;
                            Log.i(LOG_TAG, "comment btn clickd");
                            intent = new Intent(mContext.getApplicationContext(), CommentActivity.class);
                            intent.putExtra("image_id", feedItem.getImageID());
                            intent.putExtra("user_name", feedItem.getUserName());
                            mContext.startActivity(intent);

                        }
                        return true;
                    }
                }));

                popup.show();
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


        protected TextView votecount;
        protected ImageView imageView;
        protected TextView textView;
        protected TextView distance;
        protected TextView descriptionView;
        protected TextView time;
        protected CircleImageView profile;
        protected ImageButton upvote;
        protected ImageButton downvote;
        protected ImageButton comments;
        protected ImageButton more;




        public CustomViewHolder(View view) {
            super(view);

            this.descriptionView = (TextView) view.findViewById(R.id.description);
            this.imageView = (ImageView) view.findViewById(R.id.item_img);
            this.textView = (TextView) view.findViewById(R.id.username);
            this.distance = (TextView) view.findViewById(R.id.miles_away);
            this.time = (TextView) view.findViewById(R.id.timestamp);
            this.profile = (CircleImageView) view.findViewById(R.id.prof_pic);
            this.upvote = (ImageButton) view.findViewById(R.id.upvote_arrow);
            this.downvote = (ImageButton) view.findViewById(R.id.downvote_arrow);
            this.comments = (ImageButton) view.findViewById(R.id.comment_btn);
            this.more = (ImageButton) view.findViewById(R.id.more_options);
            this.votecount = (TextView) view.findViewById(R.id.vote_count);

        }
    }

    public void Vote(String image_id, String vote) {
        SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(mContext);
        String user_name = settings.getString("user_name",null);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://empirical-realm-123103.appspot.com/pictureApp/")    //We are using Foursquare API to get data
                .addConverterFactory(GsonConverterFactory.create())    //parse Gson string
                .client(httpClient)    //add logging
                .build();

        Call<Example> GetMessageCall;

        Log.i(LOG_TAG, "the vote in main is " + vote);

        if (vote.equals("up")) {
            MainFragment.UpvoteService get_service = retrofit.create(MainFragment.UpvoteService.class);
            GetMessageCall = get_service.upvote(user_name, image_id);
            Log.i(LOG_TAG, "upvote in user");
        } else{
            MainFragment.DownvoteService get_service = retrofit.create(MainFragment.DownvoteService.class);
            GetMessageCall = get_service.upvote(user_name, image_id);
            Log.i(LOG_TAG, "downvote in user");
        }
        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                Log.i(LOG_TAG, "upvoted");

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
    }

}
