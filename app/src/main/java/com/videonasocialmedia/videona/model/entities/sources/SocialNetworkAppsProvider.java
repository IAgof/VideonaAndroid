package com.videonasocialmedia.videona.model.entities.sources;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.entities.social.SocialNetworkApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jca on 9/12/15.
 */
public class SocialNetworkAppsProvider {


    public List<SocialNetworkApp> getSocialNetworksAppsInstalled() {
        List<SocialNetworkApp> socialNetworkApps = new ArrayList<>();
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
            SocialNetworkApp socialNetworkApp;
            if (packageName.toLowerCase().contains("twitter")
                    || activityName.toLowerCase().contains("twitter")) {
                socialNetworkApp = new SocialNetworkApp("Twitter", packageName,
                        activityName, icon, "#videona");
            } else if (packageName.toLowerCase().contains("facebook.katana")) {
                socialNetworkApp = new SocialNetworkApp("Facebook", packageName,
                        activityName, icon, "");
            } else if (packageName.toLowerCase().contains("whatsapp")
                    || activityName.toLowerCase().contains("whatsapp")) {
                socialNetworkApp = new SocialNetworkApp("Whatsapp", packageName,
                        activityName, icon, "#videona");
            } else if (packageName.toLowerCase().contains("youtube")
                    || activityName.toLowerCase().contains("youtube")) {
                socialNetworkApp = new SocialNetworkApp("Youtube", packageName,
                        activityName, icon, "");
            } else if (packageName.toLowerCase().contains("plus")
                    && activityName.toLowerCase().contains("com.google.android.libraries.social.gateway.GatewayActivity")) {
                socialNetworkApp = new SocialNetworkApp("GooglePlus", packageName,
                        activityName, icon, "#videona");
            } else if (packageName.toLowerCase().contains("instagram")
                    || activityName.toLowerCase().contains("instagram")) {
                socialNetworkApp = new SocialNetworkApp("Instagram", packageName,
                        activityName, icon, "#videona");
            } else {
                socialNetworkApp = new SocialNetworkApp("Generic", packageName,
                        activityName, icon, "");
            }
            socialNetworkApps.add(socialNetworkApp);
            Log.d(socialNetworkApp.getName(),socialNetworkApp.getAndroidPackageName()+"||"+socialNetworkApp.getAndroidActivityName());
        }
        return socialNetworkApps;
    }
}
