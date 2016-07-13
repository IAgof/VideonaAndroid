package com.videonasocialmedia.videona.presentation.views.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.presentation.mvp.presenters.EditNavigatorPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.EditNavigatorView;
import com.videonasocialmedia.videona.presentation.views.activity.EditActivity;
import com.videonasocialmedia.videona.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.videona.presentation.views.activity.MusicListActivity;
import com.videonasocialmedia.videona.presentation.views.activity.ShareActivity;

import static com.videonasocialmedia.videona.utils.UIUtils.tintButton;

/**
 *
 */
public class ToolbarNavigator extends LinearLayout implements EditNavigatorView {

    private ImageButton navigateToEditButton;
    private ImageButton navigateToMusicButton;
    private ImageButton navigateToShareButton;

    private Context context;
    private EditNavigatorPresenter navigatorPresenter;

    private ToolbarNavigator.ProjectModifiedCallBack callback;

    public ToolbarNavigator(Context context) {
        super(context);
        initComponents(context, null, 0);
    }

    private void initComponents(Context context, AttributeSet attrs, int defStyleAttr) {

        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.edit_activities_navigation_buttons, this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarNavigator, defStyleAttr, 0);
        boolean editSelected = a.getBoolean(R.styleable.ToolbarNavigator_edit_selected, false);
        boolean musicSelected = a.getBoolean(R.styleable.ToolbarNavigator_music_selected, false);
        boolean shareSelected = a.getBoolean(R.styleable.ToolbarNavigator_share_selected, false);
        int tintList = a.getResourceId(R.styleable.ToolbarNavigator_tint_color, R.color.button_color);
        a.recycle();

        navigateToEditButton = (ImageButton) findViewById(R.id.button_edit_navigator);
        navigateToMusicButton = (ImageButton) findViewById(R.id.button_music_navigator);
        navigateToShareButton = (ImageButton) findViewById(R.id.button_share_navigator);

        tintButton(navigateToEditButton, tintList);
        tintButton(navigateToMusicButton, tintList);
        tintButton(navigateToShareButton, tintList);

        navigateToEditButton.setSelected(editSelected);
        navigateToMusicButton.setSelected(musicSelected);
        navigateToShareButton.setSelected(shareSelected);

        initListeners();
        disableNavigatorActions();
        if (!isInEditMode())
            navigatorPresenter = new EditNavigatorPresenter(this);
    }

    private void initListeners() {
        navigateToMusicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigateToMusicButton.isEnabled()) {
                    navigatorPresenter.checkMusicAndNavigate();
                }
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
                    /*Intent intent = new Intent(context, ExportProjectService.class);
                    Snackbar.make(v, "Starting export", Snackbar.LENGTH_INDEFINITE).show();
                    context.startService(intent);*/
                    navigateTo(ShareActivity.class);
                }
            }
        });
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(context, attrs, 0);
    }

    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToolbarNavigator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initComponents(context, attrs, defStyleAttr);
    }

    public ProjectModifiedCallBack getCallback() {
        if (callback != null)
            return callback;
        else
            return new ToolbarNavigator.ProjectModifiedCallBack();
    }

    @Override
    public void enableNavigatorActions() {
        enableButton(navigateToMusicButton);
        enableButton(navigateToEditButton);
        enableButton(navigateToShareButton);
    }

    private void enableButton(ImageButton button) {
        if (button.isSelected())
            button.setEnabled(false);
        else
            button.setEnabled(true);
    }

    @Override
    public void disableNavigatorActions() {
        navigateToEditButton.setEnabled(false);
        navigateToMusicButton.setEnabled(false);
        navigateToShareButton.setEnabled(false);
    }

    @Override
    public void goToMusic(Music music) {
        if (music == null) {
            navigateTo(MusicListActivity.class);
        } else {
            Intent i = new Intent(context, MusicDetailActivity.class);
            i.putExtra(MusicDetailActivity.KEY_MUSIC_ID, music.getMusicResourceId());
            context.startActivity(i);
        }
    }

    public class ProjectModifiedCallBack {

        public void onProjectModified() {
            navigatorPresenter.areThereVideosInProject();
        }
    }
}
