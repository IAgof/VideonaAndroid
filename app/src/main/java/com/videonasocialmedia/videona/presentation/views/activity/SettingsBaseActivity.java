package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.fragment.SettingsFragment;
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;

import butterknife.ButterKnife;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsBaseActivity extends VideonaActivity implements OnVideonaDialogListener {

    protected final int REQUEST_CODE_RATE_APP = 1;

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
        VideonaDialog dialog = new VideonaDialog.Builder()
                .withTitle(getString(R.string.rateUsDialogTitle))
                .withImage(R.drawable.common_icon_videona)
                .withMessage(getString(R.string.rateUsDialogMessage))
                .withPositiveButton(getString(R.string.rateUsDialogAffirmative))
                .withNegativeButton(getString(R.string.rateUsDialogNegative))
                .withCode(REQUEST_CODE_RATE_APP)
                .withListener(this)
                .create();
        dialog.show(getFragmentManager(), "rateAppDialog");
    }

    private void navigateTo(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_RATE_APP)
            navigateTo("market://details?id=com.videonasocialmedia.videona");
    }

    @Override
    public void onClickNegativeButton(int id) {
        //TODO cambiar email
        if(id == REQUEST_CODE_RATE_APP)
            navigateTo("mailto:info@videona.com");
    }
}
