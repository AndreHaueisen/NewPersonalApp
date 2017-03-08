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
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.personal.drawer.dialogFragment.PictureSelectionMethodDialogFragment;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.andrehaueisen.fitx.Utils.getSharedPreferences;
import static com.bumptech.glide.Glide.with;

/**
 * Created by andre on 9/25/2016.
 */

public class PersonalBasicPresentationFragment extends Fragment implements ProfessionalProfileActivity.OnProfileDataReadyCallback {

    private final String TAG = PersonalBasicPresentationFragment.class.getSimpleName();
    private final int REQUEST_CODE = 0;

    @BindView(R.id.wide_background_image_view)
    ImageView mBackgroundImageView;
    @BindView(R.id.profile_image_view)
    CircleImageView mProfileImage;
    @BindView(R.id.personal_grade_text_view)
    TextView mGradeTextView;
    @BindView(R.id.review_counter_text_view)
    TextView mReviewCounterTextView;
    @BindView(R.id.price_text_input_layout)
    TextInputLayout mPriceTextInputLayout;

    private PersonalTrainer mPersonalTrainer;
    private String mProfilePhotoUriPath;
    private String mBackgroundUriPath;
    private int mLastClickedImageViewCode;

    private PictureSelectionMethodDialogFragment mPictureSelectionDialogFragment;

    public static Fragment newInstance() {
        return new PersonalBasicPresentationFragment();
    }

    public static Fragment newInstance(Bundle bundle) {
        PersonalBasicPresentationFragment personalBasicPresentationFragment = new PersonalBasicPresentationFragment();
        personalBasicPresentationFragment.setArguments(bundle);

        return personalBasicPresentationFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mPersonalTrainer = getArguments().getParcelable(Constants.PERSONAL_BUNDLE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.PERSONAL_BUNDLE_KEY, mPersonalTrainer);
        outState.putString(Constants.PERSONAL_BACKGROUND_PIC_BUNDLE_KEY, mBackgroundUriPath);
        outState.putString(Constants.PERSONAL_PROFILE_PIC_BUNDLE_KEY, mProfilePhotoUriPath);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_basic_presentation, container, false);
        ButterKnife.bind(this, view);

        Activity activity = getActivity();
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if(savedInstanceState != null){
            mPersonalTrainer = savedInstanceState.getParcelable(Constants.PERSONAL_BUNDLE_KEY);
            mBackgroundUriPath = savedInstanceState.getString(Constants.PERSONAL_BACKGROUND_PIC_BUNDLE_KEY);
            mProfilePhotoUriPath = savedInstanceState.getString(Constants.PERSONAL_PROFILE_PIC_BUNDLE_KEY);
        }

        setBackgroundImage();
        setProfileImage();

        configureEditTextBehaviour();

        if (mPersonalTrainer != null) {
            bindInfoToViews();
        } else {
            fetchPersonalOnDatabase();
        }

