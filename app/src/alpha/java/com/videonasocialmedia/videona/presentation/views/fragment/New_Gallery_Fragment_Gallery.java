package com.videonasocialmedia.videona.presentation.views.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GalleryPagerPresenter;
import com.videonasocialmedia.videona.presentation.mvp.presenters.VideoGalleryPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.videona.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.videona.presentation.views.adapter.VideoGalleryAdapter;
import com.videonasocialmedia.videona.presentation.views.listener.OnSelectionModeListener;
import com.videonasocialmedia.videona.presentation.views.listener.OnTransitionClickListener;
import com.videonasocialmedia.videona.presentation.views.listener.RecyclerViewClickListener;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.ItemSelectionSupport;
import com.videonasocialmedia.videona.utils.recyclerselectionsupport.MultiItemSelectionSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link New_Gallery_Fragment_Gallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link New_Gallery_Fragment_Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New_Gallery_Fragment_Gallery extends VideonaFragment implements VideoGalleryView, RecyclerViewClickListener, OnTransitionClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 4;


    protected Video selectedVideo;
    protected OnSelectionModeListener onSelectionModeListener;
    protected ItemSelectionSupport clickSupport;
    protected MultiItemSelectionSupport selectionSupport;

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showVideos(List<Video> videoList) {


        mAdapter = new VideoGalleryAdapter(videoList);
        mAdapter.setSelectionSupport(selectionSupport);
        mAdapter.setRecyclerViewClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnTransitionClickListener(this);


    }

    @Override
    public boolean isTheListEmpty() {
        return false;
    }

    @Override
    public void appendVideos(List<Video> movieList) {

    }

    @Override
    public void showVideoTimeline() {

    }

    @Override
    public void onClick(int position) {

        selectedVideo = mAdapter.getVideo(position);
    }

    @Override
    public void onClick(View transitionOrigin, int positionOnAdapter) {

        selectedVideo = mAdapter.getVideo(positionOnAdapter);
        String videoPath = selectedVideo.getMediaPath();
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected VideoGalleryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @Bind(R.id.viewRecycler)
    RecyclerView recyclerView;
    VideoGalleryPresenter videoGalleryPresenter;


    private OnFragmentInteractionListener mListener;

    public New_Gallery_Fragment_Gallery() {
        // Required empty public constructor
    }

    public static New_Gallery_Fragment_Gallery newInstance(String param1, String param2) {
        New_Gallery_Fragment_Gallery fragment = new New_Gallery_Fragment_Gallery();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoGalleryPresenter.obtainVideos(VideoGalleryPresenter.MASTERS_FOLDER);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_recyclerview, container, false);
        ButterKnife.bind(this,rootView);
        rootView.setTag(TAG);

        clickSupport = ItemSelectionSupport.addTo(recyclerView);
        clickSupport.setOnItemClickListener(new ItemSelectionSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (selectionSupport.getChoiceMode() == MultiItemSelectionSupport.ChoiceMode.MULTIPLE)
                    if (selectionSupport.isItemChecked(position)) {
                        selectionSupport.setItemChecked(position, true);
                        onSelectionModeListener.onItemUnchecked();
                    } else {
                        selectionSupport.setItemChecked(position, false);
                        onSelectionModeListener.onItemChecked();
                    }
            }
        });

        selectionSupport = MultiItemSelectionSupport.addTo(recyclerView);

        videoGalleryPresenter = new VideoGalleryPresenter(this);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.viewRecycler);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);


        recyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
