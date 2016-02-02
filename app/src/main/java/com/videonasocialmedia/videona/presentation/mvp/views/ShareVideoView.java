package com.videonasocialmedia.videona.presentation.mvp.views;

import com.videonasocialmedia.videona.model.entities.social.SocialNetwork;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public interface ShareVideoView {

    void showShareNetworksAvailable(List<SocialNetwork> networks);

    void hideShareNetworks();

    void showMoreNetworks(List<SocialNetwork> networks);

    void hideExtraNetworks();
}
