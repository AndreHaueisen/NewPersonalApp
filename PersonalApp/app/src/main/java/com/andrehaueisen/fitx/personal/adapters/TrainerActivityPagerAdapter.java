package com.andrehaueisen.fitx.personal.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.personal.MyClientsFragment;
import com.andrehaueisen.fitx.personal.ToBeConfirmedClassesFragment;
import com.andrehaueisen.fitx.personal.UpcomingClassesFragment;

/**
 * Created by andre on 9/8/2016.
 */

public class TrainerActivityPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private FragmentManager mFragmentManager;

    public TrainerActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

            switch (position) {
                case 0:
                    fragment = mFragmentManager.findFragmentByTag(mContext.getString(R.string.to_be_confirmed_classes_fragment_tag));
                    //fragment = mFragmentManager.findFragmentByTag(mContext.getString(R.string.upcoming_classes_fragment_tag));

                    if(fragment == null) {
                        fragment = ToBeConfirmedClassesFragment.newInstance();
                       // fragment = UpcomingClassesFragment.newInstance();
                    }
                    break;

                case 1:
                    fragment = MyClientsFragment.newInstance();
                    break;
                default:
                    fragment = UpcomingClassesFragment.newInstance();
            }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return mContext.getString(R.string.classes);
            case 1:
                return mContext.getString(R.string.my_clients);
            default:
                return mContext.getString(R.string.classes);
        }
    }
}
