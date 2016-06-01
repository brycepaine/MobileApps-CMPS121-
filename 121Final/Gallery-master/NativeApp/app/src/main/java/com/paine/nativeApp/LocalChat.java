package com.paine.nativeApp;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.paine.nativeApp.response.ChatUsersResponse;
import com.paine.nativeApp.response.ImageURLResponse;
import com.paine.nativeApp.response.PmResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
 * Created by bryce on 5/21/2016.
 */
public class LocalChat extends ActionBarActivity implements View.OnClickListener,
        MessageDataService.MessagesCallbacks{

public static final String USER_EXTRA = "USER";

    public static final String TAG = "ChatActivity";
    private final String LOG_TAG = "MyApplication";
    List<String> ids;

    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private String mRecipient;
    private ListView mListView;
    private String user_name;
    private String lat;
    private String lng;
    private Date mLastMessageDate = new Date();
    private String mConvoId;
    private MessageDataService.MessagesListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_chat);

        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        user_name = getIntent().getStringExtra("user_name");

        Log.i(LOG_TAG, " lat " + lat  + "  lnag" + lng  +"name " + user_name);
        getUsers();

        mRecipient = "Ashok";

        mListView = (ListView) findViewById(R.id.messages_list);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);

        setTitle(mRecipient);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button sendMessage = (Button)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

//        String[] ids = {"Ajay","-", "Ashok"};
//        Arrays.sort(ids);
//        mConvoId = ids[0]+ids[1]+ids[2];

//        mListener = MessageDataService.addMessagesListener(mConvoId, this);

    }

    private void setListener(){
        mListener = MessageDataService.addMessagesListener(mConvoId, this);

    }

    public void onClick(View v) {
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
//        Message msg = new Message();
//        msg.setDate(new Date());
//        msg.setText(newMessage);
//        msg.setSender(user_name);
//        Log.i(LOG_TAG, "msg sender " + msg.getSender() + " message text  " + msg.getText() + "convoid " + mConvoId);

        int i = 0;
        while(i < ids.size()){
            Message msg = new Message();
            msg.setDate(new Date());
            msg.setText(newMessage);
            msg.setSender(user_name);
            MessageDataService.saveMessage(msg, user_name + ids.get(i));

        }


    }

    @Override
    public void onMessageAdded(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }

    public interface GetUsersService {
        @GET("default/get_local_chat_users")
        Call<ChatUsersResponse> getURL(@Query("lat") String lat,
                                      @Query("lng") String lng,
                                      @Query("user_name") String user_name);
    }

    private void getUsers(){
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

        GetUsersService service = retrofit.create(GetUsersService.class);
        Call<ChatUsersResponse> queryResponseCall =
                service.getURL(lat,lng,user_name);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<ChatUsersResponse>() {
            @Override
            public void onResponse(Response<ChatUsersResponse> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }

                Log.i(LOG_TAG, "Code is: " + response.code());
                ids = response.body().getChatResult();
                Collections.sort(ids);
                for (int i = 0; i < ids.size(); i++){
                    mConvoId += ids.get(i);
                    Log.i(LOG_TAG, "username list " + ids.get(i));
                }
                Log.i(LOG_TAG, "response " + response.body().getChatResult());
                setListener();
            }


            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Log.i(LOG_TAG, "throwable t: " + t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageDataService.stop(mListener);
    }


    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(LocalChat.this, R.layout.message, R.id.message, messages);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender().equals("Ashok")){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_gray));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);


            return convertView;
        }
    }
}
