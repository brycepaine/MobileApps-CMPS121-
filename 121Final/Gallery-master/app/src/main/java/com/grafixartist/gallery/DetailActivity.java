package com.grafixartist.gallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.grafixartist.gallery.adapter.CommentAdapter;
import com.grafixartist.gallery.adapter.DepthPageTransformer;
import com.grafixartist.gallery.adapter.SectionsPagerAdapter;
import com.grafixartist.gallery.response.CommentResult;
import com.grafixartist.gallery.response.Example;

import com.grafixartist.gallery.response.ImageURLResponse;
import com.grafixartist.gallery.response.ImageResult;

import java.util.ArrayList;
import java.util.List;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public ArrayList<ImageModel> data = new ArrayList<>();
    int pos;

    SharedPreferences settings;
    public CommentAdapter aa;
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

    String list_image_id;
    String list_description;
    String getList_user_name;

    Toolbar toolbar;
    ArrayList<CommentElement> aList;
    public static String LOG_TAG = "ProfileApplication";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public interface GetService {
        @GET("default/get_comments")
        Call<Example> get_comment(@Query("image_id") String image_id);
    }

    public interface UserPicService{
        @GET("default/user_images")
        Call<ImageURLResponse> get_user_pic(@Query("user_name") String user_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.i("DetailActivity", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        //data = getIntent().getParcelableArrayListExtra("data");
        //pos = getIntent().getIntExtra("pos", 0);
        pos = 0;

        user_name = getIntent().getStringExtra("user_name");

        getUserPic(user_name);

        setTitle(user_name);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //noinspection ConstantConditions
                setTitle(user_name);
//                aList.clear();
//                refreshComments(data.get(position).getImageID());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//       // if (id == R.id.action_settings) {
//       //     return true;
//       // }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void getUserPic(final String user_name){

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

        UserPicService userpic_service = retrofit.create(UserPicService.class);
        Call<ImageURLResponse> GetUserPicCall = userpic_service.get_user_pic(user_name);

        //Call retrofit asynchronously
        GetUserPicCall.enqueue(new Callback<ImageURLResponse>() {
            @Override
            public void onResponse(Response<ImageURLResponse> response) {

                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }
                ArrayList<ImageResult> imageInfo = new ArrayList<ImageResult>(response.body().getImageResult());
                for (int i = 0; i < imageInfo.size(); i++) {


                    ImageResult res = imageInfo.get(i);
                    //if image is within proximity to user's location, then display it here
                    ImageModel imageModel = new ImageModel();
                    imageModel.setUserName(user_name);
                    imageModel.setUrl("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");
                    imageModel.setDescription(res.getDescription());
                    imageModel.setImageID(res.getImageId());

                    Log.i("OnResponse", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + imageModel.getUrl());
                    Log.i(LOG_TAG, "URL: " + imageModel.getUrl() + "  description: " + res.getDescription());

                    Log.i("URL : ", imageModel.getUrl());
                    data.add(imageModel);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }

        });
    }
}
