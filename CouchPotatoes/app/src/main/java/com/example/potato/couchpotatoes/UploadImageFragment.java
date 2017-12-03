package com.example.potato.couchpotatoes;

import android.*;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Admin on 11/24/17.
 */


// NOTE: Image View is only used to check for successful file upload.
//       Remove image view in actual app.
// Image chooser source: https://www.youtube.com/watch?v=UiqmekHYCSU
//
// Fragment onClickListener source: https://stackoverflow.com/questions/18711433/button-listener-for-button-in-fragment-in-android

public class UploadImageFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static int CAN_WRITE_TO_EXTERNAL_STORAGE = 0;

    private DBHelper helper;

    private View view;
    private ImageView mImageView;
    private FloatingActionButton btnUploadImage;

    private DialogInterface.OnClickListener dialogClickListener;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText input;
    private InputStream is;
    private Uri imageCaptureUri;

    private String userID, photoID;

    public UploadImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper = new DBHelper();

        // Source: https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
        // Workaround: Allows access to camera
        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder2.build());

        view = inflater.inflate(R.layout.fragment_upload_image, container, false);
        btnUploadImage = (FloatingActionButton) view.findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(this);
        //mImageView = (ImageView) view.findViewById( R.id.testUploadImageView );

        // Check for permissions to write to external storage
        int permissionCheck = ContextCompat.checkSelfPermission(view.getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If no permissions, try to get permission to write to external storage
        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAN_WRITE_TO_EXTERNAL_STORAGE );
        }
        else {
            CAN_WRITE_TO_EXTERNAL_STORAGE = PackageManager.PERMISSION_GRANTED;
        }

        // Create AlertDialog to prompt user for location of photo to upload
        final String[] items = new String[] { "From Camera", "From SD Card" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.select_dialog_item, items );
        builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Select Photo");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pick photo from camera
                if ( which == 0 ) {
                    // Check permissions to write to external storage
                    if ( CAN_WRITE_TO_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED ) {
                        // Notify User cannot upload photo from camera
                        AlertDialog.Builder builderStoragePermissions = new AlertDialog.Builder(view.getContext());
                        builderStoragePermissions.setMessage( "App does not have permission to access external storage! Could not upload photo from camera." )
                                .setPositiveButton("Ok", dialogClickListener)
                                .show();
                    }
                    else {
                        // Create Intent to capture a new photo using the camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // Write the new photo to the user's SD Card
                        File file = new File(Environment.getExternalStorageDirectory(), "snapshot" + String.valueOf(System.currentTimeMillis() + ".jpg"));

                        // Get the photo's uri
                        imageCaptureUri = Uri.fromFile(file);

                        // Delegate to "onActivityResult"
                        try {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                            intent.putExtra("return data", true);

                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Close dialog and delegate to Intent
                    dialog.cancel();
                }
                // Pick photo from SD Card
                else {
                    // Create Intent to choose a file from SD Card and delegate to "onActivityResult"
                    Intent intent = new Intent();
                    intent.setType( "image/*" );
                    intent.setAction( Intent.ACTION_GET_CONTENT );
                    startActivityForResult( Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE );
                }
            }
        });

        dialog = builder.create();

        return view;
    }

    @Override
    // Show upload dialog when Fragment's btnUploadImage button is clicked
    public void onClick(View v) {
        view = v;

        dialog.show();
    }

    // Get photo chosen by User and prompt user to upload photo
    // Note: Handles result of "startActivityForResult".
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode != RESULT_OK )
            return;

        // Get photo URI if photo chosen from SD Card
        if ( requestCode == PICK_FROM_FILE) {
            imageCaptureUri = data.getData();
        }

        // Get an InputStream of the photo
        try {
            is = view.getContext().getContentResolver().openInputStream(imageCaptureUri);
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }

        // Handler for photo upload AlertDialog
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // Case to upload photo
                    case DialogInterface.BUTTON_POSITIVE:
                        Log.d( "TEST", "YES" );
                        UploadImage( is );
                        break;
                    // Case to cancel photo upload
                    case DialogInterface.BUTTON_NEGATIVE:
                        Log.d( "TEST", "NO" );
                        break;
                }
            }
        };

        //LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        //final View view = factory.inflate(R.layout.content_image_view, null);

        // Create EditText field for AlertDialog
        input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Prompt user to upload the photo and allow user to enter a photo description
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage( "Enter description:" )
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Upload", dialogClickListener)
                .setView(input)
                //.setView( view )
                .show();
    }

    // Uploads the passed InputStream of an image to Firebase Storage and then uploads
    // the photo meta data to Firebase Database
    public void UploadImage ( InputStream imgStream ) {
        photoID = helper.getNewChildKey( helper.getPhotoPath() );
        userID = helper.getAuth().getUid();

        // Get reference to photo destination on Firebase Storage
        StorageReference ref = helper.getStorage().getReference().child( helper.getPhotoPath() + userID + "/" + photoID );

        // Get upload task of photo
        UploadTask uploadTask = ref.putStream( imgStream );

        // Handle upload task success and failure
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d( "TEST", "File uplaod failed" );

                // Notify User of failed upload
                AlertDialog.Builder builderUploadFailed = new AlertDialog.Builder(view.getContext());
                builderUploadFailed.setMessage( "Upload failed! Please try again." )
                        .setPositiveButton("Ok", null )
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d( "TEST", "File upload success" );

                // Notify User of successful upload
                AlertDialog.Builder builderUploadSuccess = new AlertDialog.Builder(view.getContext());
                builderUploadSuccess.setMessage( "Upload successful!" )
                        .setPositiveButton("Ok", null )
                        .show();

                // Get photo uri from Firebase Storage
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Log.d( "TEST", "Download URL: " + downloadUrl );

                String title = imageCaptureUri.getLastPathSegment();
                String descr = input.getText().toString();
                String uri = ( downloadUrl != null ) ? downloadUrl.toString() : "";

                // Add photo meta data to Firebase Database
                helper.addToUserPhoto( userID, photoID );
                helper.addToPhoto( photoID, userID, title, descr, uri );

                // Update image view with photo
                // Note: Here we only want to add listener once to verify photo upload
                // NOTE: Image View is only used to check for successful file upload.
                //       Remove image view and all code below in actual app.
                /*
                helper.getDb().getReference( helper.getPhotoPath() ).child( photoID ).child( "uri" ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ( dataSnapshot.getValue() != null ) {
                            // Get reference to photo on Firebase Storage
                            StorageReference uriRef = helper.getStorage().getReferenceFromUrl( dataSnapshot.getValue().toString() );

                            // Set ImageView to contain photo
                            Glide.with(getActivity())
                                    .using(new FirebaseImageLoader())
                                    .load(uriRef)
                                    .into(mImageView);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TODO
                        Log.d( "TEST", databaseError.getMessage() );
                    }
                });
                */
            }
        });
    }
}
