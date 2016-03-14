package com.grafixartist.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.grafixartist.gallery.adapter.GalleryAdapter;
import com.grafixartist.gallery.adapter.UserGalleryAdapter;
import com.grafixartist.gallery.response.UserImagsResult;
import com.grafixartist.gallery.response.UserImagesResponse;
import android.support.v7.widget.LinearLayoutManager;


import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bryce on 3/13/2016.
 */
public class UserActivity extends AppCompatActivity {
    private static String LOG_TAG = "MyApplication";

    private ArrayList<UserImageModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserGalleryAdapter adapter;
    private ProgressBar progressBar;
    private boolean refresh;

    private LocationData locationData;
    private String lng;
    private String lat;
    private String user_name;

    private Integer radius;
    private Integer theposition;

    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        Log.i(LOG_TAG, "on resume");
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        Intent i = getIntent();
        user_name = i.getStringExtra("user_name");
        getImageURLs();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        super.onPostResume();
    }

    private void getImageURLs(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        setProgressBarIndeterminateVisibility(true);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://empirical-realm-123103.appspot.com/pictureApp/")
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        ImageURLService service = retrofit.create(ImageURLService.class);

        Call<UserImagesResponse> queryResponseCall =
                service.getURL(user_name);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<UserImagesResponse>() {
            @Override
            public void onResponse(Response<UserImagesResponse> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }
                ArrayList<UserImagsResult> imageInfo = new ArrayList<UserImagsResult>(response.body().getUserImagsResult());
                for (int i = imageInfo.size()-1; i >= 0; i--) {
                    UserImagsResult res = imageInfo.get(i);
                    //if image is within proximity to user's location, then display it here
                    UserImageModel userImageModel = new UserImageModel();
//                    imageModel.setUserName(res.getUserName());
//                    userImageModel.setImage("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");
                    userImageModel.setDescription(res.getDescription());
                    userImageModel.setImage_id(res.getImageId());
//                    imageModel.setTimestamp(res.getTimestamp().toString());
//                    imageModel.setDistance(res.getDistance());
//                    imageModel.setTimeago(res.getTimeago());
//                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");




                    Log.i(LOG_TAG, "image info list at :" + res
                            + " URL : " + userImageModel.getImage_id() + "  description  " + res.getDescription());

                    mydata.add(userImageModel);

                    Log.i(LOG_TAG, "the data is " + mydata);
                }
                loadImages();


                Log.i(LOG_TAG, "Code is: " + response.code());
                Log.i(LOG_TAG, "response " + response.body().getUserImagsResult());
            }


            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Log.i(LOG_TAG, "throwable t: " + t);
            }
        });



    }
    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */
    public interface ImageURLService {
        @GET("default/user_images")
        Call<UserImagesResponse> getURL(@Query("user_name") String user_name);



    }

    private void loadImages(){
        Log.i(LOG_TAG, "loadimages");
        progressBar.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

        adapter = new UserGalleryAdapter(getApplicationContext(), mydata);
        mRecyclerView.setAdapter(adapter);
        //        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
//                new RecyclerItemClickListener.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Log.i(LOG_TAG, "mydata: " + mydata + " position: " + position);
//                        String imageToPass = mydata.get(position).getImageID();
//                        Log.i(LOG_TAG, "the view is " + view.getId());
//                        Log.i(LOG_TAG, "other view is" + findViewById(R.id.imageView));
//                        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
//                        intent.putExtra("image_id", imageToPass);
//                        intent.putExtra("user_name", user_name);
//                        startActivity(intent);
//
//                    }
//                }));

    }


}
