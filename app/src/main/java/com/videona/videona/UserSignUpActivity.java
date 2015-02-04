package com.videona.videona;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.videona.videona.api.ApiClient;
import com.videona.videona.api.RegisterRequestBody;
import com.videona.videona.api.Validator;
import com.videona.videona.record.RecordActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jca on 22/1/15.
 */
public class UserSignUpActivity extends Activity {

    @InjectView(R.id.register_text_field)
    TextView userName;
    @InjectView(R.id.email_text_field)
    TextView email;
    @InjectView(R.id.register_password)
    TextView password;
    @InjectView(R.id.register_confirm_password)
    TextView confirmPassword;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
        //TODO Hacer singleton el cliente api?
        VideonaApplication app = (VideonaApplication) getApplication();
        apiClient = app.getApiClient();
    }

    @OnClick(R.id.register_button)
    public void register() {
        String pass, confirmPass, usrName;

        pass = password.getText().toString();
        usrName = userName.getText().toString();
        confirmPass = confirmPassword.getText().toString();

        if (pass.equals(confirmPass)) {
            return;
        }
        if (Validator.validatePassword(pass)
                && Validator.validateUserName(usrName)
                && Validator.validateEmail(email.getText())) {


            Log.d("Registro", "user: " + usrName);
            Log.d("Registro", "email: " + email.getText().toString());
            Log.d("Registro", "pass: " + pass);

            RegisterRequestBody user = new RegisterRequestBody(
                    usrName,
                    email.getText().toString(),
                    pass);

            apiClient.register(user, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    //TODO modificar textos
                    Toast.makeText(getApplicationContext(), "Registrado correctamente: status "+response.getStatus() ,
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RecordActivity.class));
                }

                @Override
                public void failure(RetrofitError error) {
                    //TODO modificar textos
                    Toast.makeText(getApplicationContext(), "Imposible registrar: "+error.getKind().name(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
