package com.example.potato.couchpotatoes;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PictureGridTabViewActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static int itemCount;
    private static ArrayList<String> urlList;

    ArrayList<InflatedImageFragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_grid_tab_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);




        Bundle extras = getIntent().getExtras();
        itemCount = extras.getInt("itemCount");
        mViewPager.setCurrentItem(extras.getInt("startingItem"));
        urlList = extras.getStringArrayList("urlList");

        mSectionsPagerAdapter.notifyDataSetChanged();

        fragmentList = new ArrayList<>();
        for (int x = 0; x < itemCount; x++)
            fragmentList.add(InflatedImageFragment.newInstance(x));

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class InflatedImageFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public InflatedImageFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static InflatedImageFragment newInstance(int sectionNumber) {
            InflatedImageFragment fragment = new InflatedImageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_picture_grid_tab_view, container, false);
            ImageView mImageView = rootView.findViewById(R.id.picture_grid_tab_view_fragment_image);
            Picasso.with(getContext()).load(urlList.get(getArguments().getInt(ARG_SECTION_NUMBER))).into(mImageView);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show total of ArrayList length
            return itemCount;
        }
    }
}
