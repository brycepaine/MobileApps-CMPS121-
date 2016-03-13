package com.grafixartist.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.grafixartist.gallery.adapter.CommentAdapter;
import com.grafixartist.gallery.popup.PopCommentAdd;
import com.grafixartist.gallery.popup.PopPhotoDelete;
import com.grafixartist.gallery.response.Example;
import com.grafixartist.gallery.response.CommentResult;

import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * Created by bryce on 3/10/2016.
 */
/**
 * Created by thomasburch on 3/10/16.
 */


public class CommentActivity extends AppCompatActivity {

    SharedPreferences settings;
    public CommentAdapter aa;
    ArrayList<CommentElement> aList;
    String user_name;
    String image_id;
    String comment_id;
    String message;
    ProgressBar spinner;
    EditText comment;

    String list_comment;
    String list_timestamp;
    String list_comment_id;
    String list_user_name;
    String list_profile_pic;

    Button send;
    ImageButton edit;
    ImageButton delete;
    Toolbar toolbar;

    public static String LOG_TAG = "CommentApplication";
    //private String image_id;


    public interface GetService {
        @GET("default/get_comments")
        Call<Example> get_comment(@Query("image_id") String image_id);
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

        if (id == R.id.signout){
            SharedPreferences.Editor e = settings.edit();
            e.remove("user_name");
            e.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.homepage){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.mappage){
            Intent intent = new Intent(CommentActivity.this, MapActivity.class);
            intent.putExtra("image_id", image_id);
            startActivity(intent);
        }
        if (id == R.id.delete_photo){
            Intent intent = new Intent(this, PopPhotoDelete.class);
            intent.putExtra("image_id", image_id);
            startActivity(intent);
        }
        if (id == R.id.comment){
            Intent intent = new Intent(this, PopCommentAdd.class);
            intent.putExtra("image_id", image_id);
            startActivity(intent);


        }
        if (id == R.id.refresh){
            refreshComments(image_id);
            aa.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Log.i(LOG_TAG, "onCreate Comment Activity");
        //spinner = (ProgressBar)findViewById(R.id.progressBar1);

        image_id = getIntent().getStringExtra("image_id");
        Log.i(LOG_TAG, "image_id" + image_id);

        ImageView imageView = (ImageView) findViewById(R.id.comment_image);

        Glide.with(this)
                .load("http://imagegallery.netai.net/pictures/" + image_id + ".JPG")
                .into(imageView);

        toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onResume(){

        ImageView imageView = (ImageView) findViewById(R.id.comment_image);

        Glide.with(this)
                .load("http://imagegallery.netai.net/pictures/" + image_id + ".JPG")
                .into(imageView);

        //initialize adapter
        aList = new ArrayList<CommentElement>();
        aa = new CommentAdapter(this, R.layout.list_element, aList, user_name, image_id);// , lat, lng);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);

//            //ListView lv = (ListView) findViewById(R.id.layout_lv);
//            myListView.setOnTouchListener(new OnTouchListener() {
//                // Setting on Touch Listener for handling the touch inside ScrollView
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    // Disallow the touch request for parent scroll on touch of child view
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
//                    return false;
//                }
//            });

        refreshComments(image_id);//refresh chats
        aa.notifyDataSetChanged();
        super.onResume();
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

                //parse the response for list of messages
                List<CommentResult> Response_resultList;

                if(response.body().equals(null)){
                    aList.clear();
                    aList.add(new CommentElement("", "No Results", "", "", "", ""));

                }else {

                    if (response.body().response.equals("ok")) {

                        Log.i(LOG_TAG, "Result: ok");
                        Response_resultList = response.body().getCommentResult();
                    } else {
                        Response_resultList = new ArrayList<CommentResult>();
                    }

                    aList.clear();
                    for (int i = Response_resultList.size() - 1; i >= 0; i--) {

                        list_comment = Response_resultList.get(i).getComment();
                        list_timestamp = Response_resultList.get(i).getTimestamp();
                        list_user_name = (Response_resultList.get(i).getUserName());
                        list_comment_id = (Response_resultList.get(i).getCommentId());
                        list_profile_pic = Response_resultList.get(i).getProfilePic();

                        aList.add(new CommentElement(list_timestamp, image_id,
                                list_user_name, list_comment, list_comment_id, list_profile_pic));

                    }

                    if (Response_resultList.size() == 0) {
                        aList.clear();
                        aList.add(new CommentElement("", "No Results", "", "", "", ""));
                    }
                }
                //spinner.setVisibility(View.GONE);
                // We notify the ArrayList adapter that the underlying list has changed,
                // triggering a re-rendering of the list.
                aa.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }

        });
    }
}
