package com.paine.nativeApp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.paine.nativeApp.response.RegistrationResponse;

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
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SignupActivity extends AppCompatActivity {


    private static final String TAG = "SignupActivity";
    final int PIC_CROP = 1;



    String user_name;

    private Bitmap bitmapScaled;
    private String image_id;
    public static String LOG_TAG = "MyApplication";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final String SERVER_ADDRESS = "http://imagegallery.netai.net/";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PIC_REQUEST = 100;
    private LocationData locationData = LocationData.getLocationData();//store location to share between activities
    private Bitmap bitmap;
    private Uri imageUri;
    private String uploadImagestr;
    private SharedPreferences settings;
    private Integer ImageWidth;

    private static String name;

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    @Bind(R.id.profile_btn_upload)
    Button profile_btn_upload;
    @Bind(R.id.profile_btn_take)
    Button take_profile;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ImageWidth = size.x;

        settings = PreferenceManager.getDefaultSharedPreferences(this);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        profile_btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        take_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickpic();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name = _nameText.getText().toString();
//        String email = _emailText.getText().toString();
//        user_email = email;
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        // Let's register the user.
        // In truth, it may be better to keep a flag in preferences that tells us
        // whether we have already registered?

        new UploadImage(bitmap, image_id).execute();

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

        NicknameService service = retrofit.create(NicknameService.class);

        Call<RegistrationResponse> queryResponseCall =
                service.registerUser(name, password, image_id);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Response<RegistrationResponse> response) {
                Log.i(TAG, "Code is: " + response.code());
                Log.i(TAG, "The result is: " + response.body().response);
                if (response.body().response.equals("ok")) {
                    Log.i(TAG,"loginsuccesfull");
                    onSignupSuccess();
//                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor e = settings.edit();
                    Log.i(LOG_TAG, "name and image id at signup " + name + image_id);
                    e.putString("user_name", name);
                    e.putString("user_profile",image_id);
                    e.commit();

                }else onSignupFailed();


            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
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
            Toast.makeText(getApplicationContext(), "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
            //Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            //startActivity(i);
        }
    }

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000*30);
        return httpRequestParams;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //-- Choose File
    //MainActivity Stuff-- choose image/take pick
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //-- Take Profile Pic
    private void clickpic() {
        // Check Camera
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

    //-- Scale Down Photo
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

    private void performCrop(Uri picUri) {
        try {



            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("circleCrop","true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            Log.i(LOG_TAG,"IMAGE WIDTH " + ImageWidth);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //-- Upload Photo
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            bitmap = extras.getParcelable("data");


            SecureRandomString srs = new SecureRandomString();
            image_id = srs.nextString();

            ImageView iv = (ImageView) findViewById(R.id.profile_image);
//                iv.setImageBitmap(bitmap);
            try {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
                    Log.i(LOG_TAG,"scaledown");
                    bitmap = scaleDown(bitmap, width, true);
                }



//                    ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
//                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//                    Log.i(LOG_TAG, "orientation " + orientation);
//
//                    switch(orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            bitmap = rotateImage(bitmap, 90);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            bitmap = rotateImage(bitmap, 180);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            bitmap = rotateImage(bitmap, 270);
//                            break;

                // etc.
//                    }

                iv.setImageBitmap(bitmap);

//                } catch () {
//                    e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            performCrop(imageUri);
        }

        if (requestCode == PIC_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                bitmap = extras.getParcelable("data");


                SecureRandomString srs = new SecureRandomString();
                image_id = srs.nextString();

                ImageView iv = (ImageView) findViewById(R.id.profile_image);
//                iv.setImageBitmap(bitmap);
                try {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
                        Log.i(LOG_TAG,"scaledown");
                        bitmap = scaleDown(bitmap, width, true);
                    }



//                    ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
//                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//                    Log.i(LOG_TAG, "orientation " + orientation);
//
//                    switch(orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            bitmap = rotateImage(bitmap, 90);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            bitmap = rotateImage(bitmap, 180);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            bitmap = rotateImage(bitmap, 270);
//                            break;

                        // etc.
//                    }

                    iv.setImageBitmap(bitmap);

//                } catch () {
//                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }








        if(requestCode==TAKE_PIC_REQUEST &&resultCode==RESULT_OK&&data!=null) {
            Log.i(LOG_TAG, "thumbnail " + data.getData());
            imageUri = data.getData();
            Log.i(LOG_TAG, "imageURI: " + imageUri);


            SecureRandomString srs = new SecureRandomString();
            image_id = srs.nextString();

            ImageView iv = (ImageView) findViewById(R.id.profile_image);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
                    bitmap = scaleDown(bitmap, width, true);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------

    /**
     * Foursquare api https://developer.foursquare.com/docs/venues/search
     */
    public interface NicknameService {
        @GET("default/register_user")
        Call<RegistrationResponse> registerUser(@Query("name") String name,
                                                @Query("password") String password,
                                                @Query("profile_image_id") String profile_pic);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "User name taken", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
//        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
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