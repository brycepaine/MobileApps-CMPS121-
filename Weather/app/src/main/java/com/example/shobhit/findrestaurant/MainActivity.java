package com.example.shobhit.findrestaurant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shobhit.findrestaurant.response.RegistrationResponse;

import java.util.List;
import java.util.prefs.Preferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

	private String user_id;

	public static String LOG_TAG = "MyApplication";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Gets the settings, and creates a random user id if missing.
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		user_id = settings.getString("user_id", null);
		if (user_id == null) {
			// Creates a random one, and sets it.
			SecureRandomString srs = new SecureRandomString();
			user_id = srs.nextString();
			SharedPreferences.Editor e = settings.edit();
			e.putString("user_id", user_id);
			e.commit();
		}
        // Let's mock acquiring the nickname.
        String nickname = "Peter";

        // Let's register the user.
        // In truth, it may be better to keep a flag in preferences that tells us
        // whether we have already registered?

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://luca-teaching.appspot.com/localchat/")
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        NicknameService service = retrofit.create(NicknameService.class);

        Call<RegistrationResponse> queryResponseCall =
                service.registerUser(user_id, nickname);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Response<RegistrationResponse> response) {
                Log.i(LOG_TAG, "Code is: " + response.code());
                Log.i(LOG_TAG, "The result is: " + response.body().response);
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

    }

	@Override
	public void onResume(){
		super.onResume();
	}


    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */
    public interface NicknameService {
        @GET("default/register_user")
        Call<RegistrationResponse> registerUser(@Query("user_id") String user_id,
                                                @Query("nickname") String nickname);
    }

}
