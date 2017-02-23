package com.andrehaueisen.fitx.client.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.andrehaueisen.fitx.CustomButton;
import com.andrehaueisen.fitx.R;

/**
 * Created by andre on 2/21/2017.
 */

public class PersonalSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_personal_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CustomButton generalSearchButton = (CustomButton) findViewById(R.id.general_search_button);
        CustomButton specificSearchButton = (CustomButton) findViewById(R.id.specific_search_button);

        initializeFragment();

        generalSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));
                Fragment specificSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.specific_personal_search_fragment_tag));

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.show(generalSearchFragment);
                transaction.hide(specificSearchFragment);
                transaction.commit();

            }
        });

        specificSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));
                Fragment specificSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.specific_personal_search_fragment_tag));

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.show(specificSearchFragment);
                transaction.hide(generalSearchFragment);
                transaction.commit();
            }
        });

    }

    private void initializeFragment(){

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));
        Fragment specificSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.specific_personal_search_fragment_tag));

        if(generalSearchFragment == null) {
            Fragment fragment = GeneralPersonalSearchFragment.newInstance();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.general_search_fragment_container, fragment, getString(R.string.general_personal_search_fragment_tag));
            transaction.hide(fragment);
            transaction.commit();
        }

        if(specificSearchFragment == null) {
            Fragment fragment = SpecificPersonalSearchFragment.Companion.newInstance();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.specific_search_fragment_container, fragment, getString(R.string.specific_personal_search_fragment_tag));
            transaction.hide(fragment);
            transaction.commit();
        }

    }
}
