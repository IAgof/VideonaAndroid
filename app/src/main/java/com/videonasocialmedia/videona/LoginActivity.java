package com.videonasocialmedia.videona;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.videona.api.ApiClient;
import com.videonasocialmedia.videona.record.CameraActivity;
import com.videonasocialmedia.videona.record.RecordActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    @InjectView(R.id.login_text_field)
    TextView userTextField;
    @InjectView(R.id.login_password_field)
    TextView passwordTextField;
    @InjectView(R.id.checkBox_remember_me)
    CheckBox rememberMe;


    private ApiClient apiClient;
    private VideonaApplication app;
    private SharedPreferences config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        apiClient = app.getApiClient();
        config = getApplicationContext()
                .getSharedPreferences("USER_INFO", MODE_PRIVATE);
    }

    /**
     * Start the activity to create a new user when the new_user_button is clicked
     */
    @OnClick(R.id.new_user_button)
    public void goToUserSignUpActivity() {
        startActivity(new Intent(getApplicationContext(), UserSignUpActivity.class));
    }

    /**
     * Try to login the user using the credentials provided by userTextField and passwordTextField
     */
    @OnClick(R.id.send_login_button)
    public void login() {

        //TODO remove next line when remember-me is working and uncomment the rest of the method

//        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(new Intent(getApplicationContext(), CameraActivity.class));
//        } else {
            startActivity(new Intent(getApplicationContext(), RecordActivity.class));
//        }

       /* String source = userTextField.getText().toString() + ":"
                + passwordTextField.getText().toString();
        try {
            String auth = "Basic " + Base64.encodeToString(source.getBytes("UTF-8"),
                    Base64.DEFAULT);

            int rememberQueryParam = 0;
            boolean rememberMeChecked = rememberMe.isChecked();
            if (rememberMeChecked) {
                rememberQueryParam = 1;
            }

            apiClient.login(auth, rememberQueryParam, new BasicLoginCallback());

            //Store remember user config
            config.edit().putBoolean("rememberUser", rememberMeChecked);

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Error durante login",
                    Toast.LENGTH_SHORT).show();
        }*/
    }


    /**
     * Sub-class implementing the callback of the login api call
     */
    class BasicLoginCallback implements Callback<Response> {
        /**
         * Receive the successful response from the server and stores the cookie with the session
         * data in VideonaApplication.apiHeaders and, if rememberUser is true, the cookie is also
         * stored in sharedPreferences
         *
         * @param o
         * @param response successful response message from the server
         */
        @Override
        public void success(Response o, Response response) {
            Toast.makeText(getApplicationContext(), "Logeado correctamente",
                    Toast.LENGTH_LONG).show();

            String cookie = "";
            String sessionIdCookie = "";
            String rememberMeCookie = "";
            List<Header> h = response.getHeaders();
            for (Header header : h) {
                if (header.getName().equalsIgnoreCase("set-cookie")) {
                    cookie = header.getValue();
                    if (cookie.contains("PHPSESSSID")) {
                        sessionIdCookie = cookie;
                    } else if (cookie.contains("REMEMBERME")) {
                        rememberMeCookie = cookie;
                    }
                }
            }

            app.getApiHeaders().setSessionCookieValue(sessionIdCookie);


            if (rememberMe.isChecked()) {
                app.getApiHeaders().setRememberMeCookieValue(rememberMeCookie);
                config.edit().putString("sessionCookie", sessionIdCookie).apply();
                config.edit().putString("rememberMeCookie", rememberMeCookie).apply();
            }

            startActivity(new Intent(getApplicationContext(), RecordActivity.class));

        }

        /**
         * Show a message to the user let him know it was not possible to login
         *
         * @param error error captured by retrofit
         */
        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getApplicationContext(), "Error durante login",
                    Toast.LENGTH_SHORT).show();
        }
    }
}