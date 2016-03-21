package com.paine.nativeApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paine.nativeApp.response.BasicResponse;

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
public class SendPmActivity extends AppCompatActivity {
    private static String LOG_TAG = "MyApplication";
    Button send;
    EditText thePm;
    private String pm;
    SharedPreferences settings;
    private String user_name;
    private String pm_id;
    private String to_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendpm);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);


        SecureRandomString srs = new SecureRandomString();
        pm_id = srs.nextString();
        Intent intent = getIntent();
        to_user = intent.getStringExtra("user_name");

        thePm = (EditText) findViewById(R.id.editTextsendpm);
        send = (Button) findViewById(R.id.buttonsendpm);


    }

    public void sendPm(View v){

        pm = thePm.getText().toString();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://empirical-realm-123103.appspot.com/pictureApp/")
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        SendPmService service = retrofit.create(SendPmService.class);

        Call<BasicResponse> queryResponseCall =
                service.sendPm( to_user, user_name, pm, pm_id);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Response<BasicResponse> response) {
                Log.i(LOG_TAG, "Code is: " + response.code());

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);


//                Log.i(LOG_TAG, "The result is: " + response.body().response);
//                if (response.body().response.equals("ok")) {
//                    Log.i(LOG_TAG,"upload succesfull");

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
    }

    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */
    //upload image_id, comment_id, user_name, lat, lng to server
    public interface SendPmService {
        @GET("default/send_pm")
        Call<BasicResponse> sendPm( @Query("to_user") String to_user,
                                    @Query("user_name") String user_name,
                                    @Query("pm") String pm,
                                    @Query("pm_id") String pm_id);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
