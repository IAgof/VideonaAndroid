package com.videona.videona.introapp;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.videona.videona.R;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    
    private ImageView image;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.intro_fragment_screen_slide_page, container, false);

        // Set the title view to show the page number.
      //  ((TextView) rootView.findViewById(android.R.id.text1)).setText(
      //          getString(R.string.title_template_step, mPageNumber + 1));
        
        image = (ImageView) rootView.findViewById(R.id.imageViewSlide);
        Resources res = getResources(); /** from an Activity */
      
        
        if( (mPageNumber+1) == 1 ) {
        
        	image.setImageDrawable(res.getDrawable(R.drawable.uno));
        
        } 
        
        if( (mPageNumber+1) == 2 ) {
            
        	image.setImageDrawable(res.getDrawable(R.drawable.dos));
            
        }
        
        if( (mPageNumber+1) == 3 ) {
            
        	image.setImageDrawable(res.getDrawable(R.drawable.tres));
            
        }
                

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}