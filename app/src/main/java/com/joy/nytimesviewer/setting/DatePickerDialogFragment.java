package com.joy.nytimesviewer.setting;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by joy0520 on 2017/2/25.
 */

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_KEY_DATE = "extra_key_date";

    private Date mDate;

    public DatePickerDialogFragment() {
    }

    public static DatePickerDialogFragment newInstance(Date date) {
        DatePickerDialogFragment f = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_KEY_DATE, date);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = new GregorianCalendar();
        c.set(year, month, dayOfMonth);
        mDate = c.getTime();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(EXTRA_KEY_DATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = new GregorianCalendar();
        c.setTime(mDate);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                c.get(GregorianCalendar.YEAR),
                c.get(GregorianCalendar.MONTH),
                c.get(GregorianCalendar.DAY_OF_MONTH)) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                super.onClick(dialog, which);
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Log.i("DatePickerDialog", "BUTTON_POSITIVE");
                    // Return date-picking result to SettingDialog
                    Intent result = new Intent();
                    result.putExtra(EXTRA_KEY_DATE, mDate);
                    getTargetFragment().onActivityResult(SettingDialog.REQUEST_CODE_DATE_PICKER,
                            SettingDialog.RESULT_CODE_DATE_PICKER,
                            result);

                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    Log.i("DatePickerDialog", "BUTTON_NEGATIVE");
                } else {
                    Log.i("DatePickerDialog", "else");
                }
            }
        };
        return dialog;
    }


}
