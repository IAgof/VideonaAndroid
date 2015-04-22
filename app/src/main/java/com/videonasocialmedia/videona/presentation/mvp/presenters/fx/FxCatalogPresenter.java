/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters.fx;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.EditorElement;
import com.videonasocialmedia.videona.presentation.mvp.views.FxCatalogView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan Javier Cabanas on 23/3/15.
 */
public class FxCatalogPresenter {

    private FxCatalogView fxCatalogView;

    public FxCatalogPresenter(FxCatalogView fxCatalogView) {
        this.fxCatalogView = fxCatalogView;
    }

    public void start(){
        //TODO ask upper layers for fxList and remove the next

        List<EditorElement> elementList = new ArrayList<>();


        elementList.add(new Music(R.drawable.activity_music_icon_rock_normal, "audio_rock", R.raw.audio_rock, R.color.pastel_palette_pink_2));
        elementList.add(new Music(R.drawable.activity_music_icon_ambiental_normal, "audio_ambiental", R.raw.audio_ambiental,R.color.pastel_palette_red));
        elementList.add(new Music(R.drawable.activity_music_icon_clarinet_normal, "audio_clasica_flauta", R.raw.audio_clasica_flauta,R.color.pastel_palette_blue));
        elementList.add(new Music(R.drawable.activity_music_icon_classic_normal, "audio_clasica_piano", R.raw.audio_clasica_piano,R.color.pastel_palette_brown));
        elementList.add(new Music(R.drawable.activity_music_icon_folk_normal, "audio_folk", R.raw.audio_folk,R.color.pastel_palette_red));
        elementList.add(new Music(R.drawable.activity_music_icon_hip_hop_normal, "audio_hiphop", R.raw.audio_hiphop,R.color.pastel_palette_green));
        elementList.add(new Music(R.drawable.activity_music_icon_pop_normal, "audio_pop", R.raw.audio_pop,R.color.pastel_palette_purple));
        elementList.add(new Music(R.drawable.activity_music_icon_reggae_normal, "audio_reggae", R.raw.audio_reggae,R.color.pastel_palette_orange));
        elementList.add(new Music(R.drawable.activity_music_icon_violin_normal, "audio_clasica_violin", R.raw.audio_clasica_violin,R.color.pastel_palette_yellow));
        elementList.add(new Music(R.drawable.activity_music_icon_remove_normal,"Remove", R.raw.audio_clasica_violin, R.color.pastel_palette_grey));

        OnFxListReceived(elementList);
    }

    //TODO make the next method a listener of use case events
    public void OnFxListReceived(List<EditorElement> elementListt){
        fxCatalogView.showCatalog(elementListt);
    }
}
