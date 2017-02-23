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

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.CustomTextView;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.ClientActivity;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.personal.PersonalActivity;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.pojo.Client;
import com.andrehaueisen.fitx.pojo.PersonalTrainer;
import com.andrehaueisen.fitx.pojo.UndefinedUser;
import com.andrehaueisen.fitx.pojo.UserMappings;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;

public class RegisterClientOrPersonalActivity extends AppCompatActivity {

    private static final String TAG = RegisterClientOrPersonalActivity.class.getSimpleName();

    private TextInputLayout mNameTextField;
    private TextInputLayout mBirthdayTextField;
    private TextInputLayout mPhoneNumberTextField;
    private TextInputLayout mCREFNameTextField;
    private TextInputLayout mCREFTextField;
    private WheelView mStateChooserWheel;

    private String mDisplayName;
    private String mEmail;
    private String mUid;
    private String mPhotoPath;
    private String mBirthday;
    private String mPhoneNumber;

    private int mSelectedState = -1;

    private boolean mHasCREFNameChanged, mHasCREFChanged;
    private boolean mIsPersonal = false;

    private Client mClient = new Client();
    private PersonalTrainer mPersonalTrainer = new PersonalTrainer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client_or_personal);

        UndefinedUser undefinedUser = getIntent().getExtras().getParcelable(Constants.UNDEFINED_USER_EXTRA_KEY);

        mDisplayName = undefinedUser.getName();
        mEmail = undefinedUser.getEmail();
        mUid = undefinedUser.getUid();
        mPhotoPath = undefinedUser.getPhotoPath();

        mNameTextField = (TextInputLayout) findViewById(R.id.name_textInputLayout);
        EditText nameEditText = mNameTextField.getEditText();
        nameEditText.setText(mDisplayName);
        nameEditText.addTextChangedListener(mNameTextWatcher);

        mBirthdayTextField = (TextInputLayout) findViewById(R.id.birthday_textInputLayout);
        mBirthdayTextField.getEditText().addTextChangedListener(mBirthdayTextWatcher);

        mPhoneNumberTextField = (TextInputLayout) findViewById(R.id.phone_number_textInputLayout);
        mPhoneNumberTextField.getEditText().addTextChangedListener(mPhoneTextWatcher);

        mCREFNameTextField = (TextInputLayout) findViewById(R.id.cref_name_textInputLayout);
        mCREFNameTextField.getEditText().addTextChangedListener(mCREFNameTextWatcher);

        mCREFTextField = (TextInputLayout) findViewById(R.id.cref_textInputLayout);
        mCREFTextField.getEditText().addTextChangedListener(mCREFTextWatcher);

        CheckBox isPersonalCheckBox = (CheckBox) findViewById(R.id.is_personal_check_bok);

        final CustomTextView whereTextFromTextView = (CustomTextView) findViewById(R.id.where_cref_from_text_view);


        mStateChooserWheel = (WheelView) findViewById(R.id.state_chooser_wheel);
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
                    mCREFNameTextField.setVisibility(View.INVISIBLE);
                    mCREFTextField.setVisibility(View.INVISIBLE);
                    mStateChooserWheel.setVisibility(View.INVISIBLE);
                    whereTextFromTextView.setVisibility(View.INVISIBLE);
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

    private TextWatcher mBirthdayTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String birthday = editable.toString();
            if(!Utils.isBirthdayValid(birthday)){
                mBirthdayTextField.setError(getString(R.string.invalid_birthday));

            } else {
                int year = Integer.parseInt(birthday.substring(birthday.length() - 4, birthday.length()));

                if(Utils.isAgeGraterThan18(year)){
                    mBirthdayTextField.setError(getString(R.string.invalid_age));

                } else {
                    mBirthdayTextField.setErrorEnabled(false);
                    mBirthday = birthday;
                }
            }
        }
    };

    private TextWatcher mPhoneTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String phone = editable.toString();
            if(!Utils.isPhoneValid(phone)){
                mPhoneNumberTextField.setError(getString(R.string.invalid_phone_number));
            } else {
                mPhoneNumberTextField.setErrorEnabled(false);
                mPhoneNumber = phone;
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
                    Utils.getSharedPreferences(RegisterClientOrPersonalActivity.this).edit().putString(Constants.SHARED_PREF_PERSONAL_PHOTO_URI_PATH, mPhotoPath).commit();
                }

                Intent intent = new Intent(this, PersonalActivity.class);
                startActivity(intent);
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
            }

        }
    }

    private boolean isPersonalDataOk(){

        boolean isAllDataSet = true;
        mPersonalTrainer.setName(mDisplayName);
        mPersonalTrainer.setEmail(mEmail);
        mPersonalTrainer.setPhoneNumber(mPhoneNumber);
        mPersonalTrainer.setBirthday(mBirthday);
        mPersonalTrainer.setCrefState(getResources().getStringArray(R.array.states_array)[mSelectedState]);

        if (mPersonalTrainer.getName() == null || mNameTextField.isErrorEnabled()
                || mNameTextField.getEditText().getText().toString().equals("")) {
            Utils.generateInfoToast(this, getString(R.string.invalid_name)).show();
            isAllDataSet = false;
        }

        if (mPersonalTrainer.getBirthday() == null || mBirthdayTextField.isErrorEnabled()
                || mBirthdayTextField.getEditText().getText().toString().equals("")) {
            Utils.generateInfoToast(this, getString(R.string.invalid_birthday)).show();
            isAllDataSet = false;
        }

        if (mPersonalTrainer.getPhoneNumber() == null || mPhoneNumberTextField.isErrorEnabled()
                || mPhoneNumberTextField.getEditText().getText().toString().equals("")) {
            Utils.generateInfoToast(this, getString(R.string.invalid_phone_number)).show();
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
        mClient.setPhoneNumber(mPhoneNumber);
        mClient.setBirthday(mBirthday);

        if(mClient.getName() == null || mNameTextField.isErrorEnabled()
                || mNameTextField.getEditText().getText().toString().equals("")){
            Utils.generateInfoToast(this, getString(R.string.invalid_name)).show();
            isAllDataSet = false;
        }

        if(mClient.getBirthday() == null || mBirthdayTextField.isErrorEnabled()
                || mBirthdayTextField.getEditText().getText().toString().equals("")){
            Utils.generateInfoToast(this, getString(R.string.invalid_birthday)).show();
            isAllDataSet = false;
        }

        if(mClient.getPhoneNumber() == null || mPhoneNumberTextField.isErrorEnabled()
                || mPhoneNumberTextField.getEditText().getText().toString().equals("")){
            Utils.generateInfoToast(this, getString(R.string.invalid_phone_number)).show();
            isAllDataSet = false;
        }

        return isAllDataSet;
    }

    private void saveUserMapping(boolean isPersonal){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        UserMappings userMappings = new UserMappings(Utils.encodeEmail(mEmail), isPersonal);
        databaseReference.child(Constants.FIREBASE_LOCATION_UID_MAPPINGS).child(mUid).setValue(userMappings);

    }

    class ProfileImageTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap profileImage;
            if(mIsPersonal){
                try {
                    profileImage = Glide.with(RegisterClientOrPersonalActivity.this).load(mPhotoPath).asBitmap().into(100, 100).get();
                    PersonalDatabase.saveProfilePicsToFirebase(RegisterClientOrPersonalActivity.this, profileImage);

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

    class ValidateCREF extends AsyncTask<String, Void, Boolean> {

        private String mPersonalName;
        int mState;
        private String mCref;
        ProgressDialog mProgressDialog;

        public ValidateCREF(String personalName, int state, String cref) {
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
