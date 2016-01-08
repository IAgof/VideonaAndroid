package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.videonasocialmedia.videona.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.videona.model.entities.social.SocialNetworkApp;
import com.videonasocialmedia.videona.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.videona.utils.Utils;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter {

    ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    ShareVideoView shareVideoView;

    public ShareVideoPresenter(ShareVideoView shareVideoView) {
        this.shareVideoView = shareVideoView;
    }

    public void onCreate() {
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
    }

    public void onResume() {
        obtainNetworksToShare();
    }

    public void shareVideo(String videoPath, SocialNetworkApp appToShareWith, Context ctx) {

        final ComponentName name = new ComponentName(appToShareWith.getAndroidPackageName(),
                appToShareWith.getAndroidActivityName());

        Uri uri = Utils.obtainUriToShare(ctx, videoPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(name);
        ctx.startActivity(intent);


    }

    public void obtainNetworksToShare() {
        List networks = obtainNetworksToShareUseCase.obtainMainNetworks();
        shareVideoView.showShareNetworksAvailable(networks);
    }

    public void obtainExtraAppsToShare() {
        List networks = obtainNetworksToShareUseCase.obtainSecondaryNetworks();
        shareVideoView.hideShareNetworks();
        shareVideoView.showMoreNetworks(networks);
    }

}
