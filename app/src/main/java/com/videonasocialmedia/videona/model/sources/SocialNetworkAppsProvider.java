package com.videonasocialmedia.videona.model.sources;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jca on 9/12/15.
 */
public class SocialNetworkAppsProvider {


    public List<SocialNetwork> getSocialNetworksAppsInstalled() {
        List<SocialNetwork> socialNetworks = new ArrayList<>();
        Context context = VideonaApplication.getAppContext();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            Log.d("SocialNetworks", app.activityInfo.name);
            String packageName = app.activityInfo.applicationInfo.packageName;
            String activityName = app.activityInfo.name;
            Drawable icon= app.loadIcon(pm);
            SocialNetwork socialNetwork;
            if (activityName.equalsIgnoreCase("com.twitter.android.composer.ComposerActivity")) {
                socialNetwork = new SocialNetwork("Twitter", packageName,
                        activityName, icon, "#videona");
            } else if (activityName.equalsIgnoreCase("com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")) {
                socialNetwork = new SocialNetwork("Facebook", packageName,
                        activityName, icon, "");
            } else if (packageName.toLowerCase().contains("whatsapp")
                    || activityName.toLowerCase().contains("whatsapp")) {
                socialNetwork = new SocialNetwork("Whatsapp", packageName,
                        activityName, icon, "#videona");
            } else if (packageName.toLowerCase().contains("youtube")
                    || activityName.toLowerCase().contains("youtube")) {
                socialNetwork = new SocialNetwork("Youtube", packageName,
                        activityName, icon, "#videonaTime");
            } else if (packageName.toLowerCase().contains("plus")
                    && activityName.toLowerCase().contains("com.google.android.libraries.social.gateway.GatewayActivity")) {
                socialNetwork = new SocialNetwork("GooglePlus", packageName,
                        activityName, icon, "#videona");
            } else if (packageName.toLowerCase().contains("instagram")
                    || activityName.toLowerCase().contains("instagram")) {
                socialNetwork = new SocialNetwork("Instagram", packageName,
                        activityName, icon, "#videona");
            } else {
                socialNetwork = new SocialNetwork("Generic", packageName,
                        activityName, icon, "");
            }
            socialNetworks.add(socialNetwork);
            Log.d(socialNetwork.getName(), socialNetwork.getAndroidPackageName()+"||"+ socialNetwork.getAndroidActivityName());
        }
        return socialNetworks;
    }
}
