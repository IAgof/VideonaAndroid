package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.fragment.SettingsFragment;

import butterknife.ButterKnife;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsBaseActivity extends VideonaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_preferences, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                goToContact();
                return true;
            case R.id.action_vote:
                goToVote();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToContact() {
        navigateTo("mailto:info@videona.com");
    }

    private void goToVote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaAlertDialog);
        AlertDialog dialog = builder.setTitle(R.string.rateUsDialogTitle)
                .setMessage(R.string.rateUsDialogMessage)
                .setNegativeButton(R.string.rateUsDialogNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO cambiar email
                        navigateTo("mailto:info@videona.com");
                    }
                }).setPositiveButton(R.string.rateUsDialogAffirmative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        navigateTo("market://details?id=com.videonasocialmedia.videona");
                    }
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    private void navigateTo(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
