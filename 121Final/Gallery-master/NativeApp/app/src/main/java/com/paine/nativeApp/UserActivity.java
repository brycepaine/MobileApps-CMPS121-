package com.paine.nativeApp;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paine.nativeApp.models.ImageModel;
import com.paine.nativeApp.response.Example;
import com.paine.nativeApp.response.ImageResult;
import com.paine.nativeApp.response.ImageURLResponse;
import com.paine.nativeApp.adapter.UserGalleryAdapter;

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

public class UserActivity extends AppCompatActivity {
    private static String LOG_TAG = "MyApplication";

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PIC_REQUEST = 100;

    private ArrayList<ImageModel> mydata = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserGalleryAdapter adapter;
    private boolean refresh;
    private LocationData locationData;
    private String lng;
    private String lat;
    private String user_name;
    private Integer radius;
    private Integer theposition;
    private Uri imageUri;
    private String userProfile;
    private SharedPreferences settings;
    private ProgressBar progressBar;
    private ImageView profile_pic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        refresh = false;
//        Log.i(LOG_TAG, "refresh on create " + refresh);


    }


    @Override
    protected void onResume(){
//        Log.i(LOG_TAG, "on resume");
//        Log.i(LOG_TAG, "refresh on resume " + refresh);
        profile_pic = (ImageView)findViewById(R.id.profile_pic);

        Glide.with(UserActivity.this)
                .load("http://imagegallery.netai.net/pictures/" + userProfile + ".JPG")
                .into(profile_pic);

        if(!refresh) {
            locationData = LocationData.getLocationData();
        }
//     initialize refresh to false
        refresh = false;

//        shared preferences to get longitude and latitude and username if they exist
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        lat = settings.getString("lat", null);
        lng = settings.getString("lng", null);
        user_name = settings.getString("user_name", null);

        userProfile = getIntent().getStringExtra("user_name");

//        Log.i(LOG_TAG, "radius on resume " + radius);



        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Log.i(LOG_TAG, "on create user name: " + user_name + " lat: " + lat + lng);

//        if  user name is null sign in
        if (user_name == null) {
            Log.i(LOG_TAG,"switching to login intent");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

//        if long or lat is null get location
        else if (lat == null && lng == null){
            requestLocationUpdate();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        else {
//            user is signed in and has location, get the images
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            getImageURLs();
        }

        super.onResume();

    }
    protected void onPause(){
        Log.i(LOG_TAG, "on pause");
        removeLocationUpdate();
        refresh = false;

//        save long and lat
        if(lat != null && lng != null && !refresh) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
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
        adapter = new UserGalleryAdapter(UserActivity.this, mydata);
        mRecyclerView.setAdapter(adapter);

    }


    //retrofit call to get images and put them into image model
    private void getImageURLs(){

        removeLocationUpdate();
//        Log.i(LOG_TAG, "get image urls");
        setProgressBarIndeterminateVisibility(true);


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

        MainFragment.ImageURLService service = retrofit.create(MainFragment.ImageURLService.class);

        Log.i(LOG_TAG, "the user_name is: " + user_name);
        Call<ImageURLResponse> queryResponseCall =
                service.getURL(lat, lng, user_name, 100);

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
                    if (res.getUserName().equals(userProfile)) {
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
//    public interface ImageURLService {
//        @GET("default/get_images")
//        Call<ImageURLResponse> getURL(@Query("lat") String lat,
//                                      @Query("lng") String lng,
//                                      @Query("user_name") String user_name);
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);




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
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user_name", user_name);
            startActivity(intent);
        }

        if (id == R.id.inbox){
            Intent intent = new Intent(this, PmActivity.class);
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
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            // start the image capture Intent
            startActivityForResult(intent, TAKE_PIC_REQUEST);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }



    // after uploading image from gallery or camera, go to preview acitivity to add description
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!= null){
            imageUri = data.getData();
            Log.i(LOG_TAG, "imageURI: " + imageUri);
            Intent i = new Intent(this, PreviewActivity.class);
            i.putExtra("imageUri", imageUri.toString());
            startActivity(i);
        }
        if(requestCode==TAKE_PIC_REQUEST &&resultCode==RESULT_OK&&data!=null){
            Log.i(LOG_TAG, "thumbnail "  + data.getData() );
            imageUri = data.getData();
            Log.i(LOG_TAG, "imageURI: " + imageUri);
            Intent i = new Intent(this, PreviewActivity.class);
            i.putExtra("imageUri", imageUri.toString());
            startActivity(i);
        }
    }





    /*
Request location update. This must be called in onResume if the user has allowed location sharing
 */
    private void requestLocationUpdate(){
        Log.i(LOG_TAG, "requesting update");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

                Log.i(LOG_TAG, "requesting location update");
            }
            else {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.i(LOG_TAG, "please allow to use your location");

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
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

//    public interface UpvoteServiceUser {
//        @GET("default/upvote")
//        Call<Example> upvote(@Query("user_name") String user_name,
//                             @Query("image_id" ) String image_id);
//    }
//
//    public interface DownvoteServiceUser {
//        @GET("default/downvote")
//        Call<Example> upvote(@Query("user_name") String user_name,
//                             @Query("image_id" ) String image_id);
//    }

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
