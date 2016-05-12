package com.yourtion.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Yourtion on 5/12/16.
 */
public class ChoosePickerFragment extends DialogFragment {
    public static final String EXTRA_CHOISE = "com.yourtion.criminalintent.choose";
    public static final String EXTRA_CHOISE_DATE = "com.yourtion.criminalintent.choose.date";
    public static final String EXTRA_CHOISE_TIME = "com.yourtion.criminalintent.choose.time";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence pickers[] = new CharSequence[]{
                getString(R.string.datetime_picker_date),
                getString(R.string.datetime_picker_time)
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.datetime_picker_title)
                .setItems(pickers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            sendResult(Activity.RESULT_OK, EXTRA_CHOISE_DATE);
                        } else if (which == 1) {
                            sendResult(Activity.RESULT_OK, EXTRA_CHOISE_TIME);
                        }
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String choose) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_CHOISE, choose);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
