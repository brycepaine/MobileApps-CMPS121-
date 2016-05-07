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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paine.nativeApp.adapter.PmAdapter;
import com.paine.nativeApp.adapter.PmConversationAdapter;
import com.paine.nativeApp.models.ImageModel;
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
 * Created by bryce on 5/5/2016.
 */
public class PmConversationActivity extends AppCompatActivity{
    private static String LOG_TAG = "MyApplication";

    private ArrayList<PmModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PmConversationAdapter adapter;
    private ProgressBar progressBar;
    private String user_name;
    private SharedPreferences settings;
    private String user_to;

    private EditText thePm;
    private Button sendButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pm_conversation);
        Intent i = getIntent();
        user_to = i.getStringExtra("user_name");

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);

        getMessages();

        Log.i(LOG_TAG, "user_name user_to" + user_name + user_to);
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thePm = (EditText) findViewById(R.id.messageBodyField);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send(v);
            }
        });



    }

    private void send(View view){
        Log.i(LOG_TAG, "sending message " + thePm.getText().toString());

        thePm.setText("");

    }

    private void getMessages(){
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

        PmConversationService service = retrofit.create(PmConversationService.class);
        Log.i(LOG_TAG,"userfrom   user to"+ user_name+user_to);
        Call<PmResponse> queryResponseCall =
                service.getURL(user_name, user_to);

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
//                    imageModel.setTimestamp(res.getTimestamp().toString());
//                    imageModel.setDistance(res.getDistance());
//                    imageModel.setTimeago(res.getTimeago());
//                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");


                    mydata.add(userImageModel);

                    Log.i(LOG_TAG, "the data is " + mydata);
                }

                loadConversation();



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

    private void loadConversation(){
        Log.i(LOG_TAG, "loadimages");
        progressBar.setVisibility(View.GONE);


        adapter = new PmConversationAdapter(getApplicationContext(), mydata, user_name);
        mRecyclerView.setAdapter(adapter);


    }

    public interface PmConversationService {
        @GET("default/get_pm_conversation")
        Call<PmResponse> getURL(@Query("user_name") String user_name,
                                @Query("user_to") String user_to);



    }
}
