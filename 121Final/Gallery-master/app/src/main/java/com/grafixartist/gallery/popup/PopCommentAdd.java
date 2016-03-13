package com.grafixartist.gallery.popup;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.grafixartist.gallery.CommentActivity;
import com.grafixartist.gallery.R;
import com.grafixartist.gallery.response.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.math.BigInteger;
import java.security.SecureRandom;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.preference.PreferenceManager;

/**
 * Created by thomasburch on 3/10/16.
 */
public class PopCommentAdd extends Activity{

    public interface GetService {
        @GET("default/post_comment")
        Call<Example> post_comment(@Query("image_id") String image_id,
                                   @Query("comment") String comment,
                                   @Query("comment_id") String comment_id,
                                   @Query("user_name") String user_name);

    }

    public final class SecureRandomString {
        private SecureRandom random = new SecureRandom();
        public String nextString() {
            return new BigInteger(130, random).toString(32);
        }
    }

    String description;
    String image_id;
    String comment_id;
    String comment;
    String user_name;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_comment_edit);

        //description = getIntent().getStringExtra("description");
        image_id = getIntent().getStringExtra("image_id");

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);

        SecureRandomString srs = new SecureRandomString();
        comment_id = srs.nextString();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*1), (int) (height*1));

        ImageView imageView = (ImageView) findViewById(R.id.edit_image);

        Glide.with(this)
                .load("http://imagegallery.netai.net/pictures/" + image_id + ".JPG")
                .into(imageView);

        final EditText edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_comment.setText(description);

        Button edit_send = (Button) findViewById(R.id.edit_send);

        edit_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment = edit_comment.getText().toString();

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

                GetService get_service = retrofit.create(GetService.class);
                Call<Example> GetMessageCall = get_service.post_comment(image_id, comment, comment_id, user_name);

                //Call retrofit asynchronously
                GetMessageCall.enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Response<Example> response) {
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(PopCommentAdd.this, "Comment Added", duration);
                        toast.show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Log error here since request failed
                    }

                });

                Intent intent = new Intent(PopCommentAdd.this, CommentActivity.class);
                intent.putExtra("image_id", image_id);
                startActivity(intent);
                PopCommentAdd.this.finish();

            }
        });
    }

}