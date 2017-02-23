package com.andrehaueisen.fitx.shared.dialogFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.andrehaueisen.fitx.R;

/**
 * Created by andre on 9/29/2016.
 */

public class ClassDurationDialogFragment extends DialogFragment {

    private int mClassDurationCode = 4;
    private DurationCallBack mDurationCallBack;

    public static ClassDurationDialogFragment newInstance(){
        return new ClassDurationDialogFragment();
    }

    public interface DurationCallBack{
        void setDuration(int duration);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getTargetFragment() != null) {
            mDurationCallBack = (DurationCallBack) getTargetFragment();
        }else{
            mDurationCallBack = (DurationCallBack) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment_configure_class_duration, container, false);

        RadioGroup classDurationRadioButton = (RadioGroup) view.findViewById(R.id.class_duration_radio_group);
        classDurationRadioButton.setOnCheckedChangeListener(classDurationCheckedChangeListener);

        Button confirmClassConfigurationButton = (Button) view.findViewById(R.id.confirm_class_configuration_button);
        confirmClassConfigurationButton.setOnClickListener(confirmClassConfigurationClickListener);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    private RadioGroup.OnCheckedChangeListener classDurationCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            RadioButton classDurationRadioButton = (RadioButton) group.findViewById(checkedId);
            boolean isChecked = classDurationRadioButton.isChecked();

            switch (checkedId){
                case R.id.duration_45_radio_button:
                    if(isChecked){
                        mClassDurationCode = 3;
                    }
                    break;
                case R.id.duration_60_radio_button:
                    if(isChecked){
                        mClassDurationCode = 4;
                    }
                    break;
                case R.id.duration_75_radio_button:
                    if(isChecked){
                        mClassDurationCode = 5;
                    }
                    break;
                case R.id.duration_90_radio_button:
                    if(isChecked){
                        mClassDurationCode = 6;
                    }
                    break;
                case R.id.duration_105_radio_button:
                    if(isChecked){
                        mClassDurationCode = 7;
                    }
                    break;
                case R.id.duration_120_radio_button:
                    if(isChecked){
                        mClassDurationCode = 8;
                    }
                    break;
            }

        }
    };

    private View.OnClickListener confirmClassConfigurationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDurationCallBack.setDuration(mClassDurationCode);
            dismiss();
        }
    };

}
