package com.andrehaueisen.fitx.client.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 12/8/2016.
 */

public class ExpandableBoothAdapter extends RecyclerView.Adapter<ExpandableBoothAdapter.PersonalDetailedHolder> {

    private PersonalBoothActivity mActivity;
    private List<PersonalTrainer> mPersonalTrainers;
    private FirebaseProfileImageCatcher mFirebaseProfileImageCatcher;
    private PersonalFitClass mPersonalFitClass;

    public ExpandableBoothAdapter(List<PersonalTrainer> personalTrainers, PersonalBoothActivity activity, PersonalFitClass personalFitClass) {

        mActivity = activity;
        mPersonalTrainers = personalTrainers;
        mPersonalFitClass = personalFitClass;
    }

    @Override
    public PersonalDetailedHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View parentView = LayoutInflater.from(mActivity).inflate(R.layout.item_personal_resume_parent, parent, false);
        PersonalDetailedHolder holder = new PersonalDetailedHolder(parentView);
        mFirebaseProfileImageCatcher = new FirebaseProfileImageCatcher(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(PersonalDetailedHolder holder, int position) {
        holder.bindInfoToView( mPersonalTrainers.get(position) );
    }

    @Override
    public int getItemCount() {
        return mPersonalTrainers.size();
    }

    public class PersonalDetailedHolder extends RecyclerView.ViewHolder implements FirebaseProfileImageCatcher.FirebaseProfileCallback {

        @BindView(R.id.card_view) CardView mCardView;
        @BindView(R.id.personal_name_text_view) TextView mNameTextView;
        @BindView(R.id.grade_text_view) TextView mGradeTextView;
        @BindView(R.id.profile_image_view) CircleImageView mProfileCircleImageView;
        @BindView(R.id.cref_title_text_view) TextView mCrefTitle;
        @BindView(R.id.cref_number_text_view) TextView mCREFTextView;
        @BindView(R.id.class_counter_text_view) TextView mClassCounterTextView;
        @BindView(R.id.schedule_class_button) CustomButton mScheduleClassButton;

        public PersonalDetailedHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCREFTextView.getVisibility() == View.GONE){
                        mCrefTitle.setVisibility(View.VISIBLE);
                        mCREFTextView.setVisibility(View.VISIBLE);
                        mClassCounterTextView.setVisibility(View.VISIBLE);
                        mScheduleClassButton.setVisibility(View.VISIBLE);
                    } else {
                        mCrefTitle.setVisibility(View.GONE);
                        mCREFTextView.setVisibility(View.GONE);
                        mClassCounterTextView.setVisibility(View.GONE);
                        mScheduleClassButton.setVisibility(View.GONE);
                    }
                }
            });
        }

        private void sendImageCatcherToFirebase(PersonalTrainer personalTrainer){
            mFirebaseProfileImageCatcher.getPersonalProfilePicture( mActivity, Utils.encodeEmail( personalTrainer.getEmail() ) );
        }

        private void bindInfoToView(final PersonalTrainer personalTrainer){

            sendImageCatcherToFirebase(personalTrainer);

            mClassCounterTextView.setText(mActivity.getString(R.string.class_number,  Integer.valueOf(personalTrainer.getReviewCounter()).toString()));
            mCREFTextView.setText(personalTrainer.getCref());


            mScheduleClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScheduleConfirmation scheduleConfirmation = new ScheduleConfirmation(mActivity, personalTrainer, mPersonalFitClass);
                    scheduleConfirmation.makeAppointment();

                }
            });

            mNameTextView.setText(personalTrainer.getName());
            String grade = Utils.formatGrade(personalTrainer.getGrade());
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

    }


}
