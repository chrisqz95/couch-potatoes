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
    //TextView chatConversation;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> listAdapter;
    ListView listView;
    String userID, chatRoom, displayName, messageID, timestamp, message;
    TextView userName;
    Map<String,String> messageIDs = new HashMap<>();
    Map<String,String> messageSenders = new HashMap<>();
    Map<String,String> messageText = new HashMap<>();
    DialogInterface.OnClickListener dialogClickListener;
    DialogInterface.OnClickListener dialogClickListener3;

    final int MESSAGE_FETCH_LIMIT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayName = helper.getAuthUserDisplayName();

        userName = (android.widget.TextView) findViewById(R.id.userName);
        //userName.setText( (String) getIntent().getExtras().get( "userName" ) );
        userName.setText( displayName );

        sendButton = (Button) findViewById(R.id.sendButton);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        //chatConversation = (TextView) findViewById(R.id.chatConversations);
        listView = (ListView) findViewById(R.id.chatConversation);

        //listView = (ListView) findViewById(R.id.chatList);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter( listAdapter );

        userID = helper.auth.getUid();
        chatRoom = (String) getIntent().getExtras().get( "chatID" );

        //chatConversation.setText( "" );

        // Add event handler to submit a new message to Firebase upon clicking the Send button
        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                messageID = helper.getNewChildKey( helper.getChatMessagePath() + chatRoom );
                timestamp = helper.getNewTimestamp();
                message = inputMessage.getText().toString();

                inputMessage.setText( "" );

                // Fetch the user's name from Firebase and attach user's name to message
                helper.db.getReference( helper.getUserPath() + userID ).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String firstName = (String) dataSnapshot.child( "firstName" ).getValue();
                        String middleName = (String) dataSnapshot.child( "middleName" ).getValue();
                        String lastName = (String) dataSnapshot.child( "lastName" ).getValue();

                        // Get user's name as a single string
                        String name = helper.getFullName( firstName, middleName, lastName );

                        // If null, use userID instead
                        if ( name.equals( "" ) ) {
                            if ( userID != null ) {
                                name = userID.substring(name.length(), name.length() - 8);
                            }
                            else {
                                name = "No name";
                            }
                        }

                        helper.addToChatMessage( chatRoom, messageID );
                        helper.addToMessage( messageID, userID, name, chatRoom, timestamp, message );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d( "TEST", databaseError.toString() );
                    }
                });
            }
        });

        // Add event handler to fetch and display all messages in the current chat
        helper.db.getReference( helper.getChatMessagePath() + chatRoom ).limitToLast( MESSAGE_FETCH_LIMIT ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();

                listItems.clear();

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

                            //chatConversation.append( "  " + from + "    " + timestamp + ":\n" + "  " + message + "\n" );
                            String listItem = "  " + from + "    " + timestamp + ":\n" + "  " + message + "\n";
                            listItems.add( listItem );
                            listAdapter.notifyDataSetChanged();
                            messageIDs.put( listItem, dataSnapshot.getKey() );
                            messageSenders.put( dataSnapshot.getKey(), from );
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

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                message = String.valueOf( parent.getItemAtPosition( position ));

                //helper.removeFromMessage( messageIDs.get( message ) );
                //helper.removeFromChatMessage( chatRoom, messageIDs.get( message ) );

                //Log.d( "TEST", message );
                //Log.d( "TEST", messageIDs.get( message ) );
                // source: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
                /*
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Log.d( "TEST", "YES" );
                                helper.removeFromMessage( messageIDs.get( message ) );
                                helper.removeFromChatMessage( chatRoom, messageIDs.get( message ) );
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Log.d( "TEST", "NO" );
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage( message + "\nDelete this message?" ).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                        */
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Log.d( "TEST", "YES" );
                                helper.removeFromMessage( messageIDs.get( message ) );
                                helper.removeFromChatMessage( chatRoom, messageIDs.get( message ) );
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Log.d( "TEST", "NO" );
                                break;
                        }
                    }
                };

                dialogClickListener3 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Log.d( "TEST", "YES" );
                                helper.updateMessage( messageIDs.get( message ), helper.auth.getUid(), displayName, chatRoom, helper.getNewTimestamp(), "New message" );
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Log.d( "TEST", "NO" );
                                break;
                        }
                    }
                };

                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Log.d( "TEST", "YES" );
                                //helper.removeFromMessage( messageIDs.get( message ) );
                                //helper.removeFromChatMessage( chatRoom, messageIDs.get( message ) );
                                /*
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(listView.getContext());
                                builder3.setMessage( message + "\nEdit this message?" ).setPositiveButton("Yes", dialogClickListener3)
                                        .setNegativeButton("No", dialogClickListener3).show();
                                        */
                                //Source: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(listView.getContext());
                                builder3.setTitle("Title");

// Set up the input
                                final EditText input = new EditText(listView.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                input.setText( messageText.get( messageIDs.get( message) ) );
                                builder3.setView(input);

// Set up the buttons
                                builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newMessage = input.getText().toString();
                                        helper.updateMessage( messageIDs.get( message ), helper.auth.getUid(), displayName, chatRoom, helper.getNewTimestamp(), newMessage );
                                    }
                                });
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

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                AlertDialog.Builder builder = new AlertDialog.Builder(listView.getContext());
                                builder.setMessage( message + "\nDelete this message?" ).setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                Log.d( "TEST", "NO" );
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                Log.d( "TEST", "NEUTRAL" );
                                break;
                        }
                    }
                };

                // Only alter message if current user is the sender
                if ( messageSenders.get( messageIDs.get( message ) ).equals( displayName ) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(message).setPositiveButton("Edit", dialogClickListener2)
                            .setNegativeButton("Delete", dialogClickListener2).setNeutralButton("Cancel", dialogClickListener2).show();
                }
            }
        });
    }
}
