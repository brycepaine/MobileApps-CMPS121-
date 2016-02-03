package com.paine.dr.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import com.paine.dr.weather.response.Example;
import com.paine.dr.weather.response.Conditions;
import com.paine.dr.weather.response.ObservationLocation;



import java.util.List;


public class MainActivity extends AppCompatActivity {

    Conditions cond = new Conditions();
    ObservationLocation loc = new ObservationLocation();
    public static String LOG_TAG = "MyApplication";
    public static final String FRIEND_IDX = "friend_idx";

//USES LISTVIEW AS SHOWN IN LECTURE
    private class ListElement {
        ListElement() {};
        ListElement(String tl, String t2) {
            textLabel = tl;
            textLabel2 = t2;
        }
        public String textLabel;
        public String textLabel2;
    }

    private ArrayList<ListElement> aList;

    private class MyAdapter extends ArrayAdapter<ListElement> {
        int resource;
        Context context;
        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
            this.context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            ListElement w = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view.
            TextView tv = (TextView) newView.findViewById(R.id.itemText2);
            TextView tv2 = (TextView) newView.findViewById(R.id.itemText);

            tv.setText(w.textLabel);
            tv2.setText(w.textLabel2);


            newView.setTag(w.textLabel);
            newView.setTag(w.textLabel2);


            return newView;
        }
    }

    private MyAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.list_element, aList);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
    }

    //if error, clear list and set error to first element
    public void callError(){
        aList.clear();
        ListElement e = new ListElement();
        e.textLabel = "ERROR--COULD NOT ACCESS WEATHER";
        aList.add(e);
        aa.notifyDataSetChanged();
    }
// if no error, set corresponding weather/location info to new list elements
    public void changeVal(Conditions conds, ObservationLocation obs){
        aList.clear();
        ListElement city = new ListElement();
        city.textLabel = obs.city;
        city.textLabel2 = "City";
        aList.add(city);

        ListElement elev = new ListElement();
        elev.textLabel = obs.elevation;
        elev.textLabel2 = "Elevation";
        aList.add(elev);

        ListElement temp = new ListElement();
        temp.textLabel = conds.tempF.toString();
        temp.textLabel2 = "Temp(F)";
        aList.add(temp);

        ListElement hum = new ListElement();
        hum.textLabel = conds.relativeHumidity;
        hum.textLabel2 = "Humidity";
        aList.add(hum);

        ListElement avg = new ListElement();
        avg.textLabel = conds.windMph.toString();
        avg.textLabel2 = "Winds";
        aList.add(avg);

        ListElement gusts = new ListElement();
        gusts.textLabel = conds.windGustMph.toString();
        gusts.textLabel2 = "Wind Gust";
        aList.add(gusts);

        aa.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */

    public interface NicknameService {

        @GET("default/get_weather/")
        Call<Example> registerUser();


    }

    public void clickRefresh (View v) {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://luca-teaching.appspot.com/weather/")
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        NicknameService service = retrofit.create(NicknameService.class);

        Call<Example> queryResponseCall =
                service.registerUser();

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                if(response.code() == 500 || response.body().response.result.equals("error")){
                    callError();
                }
                else {
                    //Log.i(LOG_TAG, "Code is: " + response.code());
                    //Log.i(LOG_TAG, "The result is: " + response.response);
                    loc = response.body().response.conditions.observationLocation;
                    cond = response.body().response.conditions;
                    changeVal(cond, loc);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

    }

}
