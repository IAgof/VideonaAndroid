package com.videonasocialmedia.videona.presentation.views.customviews;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditNavigatorPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditNavigatorView;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videona.presentation.views.activity.MusicListActivity;

/**
 *
 */
public class ToolbarNavigator extends LinearLayout implements EditNavigatorView {

    private View view;
    private ImageButton navigateToEditButton;
    private ImageButton navigateToMusicButton;
    private ImageButton navigateToShareButton;
    private Context context;

    private EditNavigatorPresenter navigatorPresenter;

    public ToolbarNavigator(Context context) {
        super(context);
        initComponents(context);
    }

    private void initComponents(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.edit_activities_navigation_buttons, this);
        navigateToEditButton = (ImageButton) findViewById(R.id.button_edit_navigator);
        navigateToMusicButton = (ImageButton) findViewById(R.id.button_music_navigator);
        navigateToShareButton = (ImageButton) findViewById(R.id.button_share_navigator);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initComponents(context);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents(context);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initComponents(context);
    }

    private void initListeners() {
        navigateToMusicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToMusicButton.isEnabled())
                    navigateTo(MusicListActivity.class);
            }
        });
        navigateToEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToEditButton.isEnabled())
                    navigateTo(EditActivity.class);
            }
        });
        navigateToShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToShareButton.isEnabled()) {
                    //navigateTo(ShareVideoActivity)
                }
            }
        });
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(context, cls);

        context.startActivity(intent);
    }
}
