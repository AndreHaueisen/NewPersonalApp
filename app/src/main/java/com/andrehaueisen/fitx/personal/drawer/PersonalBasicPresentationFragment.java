package com.andrehaueisen.fitx.personal.drawer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.personal.drawer.dialogFragment.PictureSelectionMethodDialogFragment;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.andrehaueisen.fitx.Utils.getSharedPreferences;
import static com.bumptech.glide.Glide.with;

/**
 * Created by andre on 9/25/2016.
 */

public class PersonalBasicPresentationFragment extends Fragment implements ProfessionalProfileActivity.OnProfileDataReadyCallback {

    private final String TAG = PersonalBasicPresentationFragment.class.getSimpleName();
    private final int REQUEST_CODE = 0;

    private CircleImageView mProfileImage;
    private PersonalTrainer mPersonalTrainer;
    private TextView mGradeTextView;
    private TextView mReviewCounterTextView;
    private TextInputLayout mPriceTextInputLayout;
    private String mPhotoUriPath;
    private int mLastClickedImageViewCode;

    private PictureSelectionMethodDialogFragment mPictureSelectionDialogFragment;

    public static Fragment newInstance() {
        return new PersonalBasicPresentationFragment();
    }

    public static Fragment newInstance(Bundle bundle){
        PersonalBasicPresentationFragment personalBasicPresentationFragment = new PersonalBasicPresentationFragment();
        personalBasicPresentationFragment.setArguments(bundle);

        return personalBasicPresentationFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle bundle = getArguments();

        if(bundle != null) {
            mPersonalTrainer = getArguments().getParcelable(Constants.PERSONAL_BUNDLE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_basic_presentation, container, false);

        Activity activity = getActivity();
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image_view);
        mProfileImage.setOnClickListener(mOnImageClickListener);
        setProfileImage();

        mGradeTextView = (TextView) view.findViewById(R.id.personal_grade_text_view);
        mReviewCounterTextView = (TextView) view.findViewById(R.id.review_counter_text_view);

        mPriceTextInputLayout = (TextInputLayout) view.findViewById(R.id.price_text_input_layout);
        configureEditTextBehaviour();

        if(mPersonalTrainer != null){
            bindInfoToViews();
        }else{
            fetchPersonalOnDatabase();
        }

        return view;
    }

    private void setProfileImage(){
        mPhotoUriPath = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_PHOTO_URI_PATH, null);

        if(mPhotoUriPath == null){
            with(getContext()).load(R.drawable.head_placeholder).into(mProfileImage);
        }else {
            Uri photoUri = Uri.parse(mPhotoUriPath);
            with(getContext()).load(photoUri).into(mProfileImage);
        }
    }

    private View.OnClickListener mOnImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");

            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);

            int imageId = view.getId();
            Bundle bundle = new Bundle();

            switch (imageId) {
                case R.id.profile_image_view:
                    mLastClickedImageViewCode = Constants.PERSONAL_PROFILE_PICTURE;
                    bundle.putInt(Constants.IMAGE_CODE_BUNDLE_KEY, Constants.PERSONAL_PROFILE_PICTURE);
                    break;

            }

            mPictureSelectionDialogFragment = PictureSelectionMethodDialogFragment.newInstance(bundle);
            mPictureSelectionDialogFragment.setTargetFragment(PersonalBasicPresentationFragment.this, REQUEST_CODE);
            mPictureSelectionDialogFragment.show(fragmentTransaction, "dialog");
        }
    };

    private void configureEditTextBehaviour(){
        final EditText editText = mPriceTextInputLayout.getEditText();
        editText.addTextChangedListener(mPriceTextWatcher);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editText.setCursorVisible(false);
                return false;
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setCursorVisible(true);
            }
        });
    }

    private void bindInfoToViews(){
        mGradeTextView.setText(Utils.formatGrade(mPersonalTrainer.getGrade()));
        mReviewCounterTextView.setText(String.valueOf(mPersonalTrainer.getReviewCounter()));
        if(mPersonalTrainer.get15MinutePrice() != 0){
            mPriceTextInputLayout.getEditText().setText(String.valueOf(mPersonalTrainer.get15MinutePrice()));
        }
    }

    private TextWatcher mPriceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable price) {
            if(price != null && !price.toString().isEmpty()) {
                mPersonalTrainer.set15MinutePrice(Integer.parseInt(price.toString()));
            }
        }
    };

    private void fetchPersonalOnDatabase(){
            String personalKey = getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).child(personalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPersonalTrainer = dataSnapshot.getValue(PersonalTrainer.class);
                    bindInfoToViews();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void saveDataOnDatabase() {
        SharedPreferences.Editor editor = Utils.getSharedPreferences(getContext()).edit();
        editor.putString(Constants.SHARED_PREF_PERSONAL_PHOTO_URI_PATH, mPhotoUriPath).apply();

        Bitmap profilePicture;
        if(mProfileImage.getDrawable().getCurrent() instanceof GlideBitmapDrawable) {
            profilePicture = ((GlideBitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        }else{
            profilePicture = ((BitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        }
        PersonalDatabase.saveProfilePicsToFirebase(getActivity(), profilePicture);
        PersonalDatabase.savePersonalToDatabase(getActivity(), mPersonalTrainer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri photoUri = mPictureSelectionDialogFragment.getProfilePicsUri();
            File photoFile = mPictureSelectionDialogFragment.getImageFile();

            placeImageOnImageView(photoUri, photoFile);

        } else if (requestCode == Constants.REQUEST_IMAGE_LOAD && resultCode == Activity.RESULT_OK){

            Uri photoUri = data.getData();
            placeImageOnImageView(photoUri, null);
        }

    }

    private void placeImageOnImageView(Uri photoUri, @Nullable File photoFile) {

        mPhotoUriPath = photoUri.toString();
        if (photoFile != null) {

            String uriLastPath = photoUri.getLastPathSegment();
            switch (uriLastPath) {
                case Constants.PERSONAL_PROFILE_PICTURE_NAME:

                    mProfileImage.setBackground(null);
                    with(this).load(photoFile).asBitmap().into(mProfileImage);
                    break;

                default:
                    Log.e(PersonalBasicPresentationFragment.class.getSimpleName(), "Image code not found");
            }
        } else {

            switch (mLastClickedImageViewCode){
                case Constants.PERSONAL_PROFILE_PICTURE:

                    with(this).loadFromMediaStore(photoUri).asBitmap().into(mProfileImage);
                    break;

                default:
                    Log.e(PersonalBasicPresentationFragment.class.getSimpleName(), "Image code not found");
            }

        }
    }

}
