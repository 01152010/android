package com.upad.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ServiceFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();

    public ServiceFragmentAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList.addAll(fragmentList);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
