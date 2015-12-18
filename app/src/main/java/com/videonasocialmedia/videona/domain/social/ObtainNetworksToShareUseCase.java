package com.videonasocialmedia.videona.domain.social;

import com.videonasocialmedia.videona.model.entities.social.SocialNetworkApp;
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

    public List<SocialNetworkApp> ObtainAllNetworks() {
        return provider.getSocialNetworksAppsInstalled();
    }

    public List<SocialNetworkApp> obtainMainNetworks() {
        List<SocialNetworkApp> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetworkApp> mainNetworksList = new ArrayList<>();
        for (SocialNetworkApp app : networksList) {
            if (isMainNetwork(app)) {
                mainNetworksList.add(app);
            }
        }

        return mainNetworksList;
    }

    public List<SocialNetworkApp> obtainSecondaryNetworks() {
        List<SocialNetworkApp> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetworkApp> secondaryNetworksList = new ArrayList<>();
        for (SocialNetworkApp app : networksList) {
            if (!isMainNetwork(app)) {
                secondaryNetworksList.add(app);
            }
        }
        return secondaryNetworksList;
    }

    private boolean isMainNetwork(SocialNetworkApp app) {
        String appName = app.getName();
        return appName.equalsIgnoreCase("Twitter")
                || appName.equalsIgnoreCase("Facebook")
                || appName.equalsIgnoreCase("Whatsapp")
                || appName.equalsIgnoreCase("GooglePlus")
                || appName.equalsIgnoreCase("Youtube")
                || appName.equalsIgnoreCase("Instagram");
    }
}