        return view;
    }

    private void setBackgroundImage(){
        if(mBackgroundUriPath == null) {
            mBackgroundUriPath = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_BACKGROUND_PHOTO_URI_PATH, null);
        }

        if(mBackgroundUriPath == null){
            with(getContext()).load(R.drawable.personal_background_placeholder).into(mBackgroundImageView);
        } else {
            Uri backgroundUri = Uri.parse(mBackgroundUriPath);
            with(getContext()).load(backgroundUri).into(mBackgroundImageView);
        }
    }

    private void setProfileImage() {
        if(mProfilePhotoUriPath == null) {
            mProfilePhotoUriPath = Utils.getSharedPreferences(getContext()).getString(Constants.SHARED_PREF_PERSONAL_PROFILE_PHOTO_URI_PATH, null);
        }

        if (mProfilePhotoUriPath == null) {
            with(getContext()).load(R.drawable.head_placeholder).into(mProfileImage);
        } else {
            Uri photoUri = Uri.parse(mProfilePhotoUriPath);
            with(getContext()).load(photoUri).into(mProfileImage);
        }
    }

    @OnClick(R.id.wide_background_image_view)
    public void onBackgroundClick(ImageView backgroundImageView) {
        startPictureSelectionDialog(backgroundImageView.getId());
    }

    @OnClick(R.id.profile_image_view)
    public void onProfileImageClick(ImageView profileImageView) {
        startPictureSelectionDialog(profileImageView.getId());
    }

    private void startPictureSelectionDialog(int imageId) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();

        switch (imageId) {
            case R.id.profile_image_view:
                mLastClickedImageViewCode = Constants.PERSONAL_PROFILE_PICTURE;
                bundle.putInt(Constants.IMAGE_CODE_BUNDLE_KEY, Constants.PERSONAL_PROFILE_PICTURE);
                break;
            case R.id.wide_background_image_view:
                mLastClickedImageViewCode = Constants.PERSONAL_BACKGROUND_PICTURE;
                bundle.putInt(Constants.IMAGE_CODE_BUNDLE_KEY, Constants.PERSONAL_BACKGROUND_PICTURE);
                break;
        }

        mPictureSelectionDialogFragment = PictureSelectionMethodDialogFragment.newInstance(bundle);
        mPictureSelectionDialogFragment.setTargetFragment(PersonalBasicPresentationFragment.this, REQUEST_CODE);
        mPictureSelectionDialogFragment.show(fragmentTransaction, "dialog");
    }

    private void configureEditTextBehaviour() {
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

    private void bindInfoToViews() {
        mGradeTextView.setText(Utils.formatGrade(mPersonalTrainer.getGrade()));
        mReviewCounterTextView.setText(String.valueOf(mPersonalTrainer.getReviewCounter()));
        if (mPersonalTrainer.get15MinutePrice() != 0) {
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
            if (price != null && !price.toString().isEmpty()) {
                mPersonalTrainer.set15MinutePrice(Integer.parseInt(price.toString()));
            }
        }
    };

    private void fetchPersonalOnDatabase() {
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
        editor.putString(Constants.SHARED_PREF_PERSONAL_PROFILE_PHOTO_URI_PATH, mProfilePhotoUriPath);
        editor.putString(Constants.SHARED_PREF_PERSONAL_BACKGROUND_PHOTO_URI_PATH, mBackgroundUriPath).apply();

        Bitmap profilePicture;
        if (mProfileImage.getDrawable().getCurrent() instanceof GlideBitmapDrawable) {
            profilePicture = ((GlideBitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        } else {
            profilePicture = ((BitmapDrawable) mProfileImage.getDrawable().getCurrent()).getBitmap();
        }
        PersonalDatabase.saveProfilePicsToFirebase(getActivity(), profilePicture, Constants.PERSONAL_PROFILE_PICTURE_NAME);

        Bitmap backgroundPicture;
        if (mBackgroundImageView.getDrawable().getCurrent() instanceof GlideBitmapDrawable) {
            backgroundPicture = ((GlideBitmapDrawable) mBackgroundImageView.getDrawable().getCurrent()).getBitmap();
        } else {
            backgroundPicture = ((BitmapDrawable) mBackgroundImageView.getDrawable().getCurrent()).getBitmap();
        }
        PersonalDatabase.saveProfilePicsToFirebase(getActivity(), backgroundPicture, Constants.PERSONAL_BACKGROUND_PICTURE_NAME);
        PersonalDatabase.savePersonalToDatabase(getActivity(), mPersonalTrainer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri photoUri;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case Constants.REQUEST_IMAGE_CAPTURE:
                    startCropActivity(mPictureSelectionDialogFragment.getProfilePicsUri());
                    break;

                case Constants.REQUEST_IMAGE_LOAD:
                    startCropActivity(data.getData());
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    photoUri = result.getUri();
                    placeImageOnImageView(photoUri);
            }

        } else {
            Log.e(TAG, "Error loading image");
        }
    }

    private void startCropActivity(Uri photoUri) {

        switch (mLastClickedImageViewCode) {
            case Constants.PERSONAL_PROFILE_PICTURE:
                CropImage.activity(photoUri)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
                break;

            case Constants.PERSONAL_BACKGROUND_PICTURE:
                CropImage.activity(photoUri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(16, 9)
                        .setOutputCompressQuality(100)
                        .start(getContext(), this);
                break;
        }
    }

    private void placeImageOnImageView(Uri photoUri) {

        switch (mLastClickedImageViewCode) {
            case Constants.PERSONAL_PROFILE_PICTURE:
                mProfilePhotoUriPath = photoUri.toString();
                with(this).loadFromMediaStore(photoUri).asBitmap().into(mProfileImage);
                break;

            case Constants.PERSONAL_BACKGROUND_PICTURE:
                mBackgroundUriPath = photoUri.toString();
                with(this).loadFromMediaStore(photoUri).asBitmap().into(mBackgroundImageView);

            default:
                Log.e(PersonalBasicPresentationFragment.class.getSimpleName(), "Image code not found");
        }


    }

}
