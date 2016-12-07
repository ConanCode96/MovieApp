package com.conan.app.movieapp.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Conan on 12/2/2016.
 */

public class ReviewsDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        String title = bundle.getString("AUTHOR");
        String message = bundle.getString("CONTENT");
        final Uri uri = bundle.getParcelable("URI");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(title + "'s review").setMessage(message).setPositiveButton("View in Browser", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createViewIntent(uri);
                //onDismiss(dialog);
            }
        }).setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDismiss(dialog);
            }
        });

        return builder.create();
    }


    void createViewIntent(Uri uri) {

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(uri);

        if (viewIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(viewIntent);
    }

}
