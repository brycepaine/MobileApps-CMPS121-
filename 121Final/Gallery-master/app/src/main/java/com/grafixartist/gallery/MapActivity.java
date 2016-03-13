package com.grafixartist.gallery;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grafixartist.gallery.response.CommentResult;
import com.grafixartist.gallery.response.Example;

import java.util.ArrayList;
import java.util.List;

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
 *
 * Created by thomasburch on 3/11/16.
 */
public class MapActivity extends Activity{

    GoogleMap map;
    String my_lat;
    String my_lng;
    Double my_latitude;
    Double my_longitude;
    SharedPreferences settings;
    String image_id;

    public interface GetLocation {
        @GET("default/get_comments")
        Call<Example> get_location(@Query("image_id") String image_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        image_id = getIntent().getStringExtra("image_id");

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        my_lat = settings.getString("lat", null);
        my_lng = settings.getString("lng", null);

        my_latitude = Double.parseDouble(my_lat);
        my_longitude = Double.parseDouble(my_lng);

        final LatLng LOCATION_ME= new LatLng(my_latitude, my_longitude);

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

        GetLocation get_service = retrofit.create(GetLocation.class);
        Call<Example> GetMessageCall = get_service.get_location(image_id);

        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {


                //final LatLng LOCATION_PHOTO = new LatLng(photo_lat, photo_lng);

            }
            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }

        });




        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        map.addMarker((new MarkerOptions().position(LOCATION_ME).title("You!")));
        //map.addMarker((new MarkerOptions().position(LOCATION_PHOTO).title("Photo!")));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_ME, 16);
        map.animateCamera(update);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

}
