package com.paine.nativeApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paine.nativeApp.response.LoginResponse;

import butterknife.ButterKnife;
import butterknife.Bind;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String login_flag;
    private String myUser_name;
    private String myPassword;
    private static String LOG_TAG = "MyApplication";
    private Intent serviceIntent;
    private String profile_id;


    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();
        myUser_name=name;
        myPassword=password;

        // TODO: Implement your own authentication logic here.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://empirical-realm-123103.appspot.com/pictureApp/")	//We are using Foursquare API to get data
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .client(httpClient)	//add logging
                .build();

        LoginService service = retrofit.create(LoginService.class);

        Call<LoginResponse> queryResponseCall =
                service.login(myUser_name, myPassword);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response) {
                Log.i(TAG, "Code is: " + response.code());
                Log.i(TAG, "The result is: " + response.body().response);
                profile_id = response.body().profile_pic;
                if(response.body().response.equals("user_not_registered")){
                    login_flag = "reg";
                   // onLoginFailed();
                }else if (response.body().response.equals("invalid_password")){
                   login_flag = "pas";
                   // onLoginFailed();
                }else if (response.body().response.equals("ok")){
                    login_flag = "suc";
                    //onLoginSuccess();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (login_flag.equals("reg")) {
                            onLoginFailed();
                        }else if (login_flag.equals("pas")){
                            onLoginFailed();
                        }else if (login_flag.equals("suc")){
                            onLoginSuccess();
                        }
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here


                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public interface LoginService{
        @GET("default/login")
        Call<LoginResponse> login(@Query("name") String userName,
                                  @Query("password") String password);

    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = settings.edit();
        e.putString("user_name", myUser_name);
        e.putString("user_profile",profile_id);
        Log.i(LOG_TAG, "onLoginSuccess username: " + myUser_name);
        e.commit();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("enter a valid user name");
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

}
