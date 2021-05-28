package com.munib.spotme.adapters;

import com.munib.spotme.fragments.MyDealsBorrowedFragment;
import com.munib.spotme.fragments.MyDealsLendedFragment;
import com.munib.spotme.fragments.MyDealsPendingFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyDealsViewPager extends FragmentPagerAdapter {

    public MyDealsViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new MyDealsPendingFragment();
        }
        else if (position == 1)
        {
            fragment = new MyDealsLendedFragment();
        }
        else if (position == 2)
        {
            fragment = new MyDealsBorrowedFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Pending";
        }
        else if (position == 1)
        {
            title = "LOANED";
        }
        else if (position == 2)
        {
            title = "Borrowed";
        }
        return title;
    }
}