package com.paine.nativeApp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paine.nativeApp.adapter.PmAdapter;
import com.paine.nativeApp.adapter.PmConversationAdapter;
import com.paine.nativeApp.models.ImageModel;
import com.paine.nativeApp.models.PmModel;
import com.paine.nativeApp.response.BasicResponse;
import com.paine.nativeApp.response.PmResponse;
import com.paine.nativeApp.response.PmResult;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

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
    private String messageBody;
    private String pm_id;
    private MessageService.MessageServiceInterface messageService;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageClientListener messageClientListener = new MyMessageClientListener();
    private String profile_pic;




    private EditText thePm;
    private Button sendButton;


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pm_conversation);
        bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG,"back clicked");
                onBackPressed();
            }
        });



        Intent i = getIntent();
        user_to = i.getStringExtra("user_name");

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);
        profile_pic = settings.getString("user_profile",null);

        getMessages();

        Log.i(LOG_TAG, "user_name user_to" + user_name + user_to);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//this updates the users location and then refreshes the images


        if (id == R.id.home){
            Log.i(LOG_TAG, "options home");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user_name", user_name);
            startActivity(intent);
        }





        if (id == R.id.signout){
            SharedPreferences.Editor e = settings.edit();
            e.remove("user_name");
            e.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_2);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        thePm = (EditText) findViewById(R.id.messageBodyField);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(v);
            }
        });



    }

    private void send(View view){
        Log.i(LOG_TAG, "sending message " + thePm.getText().toString() + user_to);
        messageBody = thePm.getText().toString();
        Log.i(LOG_TAG, "mesageservice" + messageService);
        messageService.sendMessage(user_to, messageBody);
        SecureRandomString srs = new SecureRandomString();
        pm_id = srs.nextString();


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
                    service.sendPm( user_to, user_name, messageBody, pm_id);

            //Call retrofit asynchronously
            queryResponseCall.enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Response<BasicResponse> response) {
                    Log.i(LOG_TAG, "Code is: " + response.code());

//                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(i);



//                Log.i(LOG_TAG, "The result is: " + response.body().response);
//                if (response.body().response.equals("ok")) {
//                    Log.i(LOG_TAG,"upload succesfull");

                }

                @Override
                public void onFailure(Throwable t) {
                    // Log error here since request failed
                }
            });

            thePm.setText("");

    }
    @Override
    public void onDestroy() {
        messageService.removeMessageClientListener(messageClientListener);
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private void getMessages(){
        Log.i(LOG_TAG,"getMessages");
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
                    userImageModel.setProf("http://imagegallery.netai.net/pictures/" + res.getProf() + ".JPG");
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

        Log.i(LOG_TAG, "loadconversation");
        progressBar.setVisibility(View.GONE);


        adapter = new PmConversationAdapter(getApplicationContext(), mydata, user_name);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mydata.size() - 1);



    }

    public interface PmConversationService {
        @GET("default/get_pm_conversation")
        Call<PmResponse> getURL(@Query("user_name") String user_name,
                                @Query("user_to") String user_to);



    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(LOG_TAG,"onserviceConnected");
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(PmConversationActivity.this, "Message failed to send.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onIncomingMessage(MessageClient client, final Message message) {
            Log.i(LOG_TAG, "Onincoming Message");
            Log.i(LOG_TAG, "senderid: " + message.getSenderId() + "  user_to:" + user_to);
            if (message.getSenderId().equals((user_to))){
                PmModel userImageModel = new PmModel();
                userImageModel.setPm(message.getTextBody());
                userImageModel.setUserName(user_name);
                userImageModel.setTimeago("Now");


                mydata.add(userImageModel);
                adapter = new PmConversationAdapter(getApplicationContext(), mydata, user_name);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.scrollToPosition(mydata.size() - 1);


            }
//            SecureRandomString srs = new SecureRandomString();
//            pm_id = srs.nextString();
//            if (message.getSenderId().equals(user_to)) {
//                final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
//
//
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                // set your desired log level
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                OkHttpClient httpClient = new OkHttpClient.Builder()
//                        .addInterceptor(logging)
//                        .build();
//
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://empirical-realm-123103.appspot.com/pictureApp/")
//                        .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
//                        .client(httpClient)	//add logging
//                        .build();
//
//                SendPmService service = retrofit.create(SendPmService.class);
//
//                Call<BasicResponse> queryResponseCall =
//                        service.sendPm( user_to, user_name, messageBody, pm_id);
//
//                //Call retrofit asynchronously
//                queryResponseCall.enqueue(new Callback<BasicResponse>() {
//                    @Override
//                    public void onResponse(Response<BasicResponse> response) {
//                        Log.i(LOG_TAG, "Code is: " + response.code());
//
////                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
////                        startActivity(i);
//
//
//
////                Log.i(LOG_TAG, "The result is: " + response.body().response);
////                if (response.body().response.equals("ok")) {
////                    Log.i(LOG_TAG,"upload succesfull");
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        // Log error here since request failed
//                    }
//                });
//        }

//                //only add message to parse database if it doesn't already exist there
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
//                query.whereEqualTo("sinchId", message.getMessageId());
//                query.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> messageList, com.parse.ParseException e) {
//                        if (e == null) {
//                            if (messageList.size() == 0) {
//                                ParseObject parseMessage = new ParseObject("ParseMessage");
//                                parseMessage.put("senderId", currentUserId);
//                                parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
//                                parseMessage.put("messageText", writableMessage.getTextBody());
//                                parseMessage.put("sinchId", message.getMessageId());
//                                parseMessage.saveInBackground();
//
//                                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
//                            }
//                        }
//                    }
//                });

        }



        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {

            Log.i(LOG_TAG,"onMessageSent");



//            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            PmModel userImageModel = new PmModel();
////                    imageModel.setUserName(res.getUserName());
////                    userImageModel.setImage("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");
//
            userImageModel.setPm(message.getTextBody());
            userImageModel.setUserName(user_name);
            Log.i(LOG_TAG, "on message sent profile " + profile_pic);
            userImageModel.setProf("http://imagegallery.netai.net/pictures/" + profile_pic + ".JPG");
            userImageModel.setTimeago("Now");
////                    imageModel.setTimestamp(res.getTimestamp().toString());
////                    imageModel.setDistance(res.getDistance());
////                    imageModel.setTimeago(res.getTimeago());
////                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");
//
//
            mydata.add(userImageModel);
            adapter = new PmConversationAdapter(getApplicationContext(), mydata, user_name);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.scrollToPosition(mydata.size() - 1);

        }

        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}
    }

    //upload image_id, comment_id, user_name, lat, lng to server
    public interface SendPmService {
        @GET("default/send_pm")
        Call<BasicResponse> sendPm( @Query("to_user") String to_user,
                                    @Query("user_name") String user_name,
                                    @Query("pm") String pm,
                                    @Query("pm_id") String pm_id);
    }
}
