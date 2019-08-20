package com.dropout.dropmeme.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropout.dropmeme.Adapters.TrendPagerAdapter;
import com.dropout.dropmeme.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout trendTabs;
    private TrendPagerAdapter adapter;
    private View view;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trending, container, false);

        viewPager = view.findViewById(R.id.trending_pager);
        trendTabs = view.findViewById(R.id.trending_tabs);
        adapter = new TrendPagerAdapter(getFragmentManager());
        trendTabs.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

        return view;
    }

}
