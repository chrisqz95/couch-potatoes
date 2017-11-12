package com.example.potato.couchpotatoes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    DBHelper helper = new DBHelper();
    Button sendButton;
    EditText inputMessage;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> listAdapter;
    ListView listView;
    TextView userName;
    Map<String,String> messageIDs = new HashMap<>();
    Map<String,String> messageSenders = new HashMap<>();
    Map<String,String> messageText = new HashMap<>();
    DialogInterface.OnClickListener dialogClickListener;
    //DialogInterface.OnClickListener dialogClickListener3;
    String userID, chatRoom, displayName, messageID, timestamp, message;

    final int MESSAGE_FETCH_LIMIT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = (Button) findViewById(R.id.sendButton);
        inputMessage = (EditText) findViewById(R.id.inputMessage);

        // Use a ListView to display the list of messages
        listView = (ListView) findViewById(R.id.chatConversation);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter( listAdapter );

        // Get the current user's display name
        displayName = helper.getAuthUserDisplayName();

        // Display the current user's display name
        userName = (android.widget.TextView) findViewById(R.id.userName);
        userName.setText( displayName );

        // Get the current user's id
        userID = helper.auth.getUid();

        // Get the current chat room's id
        chatRoom = (String) getIntent().getExtras().get( "chatID" );

        // Add an event handler to submit a new message to the current chat upon clicking the Send button.
        // Message will be sent to Firebase Database.
        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                messageID = helper.getNewChildKey( helper.getChatMessagePath() + chatRoom );
                timestamp = helper.getNewTimestamp();
                message = inputMessage.getText().toString();

                // Clear the message text field on submitting a message
                inputMessage.setText( "" );

                // Add the message to the chat.
                // Message data will be sent to the Firebase Database accordingly.
                helper.addToChatMessage( chatRoom, messageID );
                helper.addToMessage( messageID, userID, displayName, chatRoom, timestamp, message );
            }
        });

        // Add an event handler to fetch and display all messages in the current chat
        helper.db.getReference( helper.getChatMessagePath() + chatRoom ).limitToLast( MESSAGE_FETCH_LIMIT ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();

                // Upon receiving new data, do not display previous messages more than once
                listItems.clear();

                // Fetch and display the messages
                while ( messages.hasNext() ) {
                    String messageID = messages.next().getKey();

                    //chatConversation.setText( "" );

                    // Fetch all information corresponding to the current message
                    helper.db.getReference( helper.getMessagePath() + messageID ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String from = (String) dataSnapshot.child( "name" ).getValue();
                            String chatID = (String) dataSnapshot.child( "chat_id" ).getValue();
                            String message = (String) dataSnapshot.child( "text" ).getValue();
                            String timestamp = (String) dataSnapshot.child( "timestamp" ).getValue();

                            // Add the user's display name and the timestamp to the message
                            String listItem = "  " + from + "    " + timestamp + ":\n" + "  " + message + "\n";

                            // Add the current message to the list
                            listItems.add( listItem );

                            // Notify the ListAdapter of changes
                            listAdapter.notifyDataSetChanged();

                            // Keep track of the messageID corresponding to the current message
                            messageIDs.put( listItem, dataSnapshot.getKey() );

                            // Keep track of the sender of the current message
                            messageSenders.put( dataSnapshot.getKey(), from );

                            // Keep track of the text content of the current message
                            messageText.put( dataSnapshot.getKey(), message );

                            Log.d( "TEST", listItem + " " + dataSnapshot.getKey() );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d( "TEST", databaseError.toString() );
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d( "TEST", databaseError.toString() );
            }
        });

        // Add an event handler to each message and allow the current user to edit or delete their messages.
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                // Identify the clicked message
                message = String.valueOf( parent.getItemAtPosition( position ));

                // Create DialogInterface click listener to delete the clicked message
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            // Case to delete the clicked message
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Log.d( "TEST", "YES" );
                                helper.removeFromMessage( messageIDs.get( message ) );
                                helper.removeFromChatMessage( chatRoom, messageIDs.get( message ) );
                                break;
                            // Case to cancel delete operation
                            case DialogInterface.BUTTON_NEGATIVE:
                                // Do nothing. User does not want to delete the clicked message
                                Log.d( "TEST", "NO" );
                                break;
                        }
                    }
                };

                /*
                dialogClickListener3 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                String newMessage = input.getText().toString();
                                helper.updateMessage( messageIDs.get( message ), helper.auth.getUid(),
                                        displayName, chatRoom, helper.getNewTimestamp(), newMessage );

                                // KNOWN BUG: updating message does not automatically clear and reload message list
                                // Reload current activity to fix this for now
                                finish();
                                startActivity( getIntent() );
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Log.d( "TEST", "NO" );
                                dialog.cancel();
                                break;
                        }
                    }
                };
                */

                // Create DialogInterface to prompt the user to edit or delete the clicked message.
                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            // Case to prompt user to edit the message
                            case DialogInterface.BUTTON_POSITIVE:
                                //Source: https://stackoverflow.com/questions/10903754/input-text-dialog-android

                                // Create AlertDialog to read user input and prompt the user to confirm changes
                                // to the clicked message.
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(listView.getContext());
                                builder3.setTitle("Title");

                                final EditText input = new EditText(listView.getContext());

                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                input.setText( messageText.get( messageIDs.get( message) ) );
                                builder3.setView(input);

                                // Case to submit changes and edit the clicked message
                                builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newMessage = input.getText().toString();
                                        helper.updateMessage( messageIDs.get( message ), helper.auth.getUid(),
                                                displayName, chatRoom, helper.getNewTimestamp(), newMessage );

                                        // KNOWN BUG: updating message does not automatically clear and reload message list
                                        // Reload current activity to fix this for now
                                        finish();
                                        startActivity( getIntent() );
                                    }
                                });

                                // Case to cancel changes to the clicked message
                                builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder3.setTitle( "Edit Message:" );

                                builder3.show();

                                Log.d( "TEST", "NO" );
                                break;
                            // Case to prompt user to delete message
                            case DialogInterface.BUTTON_NEGATIVE:
                                // Create AlertDialog to prompt the user to delete the clicked message.
                                AlertDialog.Builder builder = new AlertDialog.Builder(listView.getContext());
                                builder.setMessage( message + "\nDelete this message?" )
                                        .setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener)
                                        .show();
                                Log.d( "TEST", "NO" );
                                break;
                            // Case to cancel
                            case DialogInterface.BUTTON_NEUTRAL:
                                // Do nothing. User does not want to edit or delete the message.
                                Log.d( "TEST", "NEUTRAL" );
                                break;
                        }
                    }
                };

                // Only prompt the current user to edit or delete the clicked message if the current user originally sent
                // the message.
                if ( messageSenders.get( messageIDs.get( message ) ).equals( displayName ) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(message)
                            .setPositiveButton("Edit", dialogClickListener2)
                            .setNegativeButton("Delete", dialogClickListener2)
                            .setNeutralButton("Cancel", dialogClickListener2)
                            .show();
                }
            }
        });
    }
}
