package com.andrehaueisen.fitx.personal.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.personal.drawer.ScheduleArrangementFragment;
import com.andrehaueisen.fitx.personal.drawer.WeekScheduleResumeFragment;

import java.util.ArrayList;

/**
 * Created by andre on 9/24/2016.
 */

public class WeekDayAgendaPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;

    public WeekDayAgendaPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        mFragments = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.WEEK_DAY_BUNDLE_KEY, i);

            ScheduleArrangementFragment fragment = ScheduleArrangementFragment.newInstance(bundle);
            mFragments.add(fragment);
        }

        Fragment weekScheduleFragment = WeekScheduleResumeFragment.newInstance();
        mFragments.add(weekScheduleFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return 8;
    }
}
