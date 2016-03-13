package com.grafixartist.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//shobit find rest
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.grafixartist.gallery.response.ImageResult;
import com.grafixartist.gallery.response.ImageURLResponse;
import com.grafixartist.gallery.adapter.GalleryAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    private static String LOG_TAG = "MyApplication";

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PIC_REQUEST = 100;

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
    private String uploadImagestr;

    private SharedPreferences settings;

    ArrayList<String> colorList;

    private HashMap<String, Integer> spinnerHash = new HashMap<String, Integer>();
    private String spinnerHashKey;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh = false;
        Log.i(LOG_TAG, "refresh on create " + refresh);
        spinnerHash.put("5 Miles",0);
        spinnerHash.put("10 Miles", 1);
        spinnerHash.put("30 Miles", 2);
        spinnerHash.put("50 Miles", 3);
    }


    @Override
    protected void onResume(){
        Log.i(LOG_TAG, "on resume");
        Log.i(LOG_TAG, "refresh on resume " + refresh);

        
        if(!refresh) {
            locationData = LocationData.getLocationData();
        }

        refresh = false;

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        lat = settings.getString("lat", null);
        lng = settings.getString("lng", null);
        user_name = settings.getString("user_name", null);
        Log.i(LOG_TAG, "radius on resume " + radius);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.i(LOG_TAG, "on create user name: " + user_name + " lat: " + lat + lng);
        if (user_name == null) {
            Log.i(LOG_TAG,"switching to login intent");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else if (lat == null && lng == null){
            requestLocationUpdate();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getImageURLs();
        }

        super.onResume();

    }
    protected void onPause(){
        Log.i(LOG_TAG, "on pause");
        removeLocationUpdate();
        refresh = false;
        if(lat != null && lng != null && !refresh) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor e = settings.edit();
            e.putString("lat", lat);
            e.putString("lng", lng);
            e.commit();
        }
        adapter.clearData();

        Log.i(LOG_TAG, "onPause lat lng: " + lat + lng);
        super.onPause();
    }


    private void loadImages(){
        Log.i(LOG_TAG, "load images");

//        mRecyclerView.setHasFixedSize(true);
        progressBar.setVisibility(View.GONE);

        adapter = new GalleryAdapter(MainActivity.this, mydata);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Log.i(LOG_TAG, "mydata: " + mydata + " position: " + position);
                        String imageToPass = mydata.get(position).getImageID();
                        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                        intent.putExtra("image_id", imageToPass);
                        intent.putExtra("user_name", user_name);
                        startActivity(intent);

                    }
                }));
    }

    private void getImageURLs(){

        removeLocationUpdate();
        Log.i(LOG_TAG, "get image urls");
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

        ImageURLService service = retrofit.create(ImageURLService.class);

        Call<ImageURLResponse> queryResponseCall =
                service.getURL(lat, lng);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<ImageURLResponse>() {
            @Override
            public void onResponse(Response<ImageURLResponse> response) {
                if (response.code() == 500) {
                    Log.e(LOG_TAG, "Error, please try again");
                }
                ArrayList<ImageResult> imageInfo = new ArrayList<ImageResult>(response.body().getImageResult());
                for (int i = 0; i < imageInfo.size(); i++) {
                    ImageResult res = imageInfo.get(i);
                    //if image is within proximity to user's location, then display it here
                    ImageModel imageModel = new ImageModel();
                    imageModel.setUserName(res.getUserName());
                    imageModel.setUrl("http://imagegallery.netai.net/pictures/" + res.getImageId() + ".JPG");
                    imageModel.setDescription(res.getDescription());
                    imageModel.setImageID(res.getImageId());



                    Log.i(LOG_TAG, "image info list at :" + res + " username: " + res.getUserName()
                            + " URL : " + imageModel.getUrl() + "  description  " + res.getDescription());

                    mydata.add(imageModel);

                    Log.i(LOG_TAG, "the data is " + mydata);
                }
                loadImages();


                Log.i(LOG_TAG, "Code is: " + response.code());
                Log.i(LOG_TAG, "response " + response.body().getImageResult());
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
                                      @Query("lng") String lng);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem mSpinnerItem = menu.findItem(R.id.spinner);
        // Extract color list as keySet from colors hashmap

//        colorList =  new ArrayList<String>(colors.keySet());
//        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
//        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, colorList);
//        spinner.setAdapter(spin_adapter);
        View view = mSpinnerItem.getActionView();
        if (view instanceof Spinner)
        {
            Spinner spinner = (Spinner) view;
            spinner.setAdapter( ArrayAdapter.createFromResource( this,
                    R.array.spinner_data,
                    android.R.layout.simple_spinner_dropdown_item ) );

            settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            radius = settings.getInt("spinner",5);

            theposition = spinnerHash.get(radius + " Miles");
            Log.i(LOG_TAG, "spinner hash position" + theposition + " radius:" + radius);
            spinner.setSelection(theposition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(LOG_TAG, "spinner pos " + position);

                    switch(position){
                        case 0: radius = 5;
                            break;
                        case 1: radius = 10;
                            break;
                        case 2: radius = 30;
                            break;
                        case 3: radius = 50;
                            break;
                    }
                    Log.i(LOG_TAG, "radius " + radius);
                    SharedPreferences.Editor e = settings.edit();
                    e.putInt("spinner", radius);
                    e.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


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
        if (id == R.id.action_upload) {
            showFileChooser();
        }

        if (id == R.id.action_take){
            clickpic();
            Log.i(LOG_TAG, "inside action take");
        }
        if (id == R.id.signout){
            SharedPreferences.Editor e = settings.edit();
            e.remove("user_name");
            e.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
//            for testing lat lng
//            removeData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }



    private void clickpic() {
        // Check Camera
        Log.i(LOG_TAG, "click pick");
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


}
