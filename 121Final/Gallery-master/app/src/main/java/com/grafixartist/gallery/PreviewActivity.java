package com.grafixartist.gallery;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import com.grafixartist.gallery.response.UploadResponse;
import android.media.ExifInterface;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
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
 * Created by bryce on 3/1/2016.
 */
public class PreviewActivity extends AppCompatActivity {
    private static final String SERVER_ADDRESS = "http://imagegallery.netai.net/";
    private Uri imageUri;
    private Bitmap bitmap;
    private Bitmap bitmapScaled;
    private static String LOG_TAG = "MyApplication";

    private String image_id;
    private String user_name;
    private String description;
    private String comment_id;
    private String lat;
    private String lng;
    private SharedPreferences settings;


    EditText uploadImageName; //description

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = settings.getString("user_name", null);
        lat = settings.getString("lat", null);
        lng = settings.getString("lng", null);
        SecureRandomString srs = new SecureRandomString();
        image_id = srs.nextString();
        comment_id = srs.nextString();
        Log.i("MyApplication" , "preview activity   user_name:  " + user_name + " image_id: " + image_id + " comment_id: " + comment_id);
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("imageUri"));

        uploadImageName = (EditText) findViewById(R.id.editText);


        ImageView iv = (ImageView) findViewById(R.id.imageView);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);




            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            if(bitmap.getWidth()>width || bitmap.getHeight() > height){
                bitmap = scaleDown(bitmap, width,true);
                Log.i(LOG_TAG,"bitmap scaled");
            }else {

                Log.i(LOG_TAG,"bitmap notscaled");
            }

            ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.i(LOG_TAG, "orientation " + orientation);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;

                // etc.
            }
            iv.setImageBitmap(bitmap);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public void uploadPhoto(View v){
        Log.i(LOG_TAG, "upload photo clicked");
        Log.i(LOG_TAG, "username upload url" + image_id); //uploadImageName.getText().toString() );
        //upload image to server with imageid as url
        new UploadImage(bitmap, image_id).execute();

        description = uploadImageName.getText().toString();

        //upload image_id, comment_id, user_name, lat, lng to server
        //comment_id and image_id are random strings

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

        UploadService service = retrofit.create(UploadService.class);

        Call<UploadResponse> queryResponseCall =
                service.upload(image_id, comment_id, user_name, lat, lng, description);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Response<UploadResponse> response) {
                Log.i(LOG_TAG, "Code is: " + response.code());
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
    public interface UploadService {
        @GET("default/upload_image")
        Call<UploadResponse> upload(@Query("image_id") String image_id,
                                                @Query("comment_id") String comment_id,
                                                @Query("user_name") String user_name,
                                                @Query("lat") String lat,
                                                @Query("lng") String lng,
                                                @Query("description") String description);
    }


    private class UploadImage extends AsyncTask<Void,Void,Void> {
       Bitmap image;
       String name;
       public UploadImage(Bitmap image, String name){
           this.image = image;
           this.name = name;
       }
        @Override
        protected Void doInBackground(Void... params){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT );

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image",encodedImage));
            dataToSend.add(new BasicNameValuePair("name", name));

            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SavePicture.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);

            }catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
        return httpRequestParams;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}


