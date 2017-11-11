package com.example.potato.couchpotatoes;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    String userID, chatRoom;

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

        chatConversation.setText( "" );

        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String messageID = helper.getNewChildKey( helper.getChatMessagePath() + chatRoom );
                String timestamp = helper.getNewTimestamp();
                String message = inputMessage.getText().toString();

                inputMessage.setText( "" );

                helper.addToChatMessage( chatRoom, messageID );
                helper.addToMessage( messageID, userID, chatRoom, timestamp, message );
            }
        });

        /*
        inputMessage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                inputMessage.setText( "" );
            }
        });
        */

        helper.db.getReference( helper.getChatMessagePath() + chatRoom ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();

                while ( messages.hasNext() ) {
                    String messageID = messages.next().getKey();

                    helper.db.getReference( helper.getMessagePath() + messageID ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String from = (String) dataSnapshot.child( "user_id" ).getValue();
                            String chatID = (String) dataSnapshot.child( "chat_id" ).getValue();
                            String message = (String) dataSnapshot.child( "text" ).getValue();
                            String timestamp = (String) dataSnapshot.child( "timestamp" ).getValue();

                            chatConversation.append( "  " + from + ": " + message + "\n" );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
    private void appendToChatConversation(DataSnapshot snapshot) {
        Iterator<DataSnapshot> messages = snapshot.getChildren().iterator();

        while ( messages.hasNext() ) {
            String message = (String) messages.next().toString();
            chatConversation.append( userID + ": " + message );
        }
    }
    */

}
