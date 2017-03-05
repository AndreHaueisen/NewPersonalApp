package com.andrehaueisen.fitx.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.CustomButton;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.models.ClientFitClass;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 11/21/2016.
 */

public class ClientClassesAdapter extends RecyclerView.Adapter<ClientClassesAdapter.ClassHolder> {

    private Context mContext;
    private ArrayList<ClientFitClass> mClientFitClasses;

    public interface ClassCallback {
        String getPersonalKey();
    }

    public ClientClassesAdapter(Fragment fragment, ArrayList<ClientFitClass> clientFitClasses) {
        mContext = fragment.getContext();
        mClientFitClasses = clientFitClasses;
    }

    @Override
    public ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_client_class, parent, false);

        return new ClassHolder(view);
    }


    @Override
    public void onBindViewHolder(ClientClassesAdapter.ClassHolder holder, int position) {
        holder.onBindClass(position);
    }

    @Override
    public int getItemCount() {
        return mClientFitClasses.size();
    }

    public class ClassHolder extends RecyclerView.ViewHolder {

        private CountDownTimer mDismissClassCountDownTimer;
        private AlertDialog mCancellationAlertDialog;

        private CustomButton mStatusClassButton;
        private TextView mPersonalNameTextView;
        private TextView mTimeTextView;
        private TextView mDateTextView;
        private TextView mAddressTextView;
        private TextView mLocationNameTextView;
        private ImageButton mDirectionImageButton;
        private ImageButton mDismissClassImageButton;
        private ProgressBar mDismissProgressBar;
        private CircleImageView mPersonalImageView;

        public ClassHolder(View itemView) {
            super(itemView);

            setupCountDownListeners();
            setupAlertDialogs();

            mStatusClassButton = (CustomButton) itemView.findViewById(R.id.class_status_button);
            mTimeTextView = (TextView) itemView.findViewById(R.id.class_time_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.class_date_text_view);
            mPersonalNameTextView = (TextView) itemView.findViewById(R.id.personal_name_text_view);
            mAddressTextView = (TextView) itemView.findViewById(R.id.class_location_address_text_view);
            mLocationNameTextView = (TextView) itemView.findViewById(R.id.class_location_name_text_view);
            mDirectionImageButton = (ImageButton) itemView.findViewById(R.id.direction_image_button);

            mDismissClassImageButton = (ImageButton) itemView.findViewById(R.id.dismiss_class_image_button);
            mDismissProgressBar = (ProgressBar) itemView.findViewById(R.id.spin_kit_dismiss);
            DoubleBounce doubleBounceDismiss = new DoubleBounce();
            mDismissProgressBar.setIndeterminateDrawable(doubleBounceDismiss);

            mPersonalImageView = (CircleImageView) itemView.findViewById(R.id.person_image_view);
        }

        private void onBindClass(int position) {

            ClientFitClass clientFitClass = mClientFitClasses.get(position);

            String classStartTime = Utils.getClockFromTimeCode(mContext, clientFitClass.getStartTimeCode());
            String classEndTime = Utils.getClockFromTimeCode(mContext, clientFitClass.getStartTimeCode() + clientFitClass.getDurationCode());

            mDirectionImageButton.setOnClickListener(directionButtonClickListener);
            mDismissClassImageButton.setOnTouchListener(dismissClassOnTouchListener);

            mStatusClassButton.setEnabled(false);
            mStatusClassButton.setElevation(0f);
            if (clientFitClass.isConfirmed()) {
                mStatusClassButton.setText(mContext.getString(R.string.confirmed));

            } else {
                mStatusClassButton.setText(mContext.getString(R.string.not_confirmed));
            }

            String classStartEnd = mContext.getString(R.string.class_start_end, classStartTime, classEndTime);
            mTimeTextView.setText(classStartEnd);

            String date = Utils.getWrittenDateFromDateCode(mContext, clientFitClass.getDateCode());
            mDateTextView.setText(mContext.getString(R.string.class_date, date));

            mAddressTextView.setText(clientFitClass.getPlaceAddress());
            mLocationNameTextView.setText(clientFitClass.getPlaceName());

            setPersonName(clientFitClass);
            setProfileImage(clientFitClass.getClassProfileImage());
        }

        private void setupCountDownListeners(){

            mDismissClassCountDownTimer = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsUntilFinished = (int) millisUntilFinished / 1000;
                    mCancellationAlertDialog.setMessage(mContext.getString(R.string.cancellation_warning_message, secondsUntilFinished));
                }

                @Override
                public void onFinish() {
                    mDismissProgressBar.setVisibility(View.GONE);
                    mCancellationAlertDialog.dismiss();
                    String clientKey = Utils.getSharedPreferences(mContext).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);
                    ClientDatabase.deleteClassFromClientSide(mContext, mClientFitClasses.get(getAdapterPosition()), clientKey);
                }
            };

        }
        private void setupAlertDialogs(){
            mCancellationAlertDialog = new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_penalty_warning_48dp)
                    .setMessage(mContext.getString(R.string.cancellation_warning_message, 10))
                    .setTitle(R.string.penalty_warning_title)
                    .create();
        }

        private void setPersonName(ClientFitClass clientFitClass){
            mPersonalNameTextView.setText(clientFitClass.getPersonalName());
        }

        private View.OnClickListener directionButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClientFitClass fitClass = mClientFitClasses.get(getAdapterPosition());
                Uri mapsIntentUri = Uri.parse("geo:0,0?q=" + fitClass.getPlaceLatitude() + "," + fitClass.getPlaceLongitude() + "(" + fitClass.getPlaceName() + ")");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
                mContext.startActivity(mapIntent);
            }
        };

        private View.OnTouchListener dismissClassOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN){
                    mDismissProgressBar.setVisibility(View.VISIBLE);
                    mCancellationAlertDialog.show();
                    mDismissClassCountDownTimer.start();
                    return true;
                }
                if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
                    mDismissProgressBar.setVisibility(View.GONE);
                    mCancellationAlertDialog.dismiss();
                    mDismissClassCountDownTimer.cancel();
                    return true;
                }

                return false;
            }
        };

        private void setProfileImage(Bitmap personProfileImage) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(personProfileImage != null) {

                personProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                Glide.with(mContext).load(stream.toByteArray()).asBitmap().placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder))
                        .into(mPersonalImageView);
            } else {
                Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(mPersonalImageView);
            }

        }
    }
}