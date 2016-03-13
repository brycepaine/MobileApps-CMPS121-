package com.grafixartist.gallery.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.grafixartist.gallery.MainActivity;
import com.grafixartist.gallery.R;
import com.grafixartist.gallery.response.Example;

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
 * Created by thomasburch on 3/10/16.
 */
public class PopPhotoDelete extends Activity{

    public interface DeleteService {
        @GET("default/delete_photo")
        Call<Example> delete_photo(@Query("image_id") String image_id);

    }

    String image_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_photo_delete);

        image_id = getIntent().getStringExtra("image_id");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .6), (int) (height * .2));

        Button yes_delete = (Button) findViewById(R.id.yesdelete);
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
                        .baseUrl("http://empirical-realm-123103.appspot.com/pictureApp/")	//We are using Foursquare API to get data
                        .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                        .client(httpClient)	//add logging
                        .build();

                DeleteService get_service = retrofit.create(DeleteService.class);
                Call<Example> GetMessageCall = get_service.delete_photo(image_id);

                //Call retrofit asynchronously
                GetMessageCall.enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Response<Example> response) {

                    }
                    @Override
                    public void onFailure(Throwable t) {
                        // Log error here since request failed
                    }

                });

                //Delete Photo

                Intent intent = new Intent(PopPhotoDelete.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button no_delete = (Button) findViewById(R.id.nodelete);
        no_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopPhotoDelete.this.finish();
            }
        });


    }

}
