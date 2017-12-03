//package com.example.potato.couchpotatoes;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class DialogSelectSpinner extends DialogFragment {
//    String spinners[] = {"Nice","Naught","Food", "Activity"};
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setIcon(R.mipmap.empty_wheel)
//                .setTitle("Choose Your Spinner!")
//
//                .setItems(spinners, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent;
//                        switch (which) {
//                            case 0:
//                                startActivity(new Intent(MessageActivity.this, SpinBottleActivity.class));
//                                break;
//                            case 1:
//                                startActivity(new Intent(this, SignUpActivity.class));
//                                break;
//                            case 2:
//                                startActivity(new Intent(this, ResetPasswordActivity.class));
//                                break;
//                            case 3:
//
//                                startActivity(new Intent(this, ResetPasswordActivity.class));
//                                break;
//
//                        }
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do something else
//                    }
//                })
//
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,	int which) {
//                        // Do something else
//                    }
//                });
//        return builder.create();
//    }
//}
