package com.group_seven.csc413.finalprojectrepository;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brianxautumn on 5/6/15.
 */
public class HistoryOverlay extends DialogFragment implements DialogInterface.OnClickListener {
    String[] locations;
    MapsActivity callingActivity;

    static HistoryOverlay newInstance(String[] inputLocations) {
        HistoryOverlay f = new HistoryOverlay();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putStringArray("loc" , inputLocations);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        MapsActivity callingActivity = (MapsActivity) getActivity();

        dialog.dismiss();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locations = getArguments().getStringArray("loc");
        callingActivity = (MapsActivity) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("History")
                .setItems(locations, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callingActivity.onHistorySelectValue(id);
                    }

                })

                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        callingActivity.clearAllHistory();
                    }


                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });



        // Create the AlertDialog object and return it
        return builder.create();
    }

}
