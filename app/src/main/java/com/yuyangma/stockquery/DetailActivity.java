package com.yuyangma.stockquery;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private Button backHomeBtn;
    private List<String> titleList;
    private String symbol;
    private boolean isFavorited;

    private class NumberPagerAdapter extends FragmentPagerAdapter {

        public NumberPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CurrentFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        isFavorited = intent.getBooleanExtra("isFavorited", false);
        Log.i("detail", "symbol:" + symbol + ", favorited:" + isFavorited);

        titleList = new ArrayList<>();
        titleList.add("CURRENT");
        titleList.add("HISTORICAL");
        titleList.add("NEWS");

        //Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        myToolbar.setTitle(symbol);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new NumberPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.view_pager_tab);
//        tabLayout.setSelectedTabIndicatorColor();
        tabLayout.setupWithViewPager(viewPager);



    }
}
