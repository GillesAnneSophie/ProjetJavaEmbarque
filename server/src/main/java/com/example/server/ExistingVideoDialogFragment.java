package com.example.server;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// https://guides.codepath.com/android/using-dialogfragment
public class ExistingVideoDialogFragment extends DialogFragment {

    public ExistingVideoDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }



    public static ExistingVideoDialogFragment newInstance(String title) {
        ExistingVideoDialogFragment frag = new ExistingVideoDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // builder.setMessage(R.string.dialog_fire_missiles)
        builder.setMessage("File already existing. Create copy ?")
                //.setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                .setPositiveButton(getActivity().getClass().getSimpleName(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        // getActivity().getClass().getSimpleName();
                        Log.i(this.getClass().getSimpleName(), getActivity().getClass().getSimpleName());
                        Log.i(this.getClass().getSimpleName(),"iiiiiiiiiiiiiiiiiiiiii");
                        // getActivity().getClass().tes
                    }
                })
                //.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog alert = new AlertDialog.Builder(getContext()).create();

                        if (dialog != null && alert.isShowing()) {
                            dialog.dismiss();
                        }
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

