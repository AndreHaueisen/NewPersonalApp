package com.andrehaueisen.fitx.personal.drawer.dialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by andre on 9/25/2016.
 */

public class PictureSelectionMethodDialogFragment extends DialogFragment  {

    private int mImageCode;
    private Uri mPhotoUri;
    private File mImageFile;

    public static PictureSelectionMethodDialogFragment newInstance(Bundle bundle){

        PictureSelectionMethodDialogFragment pictureSelectionMethodDialogFragment = new PictureSelectionMethodDialogFragment();
        pictureSelectionMethodDialogFragment.setArguments(bundle);

        return pictureSelectionMethodDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageCode = getArguments().getInt(Constants.IMAGE_CODE_BUNDLE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment_picture_selection_method, container, false);

        ImageView galleryImageView = (ImageView) view.findViewById(R.id.gallery_choice_image_view);
        galleryImageView.setOnClickListener(onPictureInputClick);

        ImageView cameraImageView = (ImageView) view.findViewById(R.id.camera_choice_image_view);
        cameraImageView.setOnClickListener(onPictureInputClick);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    private View.OnClickListener onPictureInputClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int buttonId = v.getId();

            Fragment fragment = getTargetFragment();

            if (buttonId == R.id.gallery_choice_image_view){

                Intent photoLibraryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoLibraryIntent.setType("image/*");

                if(fragment != null) {
                    fragment.startActivityForResult(photoLibraryIntent, Constants.REQUEST_IMAGE_LOAD);
                }else {
                    getActivity().startActivityForResult(photoLibraryIntent, Constants.REQUEST_IMAGE_LOAD);
                }

                dismiss();

            }else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

                    try {
                        createImageFile();
                    } catch (IOException ioe) {
                        Log.e(PictureSelectionMethodDialogFragment.class.getSimpleName(), "Error creating file: " + ioe);
                    }

                    if (mImageFile != null) {
                        mPhotoUri = FileProvider.getUriForFile(getContext(), "com.andrehaueisen.fitx.fileprovider", mImageFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

                        if(fragment != null) {
                            fragment.startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                        }else {
                            getActivity().startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                        }

                        dismiss();
                    }
                }
            }
        }
    };

    private void createImageFile() throws IOException{

        File storageDirectory = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        switch (mImageCode) {
            case Constants.PERSONAL_PROFILE_PICTURE:
                mImageFile = new File(storageDirectory, Constants.PERSONAL_PROFILE_PICTURE_NAME);
                break;

            case Constants.PERSONAL_BACKGROUND_PICTURE:
                mImageFile = new File(storageDirectory, Constants.PERSONAL_BACKGROUND_PICTURE_NAME);
                break;

            case Constants.CLIENT_PROFILE_PICTURE:
                mImageFile = new File(storageDirectory, Constants.CLIENT_PROFILE_PICTURE_NAME);
                break;
        }

        if(!mImageFile.exists()){
            if(!mImageFile.createNewFile()){
                Log.e(PictureSelectionMethodDialogFragment.class.getSimpleName(), "Could not create file");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE){

            Fragment fragment = getTargetFragment();
            fragment.onActivityResult(requestCode, resultCode, data);

            dismiss();
        }

    }

    public Uri getProfilePicsUri() {
        return mPhotoUri;
    }

    public File getImageFile() {
        return mImageFile;
    }
}
