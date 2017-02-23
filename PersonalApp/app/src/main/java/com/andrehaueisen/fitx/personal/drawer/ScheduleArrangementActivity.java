package com.andrehaueisen.fitx.personal.drawer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.adapters.AgendaAdapter;
import com.andrehaueisen.fitx.personal.adapters.WeekDayAgendaPagerAdapter;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;
import com.touchboarder.weekdaysbuttons.WeekdaysDrawableProvider;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleArrangementActivity extends AppCompatActivity implements WeekdaysDataSource.Callback{

    private ViewPager mViewPager;
    private int mLastPagerItem = 0;
    private int mDayOfWeek;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_arrangement);

        mToolbar = (Toolbar) findViewById(R.id.agenda_toolbar);
        setSupportActionBar(mToolbar);

        WeekdaysDataSource weekdaysDataSource = new WeekdaysDataSource(this, R.id.weekdays_stub);

        weekdaysDataSource
                .setDrawableType(WeekdaysDrawableProvider.MW_ROUND)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setNumberOfLetters(2)
                .setFontTypeFace(Typeface.SANS_SERIF)
                .setSelectedColor(getResources().getColor(R.color.colorPrimaryDark))
                .setUnselectedColor(getResources().getColor(R.color.colorPrimaryDark))
                .start(this);

        mViewPager = (ViewPager) findViewById(R.id.week_day_pager);
        WeekDayAgendaPagerAdapter pagerAdapter = new WeekDayAgendaPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(pagerAdapter);

    }


    @Override
    public void onWeekdaysItemClicked(int i, WeekdaysDataItem weekdaysDataItem) {

        mDayOfWeek = weekdaysDataItem.getCalendarDayId();

        switch (mDayOfWeek){
            case Calendar.MONDAY:
                mViewPager.setCurrentItem(0, true);
                mLastPagerItem = 0;
                break;
            case Calendar.TUESDAY:
                mViewPager.setCurrentItem(1, true);
                mLastPagerItem = 1;
                break;
            case Calendar.WEDNESDAY:
                mViewPager.setCurrentItem(2, true);
                mLastPagerItem = 2;
                break;
            case Calendar.THURSDAY:
                mViewPager.setCurrentItem(3, true);
                mLastPagerItem = 3;
                break;
            case Calendar.FRIDAY:
                mViewPager.setCurrentItem(4, true);
                mLastPagerItem = 4;
                break;
            case Calendar.SATURDAY:
                mViewPager.setCurrentItem(5, true);
                mLastPagerItem = 5;
                break;
            case Calendar.SUNDAY:
                mViewPager.setCurrentItem(6, true);
                mLastPagerItem = 6;
                break;

            default:
                mViewPager.setCurrentItem(0, true);
                mLastPagerItem = 0;
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agenda_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuId = item.getItemId();
        switch (menuId){

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_change_view_menu_button:


                if(mViewPager.getCurrentItem() == 7) {

                    mViewPager.setCurrentItem(mLastPagerItem, false);

                    final Animation toWeekViewRotateAnimation = new RotateAnimation(-90.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    toWeekViewRotateAnimation.setDuration(400);
                    toWeekViewRotateAnimation.setFillAfter(true);

                    mToolbar.findViewById(R.id.action_change_view_menu_button).startAnimation(toWeekViewRotateAnimation);

                }else {
                    mViewPager.setCurrentItem(7, false);

                    final Animation fromWeekViewRotateAnimation = new RotateAnimation(0.0f, -90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    fromWeekViewRotateAnimation.setDuration(400);
                    fromWeekViewRotateAnimation.setFillAfter(true);

                    mToolbar.findViewById(R.id.action_change_view_menu_button).startAnimation(fromWeekViewRotateAnimation);
                }

                return true;

            case R.id.action_confirm_changes_menu_button:
                saveScheduleToFirebase();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveScheduleToFirebase(){

        boolean isAllClear = true;

        WeekDayAgendaPagerAdapter pagerAdapter = (WeekDayAgendaPagerAdapter) mViewPager.getAdapter();
        ArrayList<AgendaAdapter> agendaAdapters = new ArrayList<>();

        for(int i = 0; i < pagerAdapter.getCount()-1; i++){
            ScheduleArrangementFragment arrangementFragment = (ScheduleArrangementFragment) pagerAdapter.getItem(i);

            if(!arrangementFragment.hasTimeConflicts()){
                agendaAdapters.add(arrangementFragment.getScheduleAdapter());
            }else{
                isAllClear = false;
            }
        }

        if(isAllClear) {
            PersonalDatabase.savePersonalScheduleToDatabase(this, agendaAdapters);
            Utils.generateSuccessToast(this, getString(R.string.agenda_saved)).show();
        }
    }


    @Override
    public void onWeekdaysSelected(int i, ArrayList<WeekdaysDataItem> arrayList) {}
}
