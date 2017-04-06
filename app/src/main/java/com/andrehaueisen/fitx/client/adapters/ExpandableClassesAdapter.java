package com.andrehaueisen.fitx.client.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.models.Review;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 10/24/2016.
 */

public class ExpandableClassesAdapter extends RecyclerView.Adapter<ExpandableClassesAdapter.ClassResumeParentHolder>{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ClassReceipt> mClassReceipts;

    public ExpandableClassesAdapter(Context context, ArrayList<ClassReceipt> classReceipts) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mClassReceipts = classReceipts;
    }

    @Override
    public ClassResumeParentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View parentView = mLayoutInflater.inflate(R.layout.item_class_resume_parent, parent, false);
        return new ClassResumeParentHolder(parentView);
    }

    @Override
    public void onBindViewHolder(final ClassResumeParentHolder holder, final int position) {

        ClassReceipt classReceipt = mClassReceipts.get(position);

        Bitmap personalProfileImage = mClassReceipts.get(position).getPersonalProfileImage();
        Bitmap personalBackgroundImage = classReceipt.getPersonalBackgroundImage();

        if(personalProfileImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            personalProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(holder.mHeadPhotoImageView);

        }else {
            Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(holder.mHeadPhotoImageView);
        }

        if(personalBackgroundImage != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            personalBackgroundImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.personal_background_placeholder)).into(holder.mBackgroundImageView);
        }else{
            Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.personal_background_placeholder)).into(holder.mBackgroundImageView);
        }

        holder.mPersonalNameTextView.setText(classReceipt.getPersonalName());
        holder.mDateTextView.setText(Utils.getWrittenDateFromDateCode(mContext, classReceipt.getDateCode()));
        holder.mLocationTextView.setText(classReceipt.getPlaceName());

        holder.fetchPersonalGrade(position);

        holder.mSubmitReviewChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.mReviewTextInputLayout.getEditText().length() <=  holder.mReviewTextInputLayout.getCounterMaxLength()) {
                    Review review = new Review();
                    review.setGrade(holder.mRatingBar.getRating());
                    review.setText(holder.mReview);

                    ClassReceipt classReceipt = mClassReceipts.get(position);
                    ClientDatabase.saveReview(review, classReceipt.getPersonalKey(), classReceipt.getClassKey(), classReceipt.getClientKey(), mClassReceipts);

                    mClassReceipts.remove(position);
                    notifyItemRemoved(position);

                    if(mClassReceipts.isEmpty()){
                        ((Activity) mContext).finish();
                    }

                    Utils.generateSuccessToast(mContext, mContext.getString(R.string.review_submitted)).show();
                } else {
                    Utils.generateErrorToast(mContext, mContext.getString(R.string.review_too_long)).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mClassReceipts.size();
    }

    public class ClassResumeParentHolder extends RecyclerView.ViewHolder implements TextWatcher{

        @BindView(R.id.expandable_card_view) CardView mCardView;
        @BindView(R.id.background_image_view) ImageView mBackgroundImageView;
        @BindView(R.id.personal_head_photo_circle_view) CircleImageView mHeadPhotoImageView;
        @BindView(R.id.personal_name_text_view) TextView mPersonalNameTextView;
        @BindView(R.id.class_location_name_text_view) TextView mLocationTextView;
        @BindView(R.id.class_date_text_view) TextView mDateTextView;

        @BindView(R.id.grade_title_text_view) TextView mGradeTitleTextView;
        @BindView(R.id.personal_grade_text_view) TextView mPersonalGradeTextView;
        @BindView(R.id.rate_personal_rating_bar) RatingBar mRatingBar;
        @BindView(R.id.review_text_input_layout) TextInputLayout mReviewTextInputLayout;
        @BindView(R.id.submit_button) Button mSubmitReviewChildButton;

        private String mReview;

        public ClassResumeParentHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPersonalGradeTextView.getVisibility() == View.GONE){
                        mGradeTitleTextView.setVisibility(View.VISIBLE);
                        mPersonalGradeTextView.setVisibility(View.VISIBLE);
                        mRatingBar.setVisibility(View.VISIBLE);
                        mReviewTextInputLayout.setVisibility(View.VISIBLE);
                        mSubmitReviewChildButton.setVisibility(View.VISIBLE);
                    } else {
                        mGradeTitleTextView.setVisibility(View.GONE);
                        mPersonalGradeTextView.setVisibility(View.GONE);
                        mRatingBar.setVisibility(View.GONE);
                        mReviewTextInputLayout.setVisibility(View.GONE);
                        mSubmitReviewChildButton.setVisibility(View.GONE);
                    }
                }
            });

            mReviewTextInputLayout.getEditText().addTextChangedListener(this);
            mReview = "";
        }

        private void fetchPersonalGrade(int position){

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            ClassReceipt classReceipt = mClassReceipts.get(position);

            databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).child(classReceipt.getPersonalKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PersonalTrainer personalTrainer = dataSnapshot.getValue(PersonalTrainer.class);

                    String grade = Utils.formatGrade(personalTrainer.getGrade());
                    mPersonalGradeTextView.setText(grade);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mReview = editable.toString();
        }
    }
}
