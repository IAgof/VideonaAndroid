package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.videona.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.videona.utils.ConfigPreferences;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter {

    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private Project currentProject;
    private ShareVideoView shareVideoView;
    private SharedPreferences sharedPreferences;

    public ShareVideoPresenter(ShareVideoView shareVideoView, ToolbarNavigator.ProjectModifiedCallBack callback) {
        this.shareVideoView = shareVideoView;
        sharedPreferences = VideonaApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        currentProject = Project.getInstance(null, null, null);
    }

    public void onCreate() {
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
    }

    public void onResume() {
        obtainNetworksToShare();
    }

    public void obtainNetworksToShare() {
        List networks = obtainNetworksToShareUseCase.obtainMainNetworks();

        shareVideoView.showShareNetworksAvailable(networks);
    }

    public void shareVideo(String videoPath, SocialNetwork appToShareWith, Context ctx) {
        final ComponentName name = new ComponentName(appToShareWith.getAndroidPackageName(),
                appToShareWith.getAndroidActivityName());

        Uri uri = Utils.obtainUriToShare(ctx, videoPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                VideonaApplication.getAppContext().getResources().getString(R.string.sharedWithVideona));
        intent.putExtra(Intent.EXTRA_TEXT,
                VideonaApplication.getAppContext().getResources().getString(R.string.videonaTags));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setComponent(name);

        ctx.startActivity(intent);
    }

    public void obtainExtraAppsToShare() {
        List networks = obtainNetworksToShareUseCase.obtainSecondaryNetworks();
        shareVideoView.hideShareNetworks();
        shareVideoView.showMoreNetworks(networks);
    }

    public double getVideoLength() {
        return sharedPreferences.getLong(ConfigPreferences.VIDEO_DURATION, 0);
    }

    public double getNumberOfClips() {
        return sharedPreferences.getInt(ConfigPreferences.NUMBER_OF_CLIPS, 1);
    }

    public String getResolution() {
        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

    public void resetProject() {
        currentProject.clear();
    }

    public void continueProject(){
        String title = "V_EDIT_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        currentProject.setTitle(title);
    }

    public void checkIfVideoIsExported() {
        String filePath = Constants.PATH_APP + File.separator + currentProject.getTitle() + ".mp4";
        if(!videoFileExportedExist(filePath)){
            shareVideoView.startVideoExporting();
        }
    }

    private boolean videoFileExportedExist(String title) {
        File f = new File(title);
        if(f.exists())
            return true;

        return false;
    }
}
