package com.andrehaueisen.fitx.client.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.andrehaueisen.fitx.models.ClassDetailed;
import com.andrehaueisen.fitx.models.ClassReceipt;
import com.andrehaueisen.fitx.models.ClassResume;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.models.Review;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 10/24/2016.
 */

public class ExpandableClassesAdapter extends ExpandableRecyclerAdapter<ClassResume, ClassDetailed, ExpandableClassesAdapter.ClassResumeParentHolder, ExpandableClassesAdapter.ClassDetailChildHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ClassReceipt> mClassReceipts;

    public ExpandableClassesAdapter(@NonNull List<ClassResume> parentList, Context context, ArrayList<ClassReceipt> classReceipts) {
        super(parentList);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mClassReceipts = classReceipts;
    }


    @NonNull
    @Override
    public ClassResumeParentHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {

        View parentView = mLayoutInflater.inflate(R.layout.item_class_resume_parent, parentViewGroup, false);
        return new ClassResumeParentHolder(parentView);
    }

    @NonNull
    @Override
    public ClassDetailChildHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {

        View childView = mLayoutInflater.inflate(R.layout.item_class_detail_child, childViewGroup, false);

        return new ClassDetailChildHolder(childView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindParentViewHolder(@NonNull ClassResumeParentHolder parentViewHolder, int parentPosition, @NonNull ClassResume parent) {

        Bitmap personalProfileImage = mClassReceipts.get(parentPosition).getPersonalProfileImage();

        if(personalProfileImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            personalProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(parentViewHolder.mHeadPhotoImageView);

        }else {
            Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(parentViewHolder.mHeadPhotoImageView);
        }

        parentViewHolder.mPersonalNameTextView.setText(parent.getPersonalName());
        parentViewHolder.mDateTextView.setText(parent.getClassDate());
        parentViewHolder.mLocationTextView.setText(parent.getClassLocation());
    }

    @Override
    public void onBindChildViewHolder(@NonNull final ClassDetailChildHolder childViewHolder, final int parentPosition, final int childPosition, @NonNull final ClassDetailed child) {

        Bitmap personalProfileImage = mClassReceipts.get(childPosition).getPersonalProfileImage();
        Bitmap personalBackgroundImage = mClassReceipts.get(parentPosition).getPersonalBackgroundImage();

        if(personalProfileImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            personalProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(childViewHolder.mHeadPhotoChildImageView);
        }else {
            Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(childViewHolder.mHeadPhotoChildImageView);
        }

        if(personalBackgroundImage != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            personalBackgroundImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.personal_background_placeholder)).into(childViewHolder.mBackgroundImageView);
        }else{
            Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.personal_background_placeholder)).into(childViewHolder.mBackgroundImageView);
        }

        childViewHolder.fetchPersonalGrade(childPosition);

        childViewHolder.mPersonalNameChildTextView.setText(child.getPersonalName());
        childViewHolder.mDateChildTextView.setText(child.getClassDate());
        childViewHolder.mLocationChildTextView.setText(child.getClassLocation());
        childViewHolder.mSubmitReviewChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(childViewHolder.mReviewTextInputLayout.getEditText().length() <=  childViewHolder.mReviewTextInputLayout.getCounterMaxLength()) {
                    Review review = new Review();
                    review.setGrade(childViewHolder.mChildRatingBar.getRating());
                    review.setText(childViewHolder.mReview);

                    ClassReceipt classReceipt = mClassReceipts.get(childPosition);
                    ClientDatabase.saveReview(review, classReceipt.getPersonalKey(), classReceipt.getClassKey(), classReceipt.getClientKey(), mClassReceipts);

                    getParentList().remove(parentPosition);
                    notifyParentRemoved(parentPosition);

                    if(getParentList().isEmpty()){
                        ((Activity) mContext).finish();
                    }

                    Utils.generateSuccessToast(mContext, mContext.getString(R.string.review_submitted)).show();
                } else {
                    Utils.generateErrorToast(mContext, mContext.getString(R.string.review_too_long)).show();
                }
            }
        });

    }

    public class ClassResumeParentHolder extends ParentViewHolder<ClassResume, ClassDetailed>{

        private CircleImageView mHeadPhotoImageView;
        private TextView mPersonalNameTextView;
        private TextView mDateTextView;
        private TextView mLocationTextView;

        public ClassResumeParentHolder(@NonNull View itemView) {
            super(itemView);

            mHeadPhotoImageView = (CircleImageView) itemView.findViewById(R.id.personal_head_photo_circle_view);
            mPersonalNameTextView = (TextView) itemView.findViewById(R.id.personal_name_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.class_date_text_view);
            mLocationTextView = (TextView) itemView.findViewById(R.id.class_location_name_text_view);
        }
    }

    public class ClassDetailChildHolder extends ChildViewHolder<ClassDetailed> implements TextWatcher{

        private ImageView mBackgroundImageView;
        private CircleImageView mHeadPhotoChildImageView;
        private TextView mPersonalNameChildTextView;
        private TextView mDateChildTextView;
        private TextView mPersonalGradeChildTextView;
        private TextView mLocationChildTextView;
        private RatingBar mChildRatingBar;
        private TextInputLayout mReviewTextInputLayout;
        private Button mSubmitReviewChildButton;
        private String mReview;

        public ClassDetailChildHolder(@NonNull View itemView) {
            super(itemView);

            mBackgroundImageView = (ImageView) itemView.findViewById(R.id.background_image_view);
            mHeadPhotoChildImageView = (CircleImageView) itemView.findViewById(R.id.head_photo_image_view);
            mPersonalNameChildTextView = (TextView) itemView.findViewById(R.id.personal_name_text_view);
            mDateChildTextView = (TextView) itemView.findViewById(R.id.class_date_text_view);
            mPersonalGradeChildTextView = (TextView) itemView.findViewById(R.id.personal_grade_text_view);
            mLocationChildTextView = (TextView) itemView.findViewById(R.id.place_name_text_view);
            mChildRatingBar = (RatingBar) itemView.findViewById(R.id.rate_personal_rating_bar);
            mReviewTextInputLayout = (TextInputLayout) itemView.findViewById(R.id.review_text_input_layout);
            mSubmitReviewChildButton = (Button) itemView.findViewById(R.id.submit_button);

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
                    mPersonalGradeChildTextView.setText(grade);
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
