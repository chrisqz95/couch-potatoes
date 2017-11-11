package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    DBHelper helper = new DBHelper();
    Button sendButton;
    EditText inputMessage;
    TextView chatConversation;
    String userID, chatRoom, messageID, timestamp, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = (Button) findViewById(R.id.sendButton);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        chatConversation = (TextView) findViewById(R.id.chatConversations);

        userID = (String) getIntent().getExtras().get( "userID" );
        chatRoom = (String) getIntent().getExtras().get( "chatID" );

        //chatConversation.setText( "" );

        // Add event handler to submit a new message to Firebase upon clicking the Send button
        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                messageID = helper.getNewChildKey( helper.getChatMessagePath() + chatRoom );
                timestamp = helper.getNewTimestamp();
                message = inputMessage.getText().toString();

                //inputMessage.setText( "" );

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
        helper.db.getReference( helper.getChatMessagePath() + chatRoom ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();

                while ( messages.hasNext() ) {
                    String messageID = messages.next().getKey();

                    chatConversation.setText( "" );

                    // Fetch all information corresponding to the current message
                    helper.db.getReference( helper.getMessagePath() + messageID ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String from = (String) dataSnapshot.child( "name" ).getValue();
                            String chatID = (String) dataSnapshot.child( "chat_id" ).getValue();
                            String message = (String) dataSnapshot.child( "text" ).getValue();
                            String timestamp = (String) dataSnapshot.child( "timestamp" ).getValue();

                            chatConversation.append( "  " + from + "    " + timestamp + ":\n" + "  " + message + "\n" );
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
    }
}
