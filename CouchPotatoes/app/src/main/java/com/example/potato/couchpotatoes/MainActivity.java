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
    private static int CAN_WRITE_TO_EXTERNAL_STORAGE = 0;
    private InputStream is, is2;
    private String userID, photoID;
    private DialogInterface.OnClickListener dialogClickListener;
    private EditText input;
    //private ContentResolver res;

    // Image chooser source: https://www.youtube.com/watch?v=UiqmekHYCSU

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Source: https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder2.build());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for permissions to write to external storage
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAN_WRITE_TO_EXTERNAL_STORAGE );
        }
        else {
            CAN_WRITE_TO_EXTERNAL_STORAGE = 1;
        }

        final String[] items = new String[] { "From Camera", "From SD Card" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.select_dialog_item, items );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pick photo from camera
                if ( which == 0 ) {
                    if ( CAN_WRITE_TO_EXTERNAL_STORAGE == 0 ) {
                        // Notify User cannot upload photo from camera
                        AlertDialog.Builder builderStoragePermissions = new AlertDialog.Builder(MainActivity.this);
                        builderStoragePermissions.setMessage( "App does not have permission to access external storage! Could not upload photo from camera." )
                                .setPositiveButton("Ok", dialogClickListener)
                                .show();
                    }
                    else {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "snapshot" + String.valueOf(System.currentTimeMillis() + ".jpg"));

                        imageCaptureUri = Uri.fromFile(file);

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
                    Intent intent = new Intent();
                    intent.setType( "image/*" );
                    intent.setAction( Intent.ACTION_GET_CONTENT );
                    startActivityForResult( Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE );
                }
            }
        });

        final AlertDialog dialog = builder.create();

        mImageView = (ImageView) findViewById( R.id.testImageView );
        //mImageView = (ImageView) findViewById( R.id.imageViewPhoto );
        uploadImage = (Button) findViewById( R.id.uploadImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        helper = new DBHelper();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        logout = (android.widget.Button) findViewById(R.id.logout);
        chat = (android.widget.Button) findViewById(R.id.viewChats);

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

        //helper.getStorage().getReference( "Photo/" + helper.getAuth().getCurrentUser() );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode != RESULT_OK )
            return;

        //Bitmap bitmap = null;
        //String path = "";

        if ( requestCode == PICK_FROM_FILE) {
            imageCaptureUri = data.getData();
        }

        try {
            is = getContentResolver().openInputStream(imageCaptureUri);
            is2 = getContentResolver().openInputStream(imageCaptureUri);
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }

        //if ( is != null ) {
        //    bitmap = BitmapFactory.decodeStream( is );
        //}

        /*
        if ( requestCode == PICK_FROM_FILE) {
            imageCaptureUri = data.getData();

            //Log.d( "TEST", data.getDataString() );

            //path = getRealPathFromURI(imageCaptureUri);

            //File file = new File( getFilesDir(), imageCaptureUri.getLastPathSegment() );

            //String docID = DocumentsContract.getDocumentId( imageCaptureUri );
            //String[] split = docID.split( ":" );
            //String type = split[0];

            try {
                is = getContentResolver().openInputStream(imageCaptureUri);
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }

            //Log.d( "TESTT", Environment.getExternalStorageDirectory().toString() );


            //if ( path == null ) {
            //    path = imageCaptureUri.getPath();
            //}
            //if ( path != null ) {
              //  bitmap = BitmapFactory.decodeFile( path );
                //bitmap = BitmapFactory.decodeFile( path );

            //}
            //Log.d( "TEST", imageCaptureUri.getEncodedPath() );

            if ( is != null ) {
                bitmap = BitmapFactory.decodeStream( is );
            }
        }
        else {
            //path = imageCaptureUri.getPath();
            //bitmap = BitmapFactory.decodeFile( path );
            //Log.d( "TEST", path );
            try {
                is = getContentResolver().openInputStream(imageCaptureUri);
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
            if ( is != null ) {
                bitmap = BitmapFactory.decodeStream( is );
            }
        }
        */

        //mImageView.setImageBitmap( bitmap );

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
        input = new EditText(MainActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        //input.setText( "Enter title" );

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage( "Enter description:" )
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Upload", dialogClickListener)
                .setView(input)
                //.setView( view )
                .show();
    }

    /*
    public String getRealPathFromURI ( Uri uri ) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, proj, null, null, null );
        if ( cursor == null ) return null;
        int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
        cursor.moveToFirst();
        return cursor.getString( column_index );
    }
    */

    public void UploadImage ( InputStream imgStream ) {
        // Upload image
        photoID = helper.getNewChildKey( helper.getPhotoPath() );
        userID = helper.getAuth().getUid();

        StorageReference ref = helper.getStorage().getReference().child( helper.getPhotoPath() + userID + "/" + photoID );

        UploadTask uploadTask = ref.putStream( imgStream );

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

