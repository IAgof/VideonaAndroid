package com.videonasocialmedia.videona.domain.social;

import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;
import com.videonasocialmedia.videona.model.entities.sources.SocialNetworkAppsProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public class ObtainNetworksToShareUseCase {

    SocialNetworkAppsProvider provider;


    public ObtainNetworksToShareUseCase() {
        this.provider = new SocialNetworkAppsProvider();
    }

    public List<SocialNetwork> ObtainAllNetworks() {
        return provider.getSocialNetworksAppsInstalled();
    }

    public List<SocialNetwork> obtainMainNetworks() {
        List<SocialNetwork> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetwork> mainNetworksList = new ArrayList<>();
        for (SocialNetwork app : networksList) {
            if (isMainNetwork(app)) {
                mainNetworksList.add(app);
            }
        }

        return mainNetworksList;
    }

    public List<SocialNetwork> obtainSecondaryNetworks() {
        List<SocialNetwork> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetwork> secondaryNetworksList = new ArrayList<>();
        for (SocialNetwork app : networksList) {
            if (!isMainNetwork(app)) {
                secondaryNetworksList.add(app);
            }
        }
        return secondaryNetworksList;
    }

    private boolean isMainNetwork(SocialNetwork app) {
        String appName = app.getName();
        return appName.equalsIgnoreCase("Twitter")
                || appName.equalsIgnoreCase("Facebook")
                || appName.equalsIgnoreCase("Whatsapp")
                || appName.equalsIgnoreCase("GooglePlus")
                || appName.equalsIgnoreCase("Youtube")
                || appName.equalsIgnoreCase("Instagram");
    }

    public boolean checkIfSocialNetworkIsInstalled(String appName) {
        boolean result = false;
        List<SocialNetwork> networksList = provider.getSocialNetworksAppsInstalled();
        for (SocialNetwork appInstalled : networksList) {
            if ((appInstalled.getName()).toLowerCase().contains(appName)) {
                result = true;
            }
        }
        return result;
    }
}
