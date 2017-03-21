package com.andrehaueisen.fitx.register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.ClientActivity;
import com.andrehaueisen.fitx.personal.PersonalActivity;
import com.andrehaueisen.fitx.models.UndefinedUser;
import com.andrehaueisen.fitx.models.UserMappings;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/**
 * Created by andre on 11/17/2016.
 */

public class SignInActivity extends AppCompatActivity{

    private final int REQUEST_CODE = 0;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null){
            isUserFullyLogged(user);

        }else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .setLogo(R.drawable.ok_go_register_icon)
                            .setTheme(R.style.LogInTheme)
                            .build(), REQUEST_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            isUserFullyLogged(user);
        }

        if (resultCode == RESULT_CANCELED) {
            Utils.generateWarningToast(this, getString(R.string.login_canceled));
            return;
        }

        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
           Utils.generateWarningToast(this, getString(R.string.no_connection));
        }

    }

    private void isUserFullyLogged(final FirebaseUser firebaseUser){

        String uid = firebaseUser.getUid();

        mDatabase.child(Constants.FIREBASE_LOCATION_UID_MAPPINGS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    UserMappings userMappings = dataSnapshot.getValue(UserMappings.class);

                    if (!userMappings.isPersonal()) {
                        Intent intent = new Intent(SignInActivity.this, ClientActivity.class);
                        Utils.refreshClientDataOnLogin(SignInActivity.this, firebaseUser, intent);

                    } else {
                        Intent intent = new Intent(SignInActivity.this, PersonalActivity.class);
                        Utils.refreshPersonalDataOnLogin(SignInActivity.this, firebaseUser, intent);
                    }

                }else {
                    sendUserDataToClientOrPersonalActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendUserDataToClientOrPersonalActivity(){

        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        String name = user.getDisplayName();
        String email = user.getEmail();
        String uid = user.getUid();

        Uri photoUri = user.getPhotoUrl();
        String photoPath = null;
        if(photoUri != null){
            photoPath = photoUri.toString();
        }

        UndefinedUser undefinedUser = new UndefinedUser(name, email, uid, photoPath);

        Intent intent = new Intent(this, RegisterClientOrPersonalActivity.class);
        intent.putExtra(Constants.UNDEFINED_USER_EXTRA_KEY, undefinedUser);

        startActivity(intent);
        finish();

    }



    //TODO DELETE THIS - USED TO CONFIGURE FACEBOOK HASH KEY
    /*private PackageInfo info;
      private void logHashKey() {
        try {
            info = getPackageManager().getPackageInfo("com.andrehaueisen.fitx", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {

            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    } */

}
