package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MatchingActivity extends AppCompatActivity{

    private final String[] tabTitles = new String[] { "Date", "Friend" };

    // list of matches for dating and friending
//    private List<MatchedUser> matchedDateList = new ArrayList<>();
//    private List<MatchedUser> matchedFriendList = new ArrayList<>();
    private ArrayList<String> matchedDateList = new ArrayList<>();
    private ArrayList<String> matchedFriendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        // TODO CHANGE THIS THIS IS TEMPORARY
        matchedDateList.add("Della");
        matchedFriendList.add("Nestor");

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.matching_tabs);
        final MatchViewPager viewPager = (MatchViewPager) findViewById(R.id.matching_viewpager);
        MatchFragmentPagerAdapter adapter = new MatchFragmentPagerAdapter(getSupportFragmentManager());
        // add fragments to the view pager
        adapter.addFragment(MatchPageFragment.newInstance(matchedDateList), tabTitles[0]);
        adapter.addFragment(MatchPageFragment.newInstance(matchedFriendList), tabTitles[1]);

        viewPager.setAdapter(adapter);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

}
