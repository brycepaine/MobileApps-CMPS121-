package com.grafixartist.gallery.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;


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

/**
 * Created by thomasburch on 3/10/16.
 */
public class PopCommentDelete extends Activity{

    public interface GetService {
        @GET("default/delete_comment")
        Call<Example> get_comment(@Query("comment_id") String comment_id);
    }

    String description;
    String image_id;
    String comment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_comment_delete);

        description = getIntent().getStringExtra("description");
        image_id = getIntent().getStringExtra("image_id");
        comment_id = getIntent().getStringExtra("comment_id");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .6), (int) (height * .2));

        Button yes_delete = (Button) findViewById(R.id.yes_delete);
        yes_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Call<Example> GetMessageCall = get_service.get_comment(comment_id);

                //Call retrofit asynchronously
                GetMessageCall.enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Response<Example> response) {
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(PopCommentDelete.this, "Comment Deleted", duration);
                        toast.show();

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Log error here since request failed
                    }

                });

                Intent intent = new Intent(PopCommentDelete.this, CommentActivity.class);
                intent.putExtra("image_id", image_id);
                startActivity(intent);
                PopCommentDelete.this.finish();
            }
        });


        Button no_delete = (Button) findViewById(R.id.no_delete);
        no_delete.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                PopCommentDelete.this.finish();
            }
        });

    }

}
