package com.videonasocialmedia.videona.presentation.views.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.JoinBetaPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import butterknife.ButterKnife;

/**
 * Created by Veronica Lago Fominaya on 12/11/2015.
 */
public class BetaDialogFragment extends DialogFragment implements JoinBetaView {

    private RelativeLayout joinBetaInfoLayout;
    private RelativeLayout joinBetaLinkLayout;
    private JoinBetaPresenter joinBetaPresenter;
    private EditText email;
    private ImageView emailIcon;
    private SharedPreferences sharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = this.getActivity().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        joinBetaPresenter = new JoinBetaPresenter(this, sharedPreferences);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_join_beta, null);
        builder.setView(v);
        ButterKnife.bind(this, v);
        email = (EditText) v.findViewById(R.id.email_text);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP)
            email.getBackground().setColorFilter(getResources().getColor(R.color.editTextBottomLine),
                    PorterDuff.Mode.SRC_ATOP);
        emailIcon = (ImageView) v.findViewById(R.id.email_icon);
        email.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

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
        joinBetaPresenter.checkIfPreviousEmailExists();
        setNegativeButton(v);
        setPositiveButton(v);
        joinBetaInfoLayout = (RelativeLayout) v.findViewById(R.id.joinBetaInfo);

        return builder.create();
    }

    private void putIconForEditTextIsNotNull() {
        emailIcon.setImageResource(R.drawable.activity_settings_icon_email);
    }

    private void putIconForEditTextIsNull() {
        emailIcon.setImageResource(R.drawable.activity_settings_icon_email_add);
    }

    private void setNegativeButton(View v) {
        View negativeButton = v.findViewById(R.id.negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void setPositiveButton(View v) {
        View sendButton = v.findViewById(R.id.positiveButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                joinBetaPresenter.validateEmail(email.getText().toString());
            }
        });
    }

    @Override
    public void showMessage(int messageId) {
        Toast.makeText(getActivity().getApplicationContext(), getString(messageId),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void hideDialog() {
        getDialog().cancel();
    }

    @Override
    public void setEmail(String text) {
        email.setText(text);
        putIconForEditTextIsNotNull();
    }

    @Override
    public void goToBeta() {
        hideKeyboard();
        goToBetaweb();
        dismiss();
    }

    private void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(email.getWindowToken(), 0);
    }

    public void goToBetaweb() {
        String url = "https://play.google.com/apps/testing/com.videonasocialmedia.videona";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
