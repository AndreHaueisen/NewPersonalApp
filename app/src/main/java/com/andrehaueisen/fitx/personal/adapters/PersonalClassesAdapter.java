package com.andrehaueisen.fitx.personal.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.utilities.CustomButton;
import com.andrehaueisen.fitx.utilities.Utils;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 10/17/2016.
 */

public class PersonalClassesAdapter extends RecyclerView.Adapter<PersonalClassesAdapter.ClassHolder> {

    private Context mContext;
    private ArrayList<PersonalFitClass> mPersonalFitClasses;
    private Activity mActivity;
    private DisplayMetrics mDisplayMetrics;
    private DecelerateInterpolator mInterpolator;
    private Resources mResources;
    private float mSmallestScreenWidth;

    public PersonalClassesAdapter(Fragment fragment, ArrayList<PersonalFitClass> personalFitClasses) {

        mContext = fragment.getContext();
        mActivity = fragment.getActivity();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        mPersonalFitClasses = new ArrayList<>();
        mPersonalFitClasses.addAll(personalFitClasses);
        mInterpolator = new DecelerateInterpolator();
        mResources = mContext.getResources();
        mSmallestScreenWidth = Utils.getSmallestScreenWidth(mContext);
    }

    @Override
    public ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_personal_class, parent, false);

        return new ClassHolder(view);
    }

    public void removePersonalFitClass(int position){
        mPersonalFitClasses.remove(position);
    }

    public void addPersonalFitClass(PersonalFitClass personalFitClass){
        mPersonalFitClasses.add(personalFitClass);
    }

    public void changePersonalFitClass(int position, PersonalFitClass personalFitClass){
        mPersonalFitClasses.set(position, personalFitClass);
    }

    @Override
    public void onBindViewHolder(ClassHolder holder, int position) {
        holder.onBindClass(position);
    }

    @Override
    public int getItemCount() {
        return mPersonalFitClasses.size();
    }

    public class ClassHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private CountDownTimer mDismissClassCountDownTimer;
        private CountDownTimer mConfirmClassCountDownTimer;
        private AlertDialog mPenaltyAlertDialog;
        private AlertDialog mConfirmationAlertDialog;

        private CustomButton mStatusClassButton;
        private TextView mTimeTextView;
        private TextView mDateTextView;
        private TextView mClientNameTextView;
        private TextView mAddressTextView;
        private TextView mLocationNameTextView;
        private ImageButton mDirectionImageButton;
        private ImageButton mDismissClassImageButton;
        private ProgressBar mDismissProgressBar;
        private ProgressBar mConfirmProgressBar;
        private CircleImageView mClientImageView;

        public ClassHolder(View itemView) {
            super(itemView);

            setupCountDownListeners();
            setupAlertDialogs();

            mCardView = (CardView) itemView.findViewById(R.id.class_container_card_view);
            mStatusClassButton = (CustomButton) itemView.findViewById(R.id.class_status_button);
            mTimeTextView = (TextView) itemView.findViewById(R.id.class_time_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.class_date_text_view);
            mClientNameTextView = (TextView) itemView.findViewById(R.id.client_name_text_view);
            mAddressTextView = (TextView) itemView.findViewById(R.id.class_location_address_text_view);
            mLocationNameTextView = (TextView) itemView.findViewById(R.id.class_location_name_text_view);
            mDirectionImageButton = (ImageButton) itemView.findViewById(R.id.direction_image_button);

            mDismissClassImageButton = (ImageButton) itemView.findViewById(R.id.dismiss_class_image_button);
            mDismissProgressBar = (ProgressBar) itemView.findViewById(R.id.spin_kit_dismiss);
            DoubleBounce doubleBounceDismiss = new DoubleBounce();
            mDismissProgressBar.setIndeterminateDrawable(doubleBounceDismiss);

            DoubleBounce doubleBounceConfirm = new DoubleBounce();
            mConfirmProgressBar = (ProgressBar) itemView.findViewById(R.id.spin_kit_confirm);
            mConfirmProgressBar.setIndeterminateDrawable(doubleBounceConfirm);

            mClientImageView = (CircleImageView) itemView.findViewById(R.id.person_image_view);
        }

        private void onBindClass(int position) {

            setCardViewMargins();

            PersonalFitClass personalFitClass = mPersonalFitClasses.get(position);

            String classStartTime = Utils.getClockFromTimeCode(mContext, personalFitClass.getStartTimeCode());
            String classEndTime = Utils.getClockFromTimeCode(mContext, personalFitClass.getStartTimeCode() + personalFitClass.getDurationCode());

            mDirectionImageButton.setOnClickListener(directionButtonClickListener);
            mDismissClassImageButton.setOnTouchListener(dismissClassOnTouchListener);

            ObjectAnimator animator;

            if (personalFitClass.isConfirmed()) {
                mStatusClassButton.setText(mContext.getString(R.string.confirmed));
                mStatusClassButton.setEnabled(false);
                mStatusClassButton.setElevation(0f);

                animator = ObjectAnimator.ofArgb(mCardView, "CardBackgroundColor",
                        mResources.getColor(R.color.card_green), mResources.getColor(R.color.greenish), mResources.getColor(R.color.card_green))
                        .setDuration(1500);
                animator.setInterpolator(mInterpolator);
                animator.setStartDelay(2000);
                animator.start();
            } else {
                mStatusClassButton.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rectangle_outline));
                mStatusClassButton.setText(mContext.getString(R.string.hold_to_confirm));
                mStatusClassButton.setOnTouchListener(confirmClassOnTouchListener);

                animator = ObjectAnimator.ofArgb(mCardView, "CardBackgroundColor",
                        mResources.getColor(R.color.card_red), mResources.getColor(R.color.reddish), mResources.getColor(R.color.card_red))
                        .setDuration(1500);
                animator.setInterpolator(mInterpolator);
                animator.setStartDelay(2000);
                animator.start();

            }

            String classStartEnd = mContext.getString(R.string.class_start_end, classStartTime, classEndTime);
            mTimeTextView.setText(classStartEnd);

            String date = Utils.getWrittenDateFromDateCode(mContext, personalFitClass.getDateCode());
            mDateTextView.setText(mContext.getString(R.string.class_date, date));

            mAddressTextView.setText(personalFitClass.getPlaceAddress());
            mLocationNameTextView.setText(personalFitClass.getPlaceName());

            setPersonName(personalFitClass);

            setProfileImage(personalFitClass.getClassProfileImage());

        }

        private void setCardViewMargins(){
            if(mSmallestScreenWidth < 600) {
                if (getAdapterPosition() == 0) {
                    Utils.setMargins(mCardView,
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(64, mDisplayMetrics),
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics));
                } else {
                    Utils.setMargins(mCardView, Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics),
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics));
                }
            }else{
                if (getAdapterPosition() == 0 || getAdapterPosition() == 1) {
                    Utils.setMargins(mCardView,
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(86, mDisplayMetrics),
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics));
                } else {
                    Utils.setMargins(mCardView,
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics),
                            Utils.convertDpIntoPx(16, mDisplayMetrics),
                            Utils.convertDpIntoPx(8, mDisplayMetrics));
                }
            }
        }


        private void setupCountDownListeners() {

            mDismissClassCountDownTimer = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsUntilFinished = (int) millisUntilFinished / 1000;
                    mPenaltyAlertDialog.setMessage(mContext.getString(R.string.penalty_warning_message, secondsUntilFinished));
                }

                @Override
                public void onFinish() {
                    mDismissProgressBar.setVisibility(View.GONE);
                    mPenaltyAlertDialog.dismiss();
                    PersonalDatabase.deleteClassFromPersonalSide(mContext, mPersonalFitClasses.get(getAdapterPosition()));
                }
            };

            mConfirmClassCountDownTimer = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsUntilFinished = (int) millisUntilFinished / 1000;
                    mConfirmationAlertDialog.setMessage(mContext.getString(R.string.confirmation_message, secondsUntilFinished));
                }

                @Override
                public void onFinish() {
                    PersonalFitClass fitClass = mPersonalFitClasses.get(getAdapterPosition());

                    mConfirmProgressBar.setVisibility(View.GONE);
                    mConfirmationAlertDialog.dismiss();
                    PersonalDatabase.confirmClass(mActivity, fitClass.getClassKey(), fitClass.getClientKey());
                }
            };

        }

        private void setupAlertDialogs() {
            mPenaltyAlertDialog = new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_penalty_warning_48dp)
                    .setMessage(mContext.getString(R.string.penalty_warning_message, 10))
                    .setTitle(R.string.penalty_warning_title)
                    .create();

            mConfirmationAlertDialog = new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_thumb_up_48dp)
                    .setMessage(mContext.getString(R.string.confirmation_message, 10))
                    .setTitle(R.string.confirmation_title)
                    .create();
        }

        private void setPersonName(PersonalFitClass personalFitClass) {
            mClientNameTextView.setText(personalFitClass.getClientName());
        }

        private View.OnClickListener directionButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PersonalFitClass fitClass = mPersonalFitClasses.get(getAdapterPosition());
                Uri mapsIntentUri = Uri.parse("geo:0,0?q=" + fitClass.getPlaceLatitude() + "," + fitClass.getPlaceLongitude() + "(" + fitClass.getPlaceName() + ")");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
                mContext.startActivity(mapIntent);
            }
        };

        private View.OnTouchListener dismissClassOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    mDismissProgressBar.setVisibility(View.VISIBLE);
                    mPenaltyAlertDialog.show();
                    mDismissClassCountDownTimer.start();
                    return true;
                }
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    mDismissProgressBar.setVisibility(View.GONE);
                    mPenaltyAlertDialog.dismiss();
                    mDismissClassCountDownTimer.cancel();
                    return true;
                }

                return false;
            }
        };

        private View.OnTouchListener confirmClassOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mConfirmProgressBar.setVisibility(View.VISIBLE);
                    mConfirmationAlertDialog.show();
                    mConfirmClassCountDownTimer.start();
                    return true;
                }
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    mConfirmProgressBar.setVisibility(View.GONE);
                    mConfirmationAlertDialog.dismiss();
                    mConfirmClassCountDownTimer.cancel();
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
                        .into(mClientImageView);
            } else {
                Glide.with(mContext).load("").placeholder(mContext.getResources().getDrawable(R.drawable.head_placeholder)).into(mClientImageView);
            }

        }

    }
}
