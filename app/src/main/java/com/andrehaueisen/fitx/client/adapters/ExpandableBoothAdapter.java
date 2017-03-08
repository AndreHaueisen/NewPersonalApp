package com.andrehaueisen.fitx.client.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.CustomButton;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.PersonalBoothActivity;
import com.andrehaueisen.fitx.client.ScheduleConfirmation;
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher;
import com.andrehaueisen.fitx.models.PersonalDetailed;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalResume;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 12/8/2016.
 */

public class ExpandableBoothAdapter extends ExpandableRecyclerAdapter<PersonalResume, PersonalDetailed, ExpandableBoothAdapter.PersonalResumeParentHolder, ExpandableBoothAdapter.PersonalDetailedChildHolder> {

    private PersonalBoothActivity mActivity;
    private List<PersonalTrainer> mPersonalTrainers;
    private FirebaseProfileImageCatcher mFirebaseProfileImageCatcher;
    private PersonalFitClass mPersonalFitClass;

    public ExpandableBoothAdapter(@NonNull List<PersonalResume> parentList, List<PersonalTrainer> personalTrainers, PersonalBoothActivity activity, PersonalFitClass personalFitClass) {
        super(parentList);

        mActivity = activity;
        mPersonalTrainers = personalTrainers;
        mPersonalFitClass = personalFitClass;
    }

    @NonNull
    @Override
    public PersonalResumeParentHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {

        View parentView = LayoutInflater.from(mActivity).inflate(R.layout.item_personal_resume_parent, parentViewGroup, false);
        PersonalResumeParentHolder holder = new PersonalResumeParentHolder(parentView);
        mFirebaseProfileImageCatcher = new FirebaseProfileImageCatcher(holder);

        return holder;
    }

    @NonNull
    @Override
    public PersonalDetailedChildHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = LayoutInflater.from(mActivity).inflate(R.layout.item_personal_detail_child, childViewGroup, false);

        return new PersonalDetailedChildHolder(childView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull PersonalResumeParentHolder parentViewHolder, int parentPosition, @NonNull PersonalResume parent) {
        parentViewHolder.sendImageCatcherToFirebase(parent);
        parentViewHolder.bindInfoToView(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull PersonalDetailedChildHolder childViewHolder, int parentPosition, int childPosition, @NonNull PersonalDetailed child) {
        childViewHolder.bindInfoToView(child);
    }

    public class PersonalResumeParentHolder extends ParentViewHolder<PersonalResume, PersonalDetailed> implements FirebaseProfileImageCatcher.FirebaseProfileCallback {

        private TextView mNameTextView;
        private TextView mGradeTextView;
        private CircleImageView mProfileCircleImageView;


        public PersonalResumeParentHolder(@NonNull View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.personal_name_text_view);
            mGradeTextView = (TextView) itemView.findViewById(R.id.grade_text_view);
            mProfileCircleImageView = (CircleImageView) itemView.findViewById(R.id.profile_image_view);
        }

        private void sendImageCatcherToFirebase(PersonalResume personalResume){
            mFirebaseProfileImageCatcher.getPersonalProfilePicture(mActivity, personalResume.getPersonalKey());
        }

        private void bindInfoToView(PersonalResume personalResume){

            mNameTextView.setText(personalResume.getName());
            String grade = Utils.formatGrade(personalResume.getGrade());
            mGradeTextView.setText(grade);
        }

        @Override
        public void onProfileImageReady(byte[] personProfileImage) {
            if(personProfileImage != null) {
                Glide.with(mActivity).load(personProfileImage).asBitmap().into(mProfileCircleImageView);
            }else {
                Glide.with(mActivity).load(R.drawable.head_placeholder).into(mProfileCircleImageView);
            }
        }

        @Override
        public void onFrontBodyImageReady(byte[] personFrontImage) {

        }

        @Override
        public void onPersonalPicsReady(String classKey, ArrayList<byte[]> personPhotos) {

        }

        @Override
        public void onProfileImageReady(byte[] personProfileImage, int positionOnArray) {

        }

        @Override
        public void onProfileImageReady(byte[] personProfileImage, String personalKey) {

        }
    }

    public class PersonalDetailedChildHolder extends ChildViewHolder<PersonalDetailed> {

        private TextView mClassCounterTextView;
        private TextView mCREFTextView;
        private CustomButton mScheduleClassButton;

        public PersonalDetailedChildHolder(@NonNull View itemView) {
            super(itemView);

            mClassCounterTextView = (TextView) itemView.findViewById(R.id.class_counter_text_view);
            mCREFTextView = (TextView) itemView.findViewById(R.id.cref_number_text_view);
            mScheduleClassButton = (CustomButton) itemView.findViewById(R.id.schedule_class_button);
        }

        private void bindInfoToView(PersonalDetailed personalDetailed){

            mClassCounterTextView.setText(mActivity.getString(R.string.class_number, new Integer(personalDetailed.getReviewCounter()).toString()));
            mCREFTextView.setText(personalDetailed.getCref());

            final PersonalTrainer personalTrainer = mPersonalTrainers.get(getChildAdapterPosition());

            mScheduleClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScheduleConfirmation scheduleConfirmation = new ScheduleConfirmation(mActivity, personalTrainer, mPersonalFitClass);
                    scheduleConfirmation.makeAppointment();

                }
            });
        }
    }


}
