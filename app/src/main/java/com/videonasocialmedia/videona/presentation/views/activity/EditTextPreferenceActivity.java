package com.videonasocialmedia.videona.presentation.views.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qordoba.sdk.Qordoba;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditTextPreferencePresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditTextPreferenceView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public abstract class EditTextPreferenceActivity extends VideonaActivity implements
        EditTextPreferenceView {

    protected Context context;
    protected EditTextPreferencePresenter presenter;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    @Bind(R.id.edit_user_account)
    EditText editText;
    @Bind(R.id.edit_text_preferences_icon)
    ImageView editTextImage;
    @Bind(R.id.info_field)
    TextView infoText;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_edit_text);
        ButterKnife.bind(this);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP)
            editText.getBackground().setColorFilter(getResources().getColor(R.color.editTextBottomLine),
                    PorterDuff.Mode.SRC_ATOP);
        editText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {
                    putIconForEditTextIsNotNull();
                } else {
                    putIconForEditTextIsNull();
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        setToolbar();
        // Display the fragment as the main content.
        Qordoba.setCurrentNavigationRoute(android.R.id.content, this.getClass().getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        editText.setHint(getString(presenter.getHintText()));
        checkIfPreviousTextExists();
    }

    private void checkIfPreviousTextExists() {
        String text = presenter.getPreviousText();
        if (text != null && !text.isEmpty()) {
            editText.setText(text);
            putIconForEditTextIsNotNull();
            showInfoText();
        } else {
            putIconForEditTextIsNull();
        }
    }

    protected void putIconForEditTextIsNotNull() {}

    protected void putIconForEditTextIsNull() {}

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.save)
    protected void setPreference() {
        String text = editText.getText().toString();
        presenter.setPreference(text);
    }

    @Override
    public void setUserPropertyToMixpanel(String property, String value) {
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        //Special properties in Mixpanel use $ before property name
        mixpanel.getPeople().set(property, value);
        if(property == "account_email")
            mixpanel.getPeople().setOnce("$email", value);

    }

    public void showMessage(int messageId) {
        Toast.makeText(getApplicationContext(), getString(messageId),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInfoText() {
    }

    @Override
    public void hideInfoText() {
        infoText.setText(null);
    }

    @Override
    public void removeEditText() {
        editText.setText(null);
    }

    @Override
    public void goBack() {
        finish();
    }

    @OnClick(R.id.info_field)
    public void removeData() {
        presenter.removeData();
    }

}
