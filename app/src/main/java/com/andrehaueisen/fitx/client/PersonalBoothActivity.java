package com.andrehaueisen.fitx.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.client.adapters.ExpandableBoothAdapter;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalTrainer;

import java.util.ArrayList;

/**
 * Created by andre on 10/10/2016.
 */

public class PersonalBoothActivity extends AppCompatActivity{

    private ArrayList<PersonalTrainer> mPersonalTrainers;
    private PersonalFitClass mPersonalFitClass;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_booth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.personal_booth_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        mPersonalTrainers = intent.getParcelableArrayListExtra(Constants.PERSONAL_LIST_EXTRA_KEY);
        mPersonalFitClass = intent.getParcelableExtra(Constants.FIT_CLASS_EXTRA_KEY);

        setAdapter();

    }

    private void setAdapter(){

        RecyclerView trainersRecyclerView = (RecyclerView) findViewById(R.id.personal_booth_recycler_view);
        trainersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainersRecyclerView.setHasFixedSize(true);

        ExpandableBoothAdapter expandableBoothAdapter = new ExpandableBoothAdapter( mPersonalTrainers, this, mPersonalFitClass);
        trainersRecyclerView.setAdapter(expandableBoothAdapter);
    }


}
