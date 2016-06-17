package com.paine.nativeApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.paine.nativeApp.adapter.PmAdapter;
import com.paine.nativeApp.models.PmModel;
import com.paine.nativeApp.response.PmResponse;
import com.paine.nativeApp.response.PmResult;

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
 * Created by bryce on 3/14/2016.
 */
public class PmActivity extends AppCompatActivity{

    private static String LOG_TAG = "MyApplication";

    private ArrayList<PmModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PmAdapter adapter;
    private ProgressBar progressBar;
    private boolean refresh;

    private LocationData locationData;
    private String lng;
    private String lat;
    private String user_name;

    private Integer radius;
    private Integer theposition;
    private String profile_image;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "inside pm activity create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pm);

        Log.i(LOG_TAG, "on resume");
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        user_name = i.getStringExtra("user_name");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getImageURLs();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pm, menu);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//this updates the users location and then refreshes the images





        if (id == R.id.home){
            Log.i(LOG_TAG, "options home");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user_name", user_name);
            startActivity(intent);
        }


        if (id == R.id.signout){
            SharedPreferences.Editor e = settings.edit();
            e.remove("user_name");
            e.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        Call<PmResponse> queryResponseCall =
                service.getURL(user_name);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<PmResponse>() {
            @Override
            public void onResponse(Response<PmResponse> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }
                ArrayList<PmResult> imageInfo = new ArrayList<PmResult>(response.body().getPmResult());
                for (int i = imageInfo.size() - 1; i >= 0; i--) {
                    PmResult res = imageInfo.get(i);
                    Log.i(LOG_TAG, "pm " + res.getPm());
                    //if image is within proximity to user's location, then display it here
                    PmModel userImageModel = new PmModel();
//                    imageModel.setUserName(res.getUserName());
//                    userImageModel.setImage("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");

                    userImageModel.setPm(res.getPm());
                    userImageModel.setUserName(res.getUserName());
                    userImageModel.setTimeago(res.getTimeago());
                    userImageModel.setProf("http://imagegallery.netai.net/pictures/" + res.getProf() + ".JPG");
                    Log.i(LOG_TAG, "THE PROFILE PIC : " + userImageModel.getProf());
//                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");

//                    imageModel.setTimestamp(res.getTimestamp().toString());
//                    imageModel.setDistance(res.getDistance());
//                    imageModel.setTimeago(res.getTimeago());
//                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");


                    mydata.add(userImageModel);

                    Log.i(LOG_TAG, "the data is " + mydata);
                }
                loadImages();


                Log.i(LOG_TAG, "Code is: " + response.code());
                Log.i(LOG_TAG, "response " + response.body().getPmResult());
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
        @GET("default/get_pm")
        Call<PmResponse> getURL(@Query("user_name") String user_name);



    }

    private void loadImages(){
        Log.i(LOG_TAG, "loadimages");
        progressBar.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);

        adapter = new PmAdapter(getApplicationContext(), mydata);
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

