package com.videonasocialmedia.videona;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.videonasocialmedia.videona.api.ApiClient;
import com.videonasocialmedia.videona.record.RecordActivity;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    @InjectView(R.id.login_text_field)
    TextView userTextField;
    @InjectView(R.id.login_password_field)
    TextView passwordTextField;

    private ApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ButterKnife.inject(this);

        //TODO hacer cookies persistentes
        VideonaApplication app = (VideonaApplication) getApplication();
        apiClient = app.getApiClient();
    }

    /**
     * Start the activity to create a new user when the new_user_button is clicked
     */
    @OnClick(R.id.new_user_button)
    public void goToCreateUserAct() {
        startActivity(new Intent(getApplicationContext(), UserSignUpActivity.class));
    }

    /**
     * Try to login the user using the credentials provided by userTextField and passwordTextField
     */
    @OnClick(R.id.send_login_button)
    public void login() {

        /*
        String source = userTextField.getText().toString() + ":"
                + passwordTextField.getText().toString();
        try {
            String auth = "Basic " + Base64.encodeToString(source.getBytes("UTF-8"), Base64.DEFAULT);
            apiClient.login(auth, new BasicLoginCallback());
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Error durante login", Toast.LENGTH_SHORT).show();
        }
        */

        // Salto a pantalla Rec para pruebas

        Intent i = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(i);

    }

    /**
     * Sub-class implementing the callback of the login api call
     */
    class BasicLoginCallback implements Callback<Response> {
        /**
         * Star
         *
         * @param o
         * @param response successful response message from the server
         */
        @Override
        public void success(Response o, Response response) {
            //TODO store session
            Toast.makeText(getApplicationContext(), "Logeado correctamente", Toast.LENGTH_LONG).show();
            response.getHeaders();
            apiClient.getUserProfile(9, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });
            //startActivity(new Intent(getApplicationContext(), RecordActivity.class));


        }

        /**
         * Show a message to the user let him know it was not possible to login
         *
         * @param error error captured by retrofit
         */
        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getApplicationContext(), "Error durante login", Toast.LENGTH_SHORT).show();
        }
    }
}