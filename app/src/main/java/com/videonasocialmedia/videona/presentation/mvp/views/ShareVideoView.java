package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.social.SocialNetworkApp;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public interface ShareVideoView {

    void showShareNetworksAvailable(List<SocialNetworkApp> networks);

    void hideShareNetworks();

    void showMoreNetworks(List<SocialNetworkApp> networks);

    void hideExtraNetworks();
}
