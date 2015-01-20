package com.videona.videona;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by amm on 8/09/14.
 */
public class WelcomeActivity extends Activity {

    private final String LOG_TAG= this.getClass().getSimpleName();

    private boolean showToday;

    private CheckBox btnAsk;
    private Button btnOk;
    private Button btnIndiegogo;

    private Typeface tf;
    
    private TextView textWelcome;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);
        
        tf = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Medium.ttf");

        Context context = getApplicationContext();
        final UserPreferences appPrefs = new UserPreferences(context);

        Log.d(LOG_TAG, "appPrefs welcome " + appPrefs.getCheckIndiegogo());
        
        textWelcome = (TextView) findViewById(R.id.textWelcome);
        textWelcome.setTypeface(tf);

        btnOk = (Button) findViewById(R.id.btnOkWelcome);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "appPrefs welcome " + appPrefs.getCheckIndiegogo());

                setResult(RESULT_OK);
                finish();

            }
        });


        btnAsk = (CheckBox) findViewById(R.id.checkBoxNotAsk);
        btnAsk.setEnabled(true);
        btnAsk.setTypeface(tf);
        btnAsk.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAsk.isChecked()){

                    btnAsk.setChecked(true);
                    appPrefs.setCheckIndiegogo(false);

                    Log.d(LOG_TAG, "appPrefs welcome " + appPrefs.getCheckIndiegogo());

                } else {

                    btnAsk.setChecked(false);
                    appPrefs.setCheckIndiegogo(true);

                    Log.d(LOG_TAG, "appPrefs welcome " + appPrefs.getCheckIndiegogo());
                }
            }
        });


        btnIndiegogo = (Button) findViewById(R.id.btnIndiegogo);
        btnIndiegogo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(getString(R.string.videona_web));
                Intent videonaLink = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(videonaLink);

            }
        });



    }


}
