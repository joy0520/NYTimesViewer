package com.joy.nytimesviewer.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.joy.nytimesviewer.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joy0520 on 2017/2/25.
 */

public class SettingDialog extends DialogFragment {
    public static final int REQUEST_CODE_DATE_PICKER = 0;
    public static final int RESULT_CODE_DATE_PICKER = 0;

    public static final String EXTRA_KEY_SETTING_MODEL = "extra_key_setting_model";
    private SettingModel mModel;
    private Callback mCallback;

    @BindView(R.id.title_date_picker)
    TextView titleDatePicker;
    @BindView(R.id.date_picker_text)
    TextView datePickerText;
    @BindView(R.id.title_sort)
    TextView titleSort;
    @BindView(R.id.button_sort)
    Spinner buttonSort;
    @BindView(R.id.title_desk)
    TextView titleDesk;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.choice_arts)
    CheckBox choiceArts;
    @BindView(R.id.choice_fashion)
    CheckBox choiceFashion;
    @BindView(R.id.choice_sports)
    CheckBox choiceSports;


    public static SettingDialog newInstance(String title) {
        SettingDialog frag = new SettingDialog();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingDialog);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> desks = new ArrayList<>();
                if (choiceArts.isChecked())
                    desks.add(getString(R.string.setting_query_news_desk_arts));
                if (choiceFashion.isChecked())
                    desks.add(getString(R.string.setting_query_news_desk_fashion));
                if (choiceSports.isChecked())
                    desks.add(getString(R.string.setting_query_news_desk_sports));
                mModel.news_desk = desks;

                mCallback.onSettingDialogFinished(mModel);
            }
        }).setTitle(getArguments().getString("title", "Filter"))
        ;
        // Load the custom dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_setting, null);
        ButterKnife.bind(this, customView);


        updateViews();
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void setSettingModel(SettingModel model) {
        mModel = model;
    }

    private void updateViews() {
        if (mModel == null) {
            return;
        }
        Log.i("SettingDialog.updateViews()", "mModel="+mModel);
        // Setup date
        final Date date = mModel.date;
        datePickerText.setText(SettingModel.toDisplayString(date));
        datePickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance(date);
                dialog.setTargetFragment(SettingDialog.this, REQUEST_CODE_DATE_PICKER);
                dialog.show(fm, "fragment_setting_date_dialog");
            }
        });

        // Setup sorted-by spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.setting_dialog_title_sorted_by, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        buttonSort.setAdapter(adapter);
        buttonSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("SettingDialog.onItemClick", "position=" + position);
                switch (position) {
                    case 0:
                        mModel.sorted_by = SettingModel.SORTED_BY_OLDEST;
                        break;
                    case 1:
                        mModel.sorted_by = SettingModel.SORTED_BY_NEWEST;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonSort.setSelection(mModel.sorted_by);

        // Setup choices
        choiceArts.setChecked(mModel.news_desk.contains(getString(R.string.setting_query_news_desk_arts)));
        choiceFashion.setChecked(mModel.news_desk.contains(getString(R.string.setting_query_news_desk_fashion)));
        choiceSports.setChecked(mModel.news_desk.contains(getString(R.string.setting_query_news_desk_sports)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DATE_PICKER) {
            Log.i("SettingDialog.onActivityResult()", "resultCode=" + resultCode + ", data=" + data);
            // Retrieve date-picking result and update date
            mModel.date = (Date) data.getSerializableExtra(DatePickerDialogFragment.EXTRA_KEY_DATE);
            datePickerText.setText(SettingModel.toDisplayString(mModel.date));
        }
    }

    public interface Callback {
        void onSettingDialogFinished(SettingModel model);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}
