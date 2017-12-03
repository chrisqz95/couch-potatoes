package com.example.potato.couchpotatoes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class UserGalleryActivity extends AppCompatActivity {
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    // Default number of images, need to change to be dynamic
    private static final int NUM_PAGES = 3;

    /**
     * Sets up the swipe gallery
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_gallery);

        // Creates a back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.gallery);
        mPagerAdapter = new UserGalleryAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * When the back button is pressed, go back.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    /**
     * Shows a single image of a user
     */
    public static class UserImageFragment extends Fragment {
        /**
         * When the view is created, show the user image layout
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.user_image, container, false);

            return rootView;
        }

        /**
         * After the view is loaded, load the specified image
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
//        ImageView userImage = (ImageView) getView().findViewById(R.id.userImage);
//        String image = "http://www.aft.com/components/com_easyblog/themes/wireframe/images/placeholder-image.png";
//        Glide.with(getActivity()).load(image).into(userImage);
        }
    }

    /**
     * Adapter that attaches multiple image fragments to the gallery
     */
    private class UserGalleryAdapter extends FragmentStatePagerAdapter {
        /**
         * Creates a fragment manager for the gallery
         * @param fm - gallery fragment manager
         */
        public UserGalleryAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Gets the image in a specific index
         */
        @Override
        public Fragment getItem(int position) {
            return new UserImageFragment();
        }

        /**
         * Gets the number of images in the gallery
         * @return number of images
         */
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
