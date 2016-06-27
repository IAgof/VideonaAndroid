package com.videonasocialmedia.videona.presentation.views.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.videonasocialmedia.videona.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryMaterialActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_material);

        List<Integer> items = new ArrayList<>();

        items.add(R.drawable.overlay_filter_autumn);
        items.add (R.drawable.overlay_filter_bokeh);
        items.add(R.drawable.overlay_filter_burn);
        items.add (R.drawable.overlay_filter_rain);
        items.add (R.drawable.overlay_filter_old);


        recycler =(RecyclerView)findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para GridLayout
        lManager = new GridLayoutManager(this,3);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new GalleryMaterialAdapter(items);
        recycler.setAdapter(adapter);


    }
}
