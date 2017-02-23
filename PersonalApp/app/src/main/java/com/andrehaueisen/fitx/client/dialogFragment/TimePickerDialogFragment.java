package com.andrehaueisen.fitx.client.dialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by andre on 10/4/2016.
 */

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimeCallBack mTimeCallBack;
    private Calendar mCalendar = Calendar.getInstance();

    public interface TimeCallBack{
        void setTime(Calendar calendar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTimeCallBack = (TimeCallBack) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getContext(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);

        mTimeCallBack.setTime(mCalendar);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTimeCallBack = null;
    }
}
