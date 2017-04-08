package com.andrehaueisen.fitx.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.andrehaueisen.fitx.utilities.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.utilities.Utils;
import com.andrehaueisen.fitx.client.ClientActivity;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.models.Client;
import com.andrehaueisen.fitx.models.PersonalTrainer;
import com.andrehaueisen.fitx.models.UndefinedUser;
import com.andrehaueisen.fitx.models.UserMappings;
import com.andrehaueisen.fitx.personal.PersonalActivity;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Arrays.asList;

public class RegisterClientOrPersonalActivity extends AppCompatActivity {

    private static final String TAG = RegisterClientOrPersonalActivity.class.getSimpleName();

    @BindView(R.id.name_textInputLayout) TextInputLayout mNameTextField;
    @BindView(R.id.cref_name_textInputLayout) TextInputLayout mCREFNameTextField;
    @BindView(R.id.cref_textInputLayout) TextInputLayout mCREFTextField;
    @BindView(R.id.state_chooser_wheel) WheelView mStateChooserWheel;

    private String mDisplayName;
    private String mEmail;
    private String mUid;
    private String mPhotoPath;
    private String mCref;

    private int mSelectedState = -1;

    private boolean mHasCREFNameChanged, mHasCREFChanged;
    private boolean mIsPersonal = false;

