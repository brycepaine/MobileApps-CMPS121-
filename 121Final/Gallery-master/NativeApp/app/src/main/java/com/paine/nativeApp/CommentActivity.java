package com.paine.nativeApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.paine.nativeApp.adapter.CommentAdapter;
import com.paine.nativeApp.models.CommentModel;

import com.paine.nativeApp.response.Example;
import com.paine.nativeApp.response.CommentResult;

/**
 * Created by bryce on 3/10/2016.
 */
/**
 * Created by thomasburch on 3/10/16.
 */


public class CommentActivity extends AppCompatActivity {

    private String LOG_TAG = "MyApplication";
    private String image_id;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private Boolean refresh;
    private String user_name;
    private SharedPreferences settings;

    private ArrayList<CommentModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CommentAdapter adapter;

    private EditText editText;
    private Button send;
    private String comment_id;
    private String comment;
    private ImageView preview;

    public interface GetService {
        @GET("default/get_comments")
        Call<Example> get_comment(@Query("image_id") String image_id);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);

        preview=(ImageView) findViewById(R.id.the_image);
        Log.i(LOG_TAG, "onCreate Comment Activity");
        //spinner = (ProgressBar)findViewById(R.id.progressBar1);

        image_id = getIntent().getStringExtra("image_id");
        Log.i(LOG_TAG, "image_id" + image_id);

        editText = (EditText)findViewById(R.id.edit_text);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecureRandomString srs = new SecureRandomString();
                comment_id = srs.nextString();

                comment = editText.getText().toString();
                editText.setText("");
                mydata.clear();
                postComments(image_id, comment, comment_id);


            }
        });




        toolbar = (Toolbar) findViewById(R.id.toolbar_comment);
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
    protected void onPause() {
        if (mydata!=null){
            mydata.clear();
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        image_id = getIntent().getStringExtra("image_id");
        Log.i(LOG_TAG, "image_id" + image_id);

        Glide.with(this).load("http://imagegallery.netai.net/pictures/" + image_id + ".JPG")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(preview);


        user_name = getIntent().getStringExtra("user_name");
        //     initialize refresh to false
        refresh = false;
        settings = PreferenceManager.getDefaultSharedPreferences(this);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar_comment);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_comment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        refreshComments(image_id);//refresh chats
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment_menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//this updates the users location and then refreshes the images



        if (id == R.id.signout){
            SharedPreferences.Editor e = settings.edit();
            e.remove("user_name");
            e.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // refresh THE MESSAGES
    public void refreshComments(final String image_id){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://empirical-realm-123103.appspot.com/pictureApp/")	//We are using Foursquare API to get data
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        GetService get_service = retrofit.create(GetService.class);
        Call<Example> GetMessageCall = get_service.get_comment(image_id);

        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }

                //parse the response for list of messages
                ArrayList<CommentResult> imageInfo = new ArrayList<CommentResult>(response.body().getCommentResult());

                for (int i = imageInfo.size() - 1; i >= 0; i--) {
                    CommentResult res = imageInfo.get(i);
                    CommentModel imageModel = new CommentModel();
                    imageModel.setFromUser(res.getUserName());
                    imageModel.setComment(res.getComment());
                    imageModel.setCommentId(res.getCommentId());
                    imageModel.setProfileComment("http://imagegallery.netai.net/pictures/" + res.getProfilePic() + ".JPG");
                    imageModel.setTimeago(res.getTimeago());

                    mydata.add(imageModel);
                }

                // urls have been called, end profressbar
                progressBar.setVisibility(View.GONE);

                //send data to adapter
                adapter = new CommentAdapter(CommentActivity.this, mydata, user_name);
                mRecyclerView.setAdapter(adapter);
            }


            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }

        });
    }

    public void postComments(final String image_id, String comment, String comment_id) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);

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

        PostService get_service = retrofit.create(PostService.class);
        Call<Example> GetMessageCall = get_service.post_comment(image_id, comment, comment_id, user_name);

        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(CommentActivity.this, "Comment Added", duration);
                finish();
                startActivity(getIntent());
                toast.show();
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }

        });

    }
    public interface PostService {
        @GET("default/post_comment")
        Call<Example> post_comment(@Query("image_id") String image_id,
                                   @Query("comment") String comment,
                                   @Query("comment_id") String comment_id,
                                   @Query("user_name") String user_name);

    }


    public void DeleteComment(String comment_id){
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

        DeleteService get_service = retrofit.create(DeleteService.class);
        Call<Example> GetMessageCall = get_service.get_comment(comment_id);

        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                Log.i(LOG_TAG, "Comment deleted");
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(CommentActivity.this, "Comment Deleted", duration);
                toast.show();


            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
        finish();
        startActivity(getIntent());

    }

    public interface DeleteService {
        @GET("default/delete_comment")
        Call<Example> get_comment(@Query("comment_id") String comment_id);
    }

}
