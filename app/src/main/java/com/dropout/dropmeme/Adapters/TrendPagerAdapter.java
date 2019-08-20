package com.dropout.dropmeme.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dropout.dropmeme.TrendingFragments.FamousFragment;
import com.dropout.dropmeme.TrendingFragments.FunnyFragment;
import com.dropout.dropmeme.TrendingFragments.HotFragment;
import com.dropout.dropmeme.TrendingFragments.LatestFragment;
import com.dropout.dropmeme.TrendingFragments.MostLikedFragment;

public class TrendPagerAdapter extends FragmentStatePagerAdapter {

    public TrendPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new LatestFragment();
            case 1:
                return new HotFragment();
            case 2:
                return new FamousFragment();
            case 3:
                return new MostLikedFragment();
            case 4:
                return new FunnyFragment();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Latest";
            case 1:
                return "Hot";
            case 2:
                return "Famous";
            case 3:
                return "Most Liked";
            case 4:
                return "Funny Memes";
                default:
                    return super.getPageTitle(position);
        }

    }
}
