package com.munib.spotme.adapters;

import com.munib.spotme.fragments.InvestExploreFragment;
import com.munib.spotme.fragments.MyDealsLendedFragment;
import com.munib.spotme.fragments.MyInvestmentsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class InvestmentViewPager extends FragmentPagerAdapter {

    public InvestmentViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new InvestExploreFragment();
        }
        else if (position == 1)
        {
            fragment = new MyInvestmentsFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "EXPLORE";
        }
        else if (position == 1)
        {
            title = "My INVESTMENTS";
        }

        return title;
    }
}