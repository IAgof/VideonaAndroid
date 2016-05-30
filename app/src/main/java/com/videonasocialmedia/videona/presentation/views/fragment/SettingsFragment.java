package com.videonasocialmedia.videona.presentation.views.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videona.BuildConfig;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.PreferencesPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.PreferencesView;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaDialogListener;
import com.videonasocialmedia.videona.utils.AnalyticsConstants;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Veronica Lago Fominaya on 26/11/2015.
 */
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, PreferencesView,
        VideonaDialogListener {

    protected final int REQUEST_CODE_EXIT_APP = 1;
    protected ListPreference resolutionPref;
    protected ListPreference qualityPref;
    protected PreferencesPresenter preferencesPresenter;
    protected Context context;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected MixpanelAPI mixpanel;
    protected VideonaDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        initPreferences();
        preferencesPresenter = new PreferencesPresenter(this, resolutionPref, qualityPref, context,
                sharedPreferences);
        mixpanel = MixpanelAPI.getInstance(this.getActivity(), BuildConfig.MIXPANEL_TOKEN);
    }

    private void initPreferences() {
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceManager().setSharedPreferencesName(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        sharedPreferences = getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        resolutionPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION);
        qualityPref = (ListPreference) findPreference(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY);

        setupExitPreference();
        setupBetaPreference();
        setupDownloadKamaradaPreference();
        setupShareVideona();
    }

    private void setupExitPreference() {
        Preference exitPref = findPreference("exit");

        exitPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialog = new VideonaDialog.Builder()
                        .withTitle(getString(R.string.exit_app_title))
                        .withImage(R.drawable.common_icon_bobina)
                        .withMessage(getString(R.string.exit_app_message))
                        .withPositiveButton(getString(R.string.acceptExit))
                        .withNegativeButton(getString(R.string.cancelExit))
                        .withCode(REQUEST_CODE_EXIT_APP)
                        .withListener(SettingsFragment.this)
                        .create();
                dialog.show(getFragmentManager(), "exitAppDialog");
                return true;
            }
        });
    }

    private void setupBetaPreference() {
        Preference joinBetaPref = findPreference("beta");
        joinBetaPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new BetaDialogFragment().show(getFragmentManager(), "BetaDialogFragment");
                return true;
            }
        });
    }

    private void setupDownloadKamaradaPreference() {
        Preference exitPref = findPreference("downloadKamarada");
        exitPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                trackLinkClicked(getResources().getString(R.string.kamaradaGooglePlayLink),
                        AnalyticsConstants.DESTINATION_KAMARADA_PLAY);
                goToKamaradaStore();
                return true;
            }
        });
    }

    private void setupShareVideona() {
        Preference exitPref = findPreference("shareVideona");
        exitPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                trackAppShared("Videona", "WhatsApp");
                shareVideonaWithWhatsapp();
                return true;
            }
        });
    }

    private void trackLinkClicked(String uri, String destination) {
        JSONObject linkClickedProperties = new JSONObject();
        try {
            linkClickedProperties.put(AnalyticsConstants.LINK, uri);
            linkClickedProperties.put(AnalyticsConstants.SOURCE_APP,
                    AnalyticsConstants.SOURCE_APP_VIDEONA);
            linkClickedProperties.put(AnalyticsConstants.DESTINATION, destination);
            mixpanel.track(AnalyticsConstants.LINK_CLICK, linkClickedProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToKamaradaStore() {
        String url = getString(R.string.kamaradaGooglePlayLink);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void trackAppShared(String appName, String socialNetwork) {
        JSONObject appSharedProperties = new JSONObject();
        try {
            appSharedProperties.put(AnalyticsConstants.APP_SHARED_NAME, appName);
            appSharedProperties.put(AnalyticsConstants.SOCIAL_NETWORK, socialNetwork);
            mixpanel.track(AnalyticsConstants.APP_SHARED, appSharedProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void shareVideonaWithWhatsapp() {
        if(preferencesPresenter.checkIfWhatsappIsInstalled()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
            String text = getResources().getString(R.string.shareWith);
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.whatsAppSharedText));
            startActivity(Intent.createChooser(intent, text));
        } else {
            Toast.makeText(this.getActivity(), R.string.whatsAppNotInstalled, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list, null);
        ListView listView = (ListView)v.findViewById(android.R.id.list);

        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer, listView, false);
        listView.addFooterView(footer, null, false);

        TextView footerText = (TextView)v.findViewById(R.id.footerText);
        String text = getString(R.string.videona) + " v" + BuildConfig.VERSION_NAME + "\n" +
                getString(R.string.madeIn);
        footerText.setText(text);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        preferencesPresenter.checkAvailablePreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesPresenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesPresenter);
    }

    @Override
    public void setAvailablePreferences(ListPreference preference, ArrayList<String> listNames,
                                        ArrayList<String> listValues) {
        int size = listNames.size();
        CharSequence entries[] = new String[size];
        CharSequence entryValues[] = new String[size];
        for (int i=0; i<size; i++) {
            entries[i] = listNames.get(i);
            entryValues[i] = listValues.get(i);
        }
        preference.setEntries(entries);
        preference.setEntryValues(entryValues);
    }

    @Override
    public void setDefaultPreference(ListPreference preference, String name, String key) {
        preference.setValue(name);
        preference.setSummary(name);
        editor.putString(key, name);
        editor.commit();
    }

    @Override
    public void setPreference(ListPreference preference, String name) {
        preference.setValue(name);
        preference.setSummary(name);
        trackQualityAndResolutionUserTraits(preference.getKey(), name);
    }

    @Override
    public void setSummary(String key, String value) {
        Preference preference = findPreference(key);
        preference.setSummary(value);
    }

    private void trackQualityAndResolutionUserTraits(String key, String value) {
        String property = null;
        if(key.equals(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION))
            property = AnalyticsConstants.RESOLUTION;
        else if(key.equals(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY))
            property = AnalyticsConstants.QUALITY;
        mixpanel.getPeople().set(property, value.toLowerCase());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference connectionPref = findPreference(key);
        trackQualityAndResolutionUserTraits(key, sharedPreferences.getString(key, ""));
        connectionPref.setSummary(sharedPreferences.getString(key, ""));
    }

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_EXIT_APP) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
            System.exit(0);
        }
    }

    @Override
    public void onClickNegativeButton(int id) {
        if(id == REQUEST_CODE_EXIT_APP)
            dialog.dismiss();
    }

}
