package com.andrehaueisen.fitx.client.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.client.PersonalSearchFragment;
import com.andrehaueisen.fitx.client.ToBeConfirmedClientClassFragment;
import com.andrehaueisen.fitx.client.UpcomingClientClassFragment;

/**
 * Created by Carla on 14/09/2016.
 */

public class SectionPagerAdapterClient extends FragmentStatePagerAdapter {

    private Context mContext;

    public SectionPagerAdapterClient(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = PersonalSearchFragment.newInstance();
                break;
            case 1:
                fragment = UpcomingClientClassFragment.newInstance();
                break;
            case 2:
                fragment = ToBeConfirmedClientClassFragment.newInstance();
                break;
            default:
                fragment = UpcomingClientClassFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return mContext.getString(R.string.find_personal);
            case 1:
                return mContext.getString(R.string.upcoming_classes);
            case 2:
                return mContext.getString(R.string.to_be_confirmed_classes);
            default:
                return mContext.getString(R.string.find_personal);
        }
    }
}
