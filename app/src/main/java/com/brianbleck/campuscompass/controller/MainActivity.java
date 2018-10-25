package com.brianbleck.campuscompass.controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.view.MainMenuFragment;
import com.brianbleck.campuscompass.view.MapsFragment;
import com.brianbleck.campuscompass.view.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static final int MAIN_MENU_FRAG_PAGER_NUMBER = 0;
    public static final int MAPS_FRAG_PAGER_NUMBER = 1;
    public static final int SEARCH_FRAG_PAGER_NUMBER = 2;
    private SectionsStatePagerAdapter mSSPagerAdapter;
    private ViewPager mViewPager;
    private int callingViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
    }

    private void initFields() {
        mSSPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupPager(mViewPager);
    }

    private void setupPager(ViewPager pager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainMenuFragment(), "MainMenuFragment");
        adapter.addFragment(new MapsFragment(), "MapsFragment");
        adapter.addFragment(new SearchFragment(), "SearchFragment");
        pager.setAdapter(adapter);
    }

    public void setmViewPager(int fragmentNumber, int id){
        callingViewId = id;
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public int getCallingViewId() {
        return callingViewId;
    }

}
