package com.example.mooderation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

// TODO does this need to be a public class?
public class LocationDeniedDialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Location Permission Denied").setMessage("To enable location permission go to Settings -> Apps -> MOODeration -> Permissions -> Location").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();

    }
}
