package com.videonasocialmedia.videona.presentation.views.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.videonasocialmedia.videona.R;

/**
 * Created by Veronica Lago Fominaya on 30/11/2015.
 */
public class EditTextDialogPreference extends EditTextPreference {

    private LinearLayout editTextDialogPreferenceView;
    private EditText edittext;

    public EditTextDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.preferences_edit_text_dialog);
    }

    @Override
    protected void onPrepareDialogBuilder(android.app.AlertDialog.Builder builder) {
        builder.setTitle(null);
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        editTextDialogPreferenceView = (LinearLayout) view;

        edittext = (EditText) view.findViewById(R.id.data);
        edittext.setText(PreferenceManager.
                getDefaultSharedPreferences(view.getContext()).
                getString(getKey(), ""));

        Button ok_button = (Button) view.findViewById(R.id.save);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edittext.getText().toString();
                if(callChangeListener(value)) {
                    SharedPreferences.Editor editor = getEditor();
                    editor.putString(getKey(), value);
                    editor.commit();
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            //SharedPreferences.Editor editor = getEditor();
            //editor.putString(myKey2, myView.getValue2());
            //editor.commit();
        }
    }
}
