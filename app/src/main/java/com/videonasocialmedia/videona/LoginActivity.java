package com.videonasocialmedia.videona;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.videona.api.ApiClient;
import com.videonasocialmedia.videona.record.RecordActivity;

import java.io.UnsupportedEncodingException;
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

    private ApiClient apiClient;
    private VideonaApplication app;
    private SharedPreferences config;

    private boolean rememberUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        apiClient = app.getApiClient();
        config = getApplicationContext()
                .getSharedPreferences("USER_INFO", MODE_PRIVATE);
        rememberUser = config.getBoolean("rememberUser", false);
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
        String source = userTextField.getText().toString() + ":"
                + passwordTextField.getText().toString();
        try {
            String auth = "Basic " + Base64.encodeToString(source.getBytes("UTF-8"),
                    Base64.DEFAULT);
            int remember = 0;
            if (rememberUser) remember = 1;
            apiClient.login(auth, remember, new BasicLoginCallback());

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Error durante login",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sub-class implementing the callback of the login api call
     */
    class BasicLoginCallback implements Callback<Response> {
        /**
         * Receive the successful response from the server and stores the cookie with the session
         * data in VideonaApplication.apiHeaders and, if rememberUser is true, the cookie is also
         * stored in sharedPreferences with the key cookie
         *
         * @param o
         * @param response successful response message from the server
         */
        @Override
        public void success(Response o, Response response) {
            Toast.makeText(getApplicationContext(), "Logeado correctamente",
                    Toast.LENGTH_LONG).show();

            String cookie = "";
            List<Header> h = response.getHeaders();
            for (Header header : h) {
                if (header.getName().equalsIgnoreCase("set-cookie"))
                    cookie = header.getValue();
            }

            app.getApiHeaders().setSessionCookieValue(cookie);


            if (rememberUser) {
                config.edit().putString("cookie", cookie).apply();
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