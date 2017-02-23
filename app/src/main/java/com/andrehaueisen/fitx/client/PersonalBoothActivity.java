package com.andrehaueisen.fitx.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.adapters.ExpandableBoothAdapter;
import com.andrehaueisen.fitx.pojo.PersonalDetailed;
import com.andrehaueisen.fitx.pojo.PersonalFitClass;
import com.andrehaueisen.fitx.pojo.PersonalResume;
import com.andrehaueisen.fitx.pojo.PersonalTrainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 10/10/2016.
 */

public class PersonalBoothActivity extends AppCompatActivity{

    private ArrayList<PersonalTrainer> mPersonalTrainers;
    private PersonalFitClass mPersonalFitClass;

    private ExpandableBoothAdapter mExpandableBoothAdapter;

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

        List<PersonalResume> personalResumes;

        personalResumes = configureExpandableAdapterInfo();

        mExpandableBoothAdapter = new ExpandableBoothAdapter(personalResumes, mPersonalTrainers, this, mPersonalFitClass);
        trainersRecyclerView.setAdapter(mExpandableBoothAdapter);
    }

    private List<PersonalResume> configureExpandableAdapterInfo(){


        List<PersonalResume> personalResumes = new ArrayList<>();

        for(PersonalTrainer personalTrainer : mPersonalTrainers) {

            List<PersonalDetailed> personalDetaileds = new ArrayList<>();

            PersonalDetailed personalDetailed = new PersonalDetailed();
            personalDetailed.setCref(personalTrainer.getCref());
            personalDetailed.setReviewCounter(personalTrainer.getReviewCounter());

            personalDetaileds.add(personalDetailed);

            String name = personalTrainer.getName();
            float grade = personalTrainer.getGrade();
            String key = Utils.encodeEmail(personalTrainer.getEmail());

            PersonalResume personalResume = new PersonalResume(name, grade, personalDetaileds, key);

            personalResumes.add(personalResume);
        }

        return personalResumes;
    }
}
