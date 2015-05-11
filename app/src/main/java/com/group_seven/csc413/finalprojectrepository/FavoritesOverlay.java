package com.group_seven.csc413.finalprojectrepository;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by brianxautumn on 5/8/15.
 */
public class FavoritesOverlay extends DialogFragment implements DialogInterface.OnClickListener {
    String[] locations;
    MapsActivity callingActivity;
    ArrayList<Integer> mSelectedItems;

    static FavoritesOverlay newInstance(String[] inputLocations) {
        FavoritesOverlay f = new FavoritesOverlay();

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
        mSelectedItems = new ArrayList();
        // Use the Builder class for convenient dialog construction


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Favorites")
                .setMultiChoiceItems(locations, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callingActivity.clearFavorites();
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