    private Client mClient = new Client();
    private PersonalTrainer mPersonalTrainer = new PersonalTrainer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client_or_personal);

        ButterKnife.bind(this);

        UndefinedUser undefinedUser = getIntent().getExtras().getParcelable(Constants.UNDEFINED_USER_EXTRA_KEY);

        mDisplayName = undefinedUser.getName();
        mEmail = undefinedUser.getEmail();
        mUid = undefinedUser.getUid();
        mPhotoPath = undefinedUser.getPhotoPath();

        EditText nameEditText = mNameTextField.getEditText();
        nameEditText.setText(mDisplayName);
        nameEditText.addTextChangedListener(mNameTextWatcher);

        mCREFNameTextField.getEditText().addTextChangedListener(mCREFNameTextWatcher);

        mCREFTextField.getEditText().addTextChangedListener(mCREFTextWatcher);

        CheckBox isPersonalCheckBox = (CheckBox) findViewById(R.id.is_personal_check_bok);

        final TextView whereTextFromTextView = (TextView) findViewById(R.id.where_cref_from_text_view);

        configureWheel();

        isPersonalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    mCREFNameTextField.setVisibility(View.VISIBLE);
                    mCREFTextField.setVisibility(View.VISIBLE);
                    mStateChooserWheel.setVisibility(View.VISIBLE);
                    whereTextFromTextView.setVisibility(View.VISIBLE);
                    mIsPersonal = true;
                } else {
                    mCREFNameTextField.setVisibility(View.GONE);
                    mCREFTextField.setVisibility(View.GONE);
                    mStateChooserWheel.setVisibility(View.INVISIBLE);
                    whereTextFromTextView.setVisibility(View.GONE);
                    mIsPersonal = false;
                }
            }
        });

    }

    private void configureWheel(){
        final List<String> state_names = asList(getResources().getStringArray(R.array.states_array));
        mStateChooserWheel.setWheelAdapter(new BaseWheelAdapter(){
            @Override
            public View bindView(int position, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.item_simple_name, parent, false);

                TextView stateTextView = (TextView) view.findViewById(R.id.simple_text_view);
                stateTextView.setText(state_names.get(position));
                return view;
            }
        });

        mStateChooserWheel.setWheelData(state_names);
        mStateChooserWheel.setSkin(WheelView.Skin.Holo);
        mStateChooserWheel.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                mSelectedState = position;
            }
        });

        WheelView.WheelViewStyle wheelViewStyle = new WheelView.WheelViewStyle();
        wheelViewStyle.backgroundColor = getResources().getColor(R.color.transparent);
        wheelViewStyle.textColor = getResources().getColor(R.color.black);
        wheelViewStyle.holoBorderColor = getResources().getColor(R.color.colorAccent);
        mStateChooserWheel.setStyle( wheelViewStyle );

    }

    private TextWatcher mNameTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String name = editable.toString();
            if(mNameTextField.getEditText().getText().toString().equals("")){
                mNameTextField.setError(getString(R.string.invalid_name));
            } else {
                mNameTextField.setErrorEnabled(false);
                mDisplayName = name;
            }
        }
    };

    private TextWatcher mCREFNameTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mHasCREFNameChanged = true;
            mCREFTextField.setEnabled(true);
            if (mCREFNameTextField.getEditText().getText().toString().equals("")) {
                mCREFNameTextField.setError(getString(R.string.invalid_name));
            } else {
                mCREFNameTextField.setErrorEnabled(false);
            }
        }
    };

    private TextWatcher mCREFTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mHasCREFChanged = true;
            String cref = editable.toString();
            String personalCrefName = mCREFNameTextField.getEditText().getText().toString();

            if (!personalCrefName.isEmpty() && mSelectedState != -1 && !cref.isEmpty()) {
                mCREFTextField.setErrorEnabled(false);
                mCref = cref;
            } else {
                mCREFTextField.setError(getString(R.string.cref_is_wrong));
            }
        }
    };

    public void saveData(View view) {

        if(mIsPersonal){

            if (isPersonalDataOk()) {
                PersonalDatabase.savePersonalToDatabase(this, mPersonalTrainer);
                saveUserMapping(true);

                if(mPhotoPath != null){
                    new ProfileImageTask().execute();
                    Utils.getSharedPreferences(RegisterClientOrPersonalActivity.this).edit().putString(Constants.SHARED_PREF_PERSONAL_PROFILE_PHOTO_URI_PATH, mPhotoPath).commit();
                }

                Intent intent = new Intent(this, PersonalActivity.class);
                startActivity(intent);
                finish();
            }

        } else {

            if(isClientDataOk()){

                if(mPhotoPath != null) {
                    new ProfileImageTask().execute();
                    Utils.getSharedPreferences(RegisterClientOrPersonalActivity.this).edit().putString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, mPhotoPath).commit();
                }

                ClientDatabase.saveClientToDatabase(this, mClient);
                saveUserMapping(false);

                Intent intent = new Intent(this, ClientActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    private boolean isPersonalDataOk(){

        boolean isAllDataSet = true;
        mPersonalTrainer.setName(mDisplayName);
        mPersonalTrainer.setEmail(mEmail);
        mPersonalTrainer.setCrefState(getResources().getStringArray(R.array.states_array)[mSelectedState]);
        mPersonalTrainer.setCref(mCref);

        if (mPersonalTrainer.getName() == null || mNameTextField.isErrorEnabled()
                || mNameTextField.getEditText().getText().toString().equals("")) {
            Utils.generateInfoToast(this, getString(R.string.invalid_name)).show();
            isAllDataSet = false;
        }

        String mCrefName = mCREFNameTextField.getEditText().getText().toString();
        if (mCREFNameTextField.isErrorEnabled() || mCrefName.equals("")) {
            Utils.generateInfoToast(this, getString(R.string.invalid_name)).show();
            isAllDataSet = false;
        }

        String cref = mCREFTextField.getEditText().getText().toString();
        if (mCREFTextField.isErrorEnabled()) {
            Utils.generateInfoToast(this, getString(R.string.invalid_cref)).show();
            isAllDataSet = false;
        }

        if (isAllDataSet && !cref.isEmpty() && (mHasCREFChanged || mHasCREFNameChanged)) {
            new RegisterClientOrPersonalActivity.ValidateCREF(mCrefName, mSelectedState, cref).execute();
        }

        return isAllDataSet;
    }

    private boolean isClientDataOk(){

        boolean isAllDataSet = true;

        mClient.setName(mDisplayName);
        mClient.setEmail(mEmail);

        if(mClient.getName() == null || mNameTextField.isErrorEnabled()
                || mNameTextField.getEditText().getText().toString().equals("")){
            Utils.generateInfoToast(this, getString(R.string.invalid_name)).show();
            isAllDataSet = false;
        }

        return isAllDataSet;
    }

    private void saveUserMapping(boolean isPersonal){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        UserMappings userMappings = new UserMappings(Utils.encodeEmail(mEmail), isPersonal);
        databaseReference.child(Constants.FIREBASE_LOCATION_UID_MAPPINGS).child(mUid).setValue(userMappings);

    }

    private class ProfileImageTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap profileImage;
            if(mIsPersonal){
                try {
                    profileImage = Glide.with(RegisterClientOrPersonalActivity.this).load(mPhotoPath).asBitmap().into(100, 100).get();
                    PersonalDatabase.saveProfilePicsToFirebase(RegisterClientOrPersonalActivity.this, profileImage, Constants.PERSONAL_PROFILE_PICTURE_NAME);

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }

            }else {
                try {
                    profileImage = Glide.with(RegisterClientOrPersonalActivity.this).load(mPhotoPath).asBitmap().into(100, 100).get();
                    ClientDatabase.saveProfilePicsToFirebase(RegisterClientOrPersonalActivity.this, profileImage);

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            return null;
        }


    }

    private class ValidateCREF extends AsyncTask<String, Void, Boolean> {

        private String mPersonalName;
        int mState;
        private String mCref;
        ProgressDialog mProgressDialog;

        ValidateCREF(String personalName, int state, String cref) {
            mPersonalName = personalName;
            mState = state;
            mCref = cref;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(RegisterClientOrPersonalActivity.this);
            mProgressDialog.setTitle(getString(R.string.validating_cref));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            return new CrefValidator().isPersonalValid(mPersonalName, mState, mCref);
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            super.onPostExecute(isValid);

            mProgressDialog.dismiss();

            if (isValid) {
                Utils.generateSuccessToast(RegisterClientOrPersonalActivity.this, getString(R.string.name_cref_match)).show();

                mCREFTextField.setErrorEnabled(false);
                mCREFTextField.setEnabled(false);
                mPersonalTrainer.setCref(mCREFTextField.getEditText().getText().toString());

                mHasCREFNameChanged = false;
                mHasCREFChanged = false;

            } else {
                Utils.generateErrorToast(RegisterClientOrPersonalActivity.this, getString(R.string.name_cref_not_match)).show();
                mCREFTextField.setError(getString(R.string.invalid_cref));
            }
        }

    }

}
