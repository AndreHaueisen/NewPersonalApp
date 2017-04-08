package com.andrehaueisen.fitx.client.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.andrehaueisen.fitx.utilities.Constants;
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

        initializeFragment();

    }

    private void initializeFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));

        if (generalSearchFragment == null) {
            Fragment fragment = GeneralPersonalSearchFragment.newInstance();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.general_search_fragment_container, fragment, getString(R.string.general_personal_search_fragment_tag));
            transaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            configureSearchView(searchView);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));

                if(!generalSearchFragment.isVisible()) {
                    goBackToGeneralSearch();
                }else {
                    finish();
                }

                return true;

            default: return false;
        }
    }

    private void goBackToGeneralSearch(){
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));
        Fragment specificSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.specific_personal_search_fragment_tag));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(generalSearchFragment);
        if(specificSearchFragment != null) {
            transaction.hide(specificSearchFragment);
        }
        transaction.commit();
    }

    private void configureSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment generalSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.general_personal_search_fragment_tag));
                Fragment specificSearchFragment = fragmentManager.findFragmentByTag(getString(R.string.specific_personal_search_fragment_tag));

                if (specificSearchFragment != null) {
                    fragmentManager.beginTransaction().remove(specificSearchFragment).commitNow();
                }

                Bundle bundle = new Bundle();
                bundle.putString(Constants.SEARCH_BUNDLE_KEY, query);
                Fragment fragment = PersonalSearchFragment.Companion.newInstance(bundle);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.specific_search_fragment_container, fragment, getString(R.string.specific_personal_search_fragment_tag));
                transaction.show(fragment);
                transaction.hide(generalSearchFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
