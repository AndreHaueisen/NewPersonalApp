package com.andrehaueisen.fitx.client.dialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by andre on 10/3/2016.
 */

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DateCallBack mDateCallBack;
    private Calendar mCalendar = Calendar.getInstance();

    public interface DateCallBack{
        void setDate(Calendar calendar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDateCallBack = (DateCallBack) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);

        mDateCallBack.setDate(mCalendar);
        dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDateCallBack = null;
    }
}
