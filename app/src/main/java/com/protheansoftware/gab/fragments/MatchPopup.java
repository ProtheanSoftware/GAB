package com.protheansoftware.gab.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.protheansoftware.gab.R;

/**
 * Created by Oscar Hall on 18/10/15.
 * Just a dialog to let you know you have a match and you can gab with them.
 */
public class MatchPopup extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.match_popup_text)
                    .setPositiveButton(R.string.match_popup_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Take me to the matchesScreen
                        }
                    })
                    .setNegativeButton(R.string.match_popup_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // return a finished dialog. Call it with .show(getFragmentHandler, "<Your tag>");
            return builder.create();
        }

}
