package com.videonasocialmedia.videona.presentation.views.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.videonasocialmedia.videona.R;



public class ProfileActivity extends AppCompatActivity {

    private RecyclerView lstLista;
    private CollapsingToolbarLayout ctlLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Prueba Jesús");
        getSupportActionBar().setSubtitle("yosu13@gmail.com");



        //RecyclerView
        RecyclerView lstLista = (RecyclerView) findViewById(R.id.lstLista);


        ItemData itemsData[] = { new ItemData(R.drawable.activity_edit_clip_delete_normal),
                new ItemData(R.drawable.activity_edit_clip_delete_normal),
                new ItemData(R.drawable.activity_edit_clip_delete_normal),
                new ItemData(R.drawable.activity_edit_clip_delete_normal),
                new ItemData(R.drawable.activity_edit_clip_delete_normal),
                new ItemData(R.drawable.activity_edit_clip_delete_normal)};


        lstLista.setLayoutManager(new GridLayoutManager(this,3,LinearLayoutManager.VERTICAL,false));

        MyAdapter mAdapter = new MyAdapter(itemsData);
        lstLista.setAdapter(mAdapter);

        }
}