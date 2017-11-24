package com.example.potato.couchpotatoes;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private android.widget.TextView userName;
    private android.widget.Button logout;
    private android.widget.Button chat;

    private Uri imageCaptureUri;
    private ImageView mImageView;
    private Button uploadImage;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static int CAN_WRITE_TO_EXTERNAL_STORAGE = PackageManager.PERMISSION_DENIED;
    private InputStream is, is2;
    private String userID, photoID;
    private DialogInterface.OnClickListener dialogClickListener;
    private EditText input;

    // Image chooser source: https://www.youtube.com/watch?v=UiqmekHYCSU

    // NOTE: Image View is only used to check for successful file upload.
    //       Remove image view in actual app.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        logout = (android.widget.Button) findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);

        mImageView = (ImageView) findViewById( R.id.testImageView );
        uploadImage = (Button) findViewById( R.id.uploadImage);

        // Source: https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
        // Workaround: Allows access to camera
        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder2.build());

        // Check for permissions to write to external storage
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If no permissions, try to get permission to write to external storage
        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAN_WRITE_TO_EXTERNAL_STORAGE );
        }
        else {
            CAN_WRITE_TO_EXTERNAL_STORAGE = PackageManager.PERMISSION_GRANTED;
        }

        // Create AlertDialog to prompt user for location of photo to upload
        final String[] items = new String[] { "From Camera", "From SD Card" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.select_dialog_item, items );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pick photo from camera
                if ( which == 0 ) {
                    // Check permissions to write to external storage
                    if ( CAN_WRITE_TO_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED ) {
                        // Notify User cannot upload photo from camera
                        AlertDialog.Builder builderStoragePermissions = new AlertDialog.Builder(MainActivity.this);
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
        final AlertDialog dialog = builder.create();

        // Display user's name if logged in
        if ( helper.isUserLoggedIn() ) {
            String displayName = helper.getAuthUserDisplayName();

            userName.setText( displayName );
        }
        // Else, redirect user to login page
        else {
            startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            finish();
        }

        // Add event handler to logout button to begin user logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.getAuth().signOut();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                finish();
            }
        });

        // Add event handler to chat button to start the ChatRoomActivity
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), ChatRoomActivity.class );
                //intent.putExtra( "userName", userName.getText() );
                startActivity( intent );
            }
        });

        // Add event handler to upload button to start upload process
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    // Get photo chosen by User and prompt user to upload photo
    // Note: Handles result of "startActivityForResult".
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode != RESULT_OK )
            return;

        // Get photo URI if photo chosen from SD Card
        if ( requestCode == PICK_FROM_FILE) {
            imageCaptureUri = data.getData();
        }

        // Get an InputStream of the photo
        try {
            is = getContentResolver().openInputStream(imageCaptureUri);
            is2 = getContentResolver().openInputStream(imageCaptureUri);
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
                        UploadImage( is2 );
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
        input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Prompt user to upload the photo and allow user to enter a photo description
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                AlertDialog.Builder builderUploadFailed = new AlertDialog.Builder(MainActivity.this);
                builderUploadFailed.setMessage( "Upload failed! Please try again." )
                        .setPositiveButton("Ok", null )
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d( "TEST", "File upload success" );

                // Notify User of successful upload
                AlertDialog.Builder builderUploadSuccess = new AlertDialog.Builder(MainActivity.this);
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
                helper.getDb().getReference( helper.getPhotoPath() ).child( photoID ).child( "uri" ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ( dataSnapshot.getValue() != null ) {
                            // Get reference to photo on Firebase Storage
                            StorageReference uriRef = helper.getStorage().getReferenceFromUrl( dataSnapshot.getValue().toString() );

                            // Set ImageView to contain photo
                            Glide.with(MainActivity.this)
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
            }
        });
    }
}

