package com.example.potato.couchpotatoes;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

// NOTE: Image View is only used to check for successful file upload.
//       Remove image view in actual app.
// Image chooser source: https://www.youtube.com/watch?v=UiqmekHYCSU
//
// Fragment onClickListener source: https://stackoverflow.com/questions/18711433/button-listener-for-button-in-fragment-in-android
// Runtime Permissions source:

public class UploadImageFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int REQUEST_WRITE_TO_EXTERNAL_STORAGE = 20;

    private int canWriteToExternalStorage = PackageManager.PERMISSION_DENIED;

    private DBHelper dbHelper;

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
        dbHelper = DBHelper.getInstance();

        // Source: https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
        // Workaround: Allows access to camera
        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder2.build());

        view = inflater.inflate(R.layout.fragment_upload_image, container, false);
        btnUploadImage = (FloatingActionButton) view.findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(this);

        // Check for permissions to write to external storage
        int permissionCheck = ContextCompat.checkSelfPermission(view.getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If no permissions, try to get permission to write to external storage
        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {}
        // No explanation needed, request permission
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_TO_EXTERNAL_STORAGE );

            canWriteToExternalStorage = PackageManager.PERMISSION_GRANTED;
        }

        // If write to external storage denied, only allow app to read images from SD Card
        if ( canWriteToExternalStorage == PackageManager.PERMISSION_DENIED ) {
            final String[] items = new String[] { "From SD Card" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.select_dialog_item, items );
            builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Select Photo");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        // Create Intent to choose a file from SD Card and delegate to "onActivityResult"
                        Intent intent = new Intent();
                        intent.setType( "image/*" );
                        intent.setAction( Intent.ACTION_GET_CONTENT );
                        startActivityForResult( Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE );
                }
            });
        }
        // Write to external storage allowed, allow app to use camera to write a new image taken to the SD Card
        // and then upload the new image
        else {
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
                        if ( canWriteToExternalStorage == PackageManager.PERMISSION_DENIED ) {
                            // Notify User cannot upload photo from camera
                            AlertDialog.Builder builderStoragePermissions = new AlertDialog.Builder(view.getContext());
                            builderStoragePermissions.setMessage( "App does not have permission to write to external storage! Cannot upload photo taken using camera." )
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
        }

        dialog = builder.create();

        return view;
    }

    @Override
    // Show upload dialog when Fragment's btnUploadImage button is clicked
    public void onClick(View v) {
        view = v;

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults ) {
        switch ( requestCode ) {
            case REQUEST_WRITE_TO_EXTERNAL_STORAGE: {
                if ( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    canWriteToExternalStorage = PackageManager.PERMISSION_GRANTED;
                }
                else {
                    canWriteToExternalStorage = PackageManager.PERMISSION_DENIED;
                }
            }
        }
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
                        UploadImage( is );
                        break;
                    // Case to cancel photo upload
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        // Create EditText field for AlertDialog
        input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Prompt user to upload the photo and allow user to enter a photo description
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage( "Enter description:" )
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Upload", dialogClickListener)
                .setView(input)
                .show();
    }

    // Uploads the passed InputStream of an image to Firebase Storage and then uploads
    // the photo meta data to Firebase Database
    public void UploadImage ( InputStream imgStream ) {
        photoID = dbHelper.getNewChildKey( dbHelper.getPhotoPath() );
        userID = dbHelper.getAuth().getUid();

        // Get reference to photo destination on Firebase Storage
        StorageReference ref = dbHelper.getStorage().getReference().child( dbHelper.getPhotoPath() + userID + "/" + photoID );

        // Get upload task of photo
        UploadTask uploadTask = ref.putStream( imgStream );

        // Handle upload task success and failure
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Notify User of failed upload
                AlertDialog.Builder builderUploadFailed = new AlertDialog.Builder(view.getContext());
                builderUploadFailed.setMessage( "Upload failed! Please try again." )
                        .setPositiveButton("Ok", null )
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            // Get photo uri from Firebase Storage
            Uri downloadUrl = taskSnapshot.getDownloadUrl();

            String title = imageCaptureUri.getLastPathSegment();
            String descr = input.getText().toString();
            String uri = ( downloadUrl != null ) ? downloadUrl.toString() : "";

            // Add photo meta data to Firebase Database
            dbHelper.addToUserPhoto( userID, photoID );
            dbHelper.addToPhoto( photoID, userID, title, descr, uri );

            // Notify User of successful upload
            Toast.makeText(getActivity(), "Photo uploaded successfully", Toast.LENGTH_LONG).show();

            Intent intent = new Intent( getActivity(), getActivity().getClass()  );
            intent.putExtra( "uid", userID );
            intent.putExtra( "isCurrentUser", true );
            getActivity().finish();
            startActivity( intent );
            }
        });
    }
}
