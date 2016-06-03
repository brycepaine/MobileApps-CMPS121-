package com.paine.nativeApp;

/**
 * Created by bryce on 5/12/2016.
 */
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;



import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.Spinner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//shobit find rest
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.paine.nativeApp.models.ImageModel;
import com.paine.nativeApp.response.Example;
import com.paine.nativeApp.response.ImageResult;
import com.paine.nativeApp.response.ImageURLResponse;
import com.paine.nativeApp.adapter.GalleryAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
public class MainFragment extends Fragment {
    private static String LOG_TAG = "MyApplication";

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PIC_REQUEST = 100;
    private String user_profile_pic;

    private ArrayList<ImageModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;
    private boolean refresh;
    private LocationData locationData;
    private String lng;
    private String lat;
    private String user_name;
    private Integer radius;
    private Integer theposition;
    private Uri imageUri;
    private SharedPreferences settings;
    private HashMap<String, Integer> spinnerHash = new HashMap<String, Integer>();
    private Intent serviceIntent;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem mSpinnerItem = menu.findItem(R.id.spinner);
        setHasOptionsMenu(true);

        //this is for the spinner mile radius.  it defaults to shared preference value
        //or 5 if nothing has been saved
        View view = mSpinnerItem.getActionView();
        if (view instanceof Spinner)
        {
            Spinner spinner = (Spinner) view;
            spinner.setAdapter( ArrayAdapter.createFromResource( getActivity(),
                    R.array.spinner_data,
                    android.R.layout.simple_spinner_dropdown_item ) );

            settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

            radius = settings.getInt("spinner", 5);
            theposition = spinnerHash.get(radius + " Miles");
            Log.i(LOG_TAG, "spinner hash position" + theposition + " radius:" + radius);
            spinner.setSelection(theposition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(LOG_TAG, "spinner pos " + position);

                    switch (position) {
                        case 0:
                            radius = 5;
                            break;
                        case 1:
                            radius = 10;
                            break;
                        case 2:
                            radius = 30;
                            break;
                        case 3:
                            radius = 50;
                            break;
                        case 4:
                            radius= 100;
                            break;
                    }
                    Log.i(LOG_TAG, "radius " + radius);
                    SharedPreferences.Editor e = settings.edit();
                    e.putInt("spinner", radius);
                    e.commit();
                    getImageURLs();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//this updates the users location and then refreshes the images
        if(id == R.id.refresh){
            refresh = true;
            SharedPreferences.Editor e = settings.edit();
            e.putString("lat", null);
            e.putString("lng", null);
            e.commit();
            Log.i(LOG_TAG, "lat after refresh should be null" + settings.getString("lat", null));
            adapter.clearData();
            progressBar.setVisibility(View.VISIBLE);
            requestLocationUpdate();

        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    // for taking picture
    private void clickpic() {
        // Check Camera
//        Log.i(LOG_TAG, "click pick");
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            // start the image capture Intent
            startActivityForResult(intent, TAKE_PIC_REQUEST);

        } else {
            Toast.makeText(getActivity(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }

    @Override
    public void onResume(){
        Log.i(LOG_TAG, "on resume");
//        Log.i(LOG_TAG, "refresh on resume " + refresh);
        serviceIntent = new Intent(getActivity(), MessageService.class);
        getActivity().startService(serviceIntent);


        if(!refresh) {
            locationData = LocationData.getLocationData();
        }
//     initialize refresh to false
        refresh = false;

//        shared preferences to get longitude and latitude and username if they exist
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lat = settings.getString("lat", null);
        lng = settings.getString("lng", null);
        user_name = settings.getString("user_name", null);
        Log.i(LOG_TAG, "lat lng on resume" + lat + lng);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        radius = settings.getInt("spinner",5);

//        Log.i(LOG_TAG, "on create user name: " + user_name + " lat: " + lat + lng);

//        if  user name is null sign in
        if (user_name == null) {
            Log.i(LOG_TAG,"switching to login intent");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

//        if long or lat is null get location
        else if (lat == null && lng == null){
            Log.i(LOG_TAG, "lat and lng are null");
            requestLocationUpdate();
//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);
        }
        else {
//            user is signed in and has location, get the images
//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);
            requestLocationUpdate();
            getImageURLs();
        }

        super.onResume();

    }

    public void onPause(){
        Log.i(LOG_TAG, "on pause");
        removeLocationUpdate();
        refresh = false;

//        save long and lat
        if(lat != null && lng != null && !refresh) {
            settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor e = settings.edit();
            e.putString("lat", lat);
            e.putString("lng", lng);
            e.commit();
        }

//        clear adapter data for memory
        if (adapter != null){
            adapter.clearData();

        }

//        Log.i(LOG_TAG, "onPause lat lng: " + lat + lng);
        super.onPause();
    }

    private void loadImages(){
        Log.i(LOG_TAG, "load images");

        // urls have been called, end profressbar
        progressBar.setVisibility(View.GONE);

        //send data to adapter
        adapter = new GalleryAdapter(getActivity(), mydata);
        mRecyclerView.setAdapter(adapter);

    }

    public void getImageURLs(){

        removeLocationUpdate();
//        Log.i(LOG_TAG, "get image urls");
        getActivity().setProgressBarIndeterminateVisibility(true);


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

        ImageURLService service = retrofit.create(ImageURLService.class);

        Log.i(LOG_TAG, "the user_name is: " + user_name + "RADIUS" + radius);
        Call<ImageURLResponse> queryResponseCall =
                service.getURL(lat, lng, user_name, radius);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<ImageURLResponse>() {
            @Override
            public void onResponse(Response<ImageURLResponse> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }
                ArrayList<ImageResult> imageInfo = new ArrayList<ImageResult>(response.body().getImageResult());
                for (int i = imageInfo.size() - 1; i >= 0; i--) {
                    ImageResult res = imageInfo.get(i);
                    ImageModel imageModel = new ImageModel();
                    imageModel.setUserName(res.getUserName());
                    imageModel.setUrl("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");
                    imageModel.setDescription(res.getDescription());
                    imageModel.setImageID(res.getImageId());
                    imageModel.setDistance(res.getDistance());
                    imageModel.setTimeago(res.getTimeago());
                    imageModel.setVotes(res.getVoteCount());
                    imageModel.setUserVote(res.getUserVote());
                    imageModel.setProfile("http://imagegallery.netai.net/pictures/" + res.getProfPic() + ".JPG");
                    Log.i(LOG_TAG, "image info list at :" + res + " username: " + res.getUserName()
                            + " URL : " + imageModel.getUrl() + "  description  " + res.getDescription());

                    mydata.add(imageModel);
                }
                loadImages();
            }


            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Log.i(LOG_TAG, "throwable t: " + t);
            }
        });
    }

    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */
    public interface ImageURLService {
        @GET("default/get_images")
        Call<ImageURLResponse> getURL(@Query("lat") String lat,
                                      @Query("lng") String lng,
                                      @Query("user_name") String user_name,
                                      @Query("radius")  Integer radius);
    }

    /*
Request location uplate. This must be called in onResume if the user has allowed location sharing
*/
    public void requestLocationUpdate(){
        Log.i(LOG_TAG, "requesting update");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

                Log.i(LOG_TAG, "requesting location update");
            }
            else {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.i(LOG_TAG, "please allow to use your location");

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } else{
            Log.i(LOG_TAG, "requesting location update from user");
            //prompt user to enable location
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

                        Log.i(LOG_TAG, "requesting location update");
                    } else{
                        throw new RuntimeException("permission not granted still callback fired");
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*
  Remove location update. This must be called in onPause if the user has allowed location sharing
   */
    private void removeLocationUpdate() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.removeUpdates(locationListener);
                Log.i(LOG_TAG, "removing location update");
            }
        }
    }

    /**
     * Listens to the location, and gets the most precise recent location.
     * Copied from Prof. Luca class code
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(LOG_TAG, "on location changed");
            Location lastLocation = locationData.getLocation();

            // Do something with the location you receive.
            double newAccuracy = location.getAccuracy();

            long newTime = location.getTime();
            // Is this better than what we had?  We allow a bit of degradation in time.
            boolean isBetter = ((lastLocation == null) ||
                    newAccuracy < lastLocation.getAccuracy() + (newTime - lastLocation.getTime()));
            if (isBetter) {
                // We replace the old estimate by this one.
                locationData.setLocation(location);


                Log.i(LOG_TAG, "lat" + Double.toString(locationData.getLocation().getLatitude()));
                lat = Double.toString(locationData.getLocation().getLatitude());
                lng = Double.toString(locationData.getLocation().getLongitude());

                if(!refresh){
                    Log.i(LOG_TAG, "Not REFRESH");
                }else Log.i(LOG_TAG, "REFRESH");
                getImageURLs();


//                //Now we have the location.
//                Button searchButton = (Button) findViewById(R.id.searchButton);
//                if(checkLocationAllowed())
//                    searchButton.setEnabled(true);//We must enable search button
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    public interface UpvoteService {
        @GET("default/upvote")
        Call<Example> upvote(@Query("user_name") String user_name,
                             @Query("image_id" ) String image_id);
    }

    public interface DownvoteService {
        @GET("default/downvote")
        Call<Example> upvote(@Query("user_name") String user_name,
                             @Query("image_id" ) String image_id);
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), MessageService.class));
        super.onDestroy();
    }

    public void Vote(String image_id, String vote) {
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

        Call<Example> GetMessageCall;

        Log.i(LOG_TAG, "the vote in main is " + vote);

        if (vote.equals("up")) {
            MainFragment.UpvoteService get_service = retrofit.create(MainFragment.UpvoteService.class);
            GetMessageCall = get_service.upvote(user_name, image_id);
            Log.i(LOG_TAG, "upvote in user");
        } else{
            MainFragment.DownvoteService get_service = retrofit.create(MainFragment.DownvoteService.class);
            GetMessageCall = get_service.upvote(user_name, image_id);
            Log.i(LOG_TAG, "downvote in user");
        }
        //Call retrofit asynchronously
        GetMessageCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response) {
                Log.i(LOG_TAG, "upvoted");

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
    }

}
