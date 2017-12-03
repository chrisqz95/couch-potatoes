package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    DBHelper helper = new DBHelper();
    DatabaseReference reference1, reference2;
    String userID, chatRoom, displayName, messageID, timestamp, message, companion;
    TextView userName;

    Map<String, String> messageIDs = new HashMap<>();
    Map<String, String> messageSenders = new HashMap<>();
    Map<String, String> messageText = new HashMap<>();

    final int MESSAGE_FETCH_LIMIT = 50;

    // For the side navigation bar and its listed items
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // places toolbar into the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        //Firebase.setAndroidContext(this);
        reference1 = helper.getDb().getReference();
        reference2 = helper.getDb().getReference();

        // Get the current user's display name
        displayName = helper.getAuthUserDisplayName();

        // Get the current user's id
        userID = helper.getAuth().getUid();

        // Get the current chat room's id
        chatRoom = (String) getIntent().getExtras().get("chatID");
        // Get the other person in the chat
        companion = (String) getIntent().getExtras().get("otherUsers");

        // Display the current user's display name
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(companion);
        //reference1 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        //reference2 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageID = helper.getNewChildKey(helper.getChatMessagePath() + chatRoom);
                timestamp = helper.getNewTimestamp();
                message = messageArea.getText().toString();

                // Clear the message text field on submitting a message
                messageArea.setText("");

                // Add the message to the chat.
                // Message data will be sent to the Firebase Database accordingly.
                if (!(message.equals(""))) {
                    helper.addToChatMessage(chatRoom, messageID);
                    helper.addToMessage(messageID, userID, displayName, chatRoom, timestamp, message);
                }
            }
        });

        /*reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String from = (String) dataSnapshot.child( "name" ).getValue();
                String chatID = (String) dataSnapshot.child( "chat_id" ).getValue();
                String message = (String) dataSnapshot.child( "text" ).getValue();
                String timestamp = (String) dataSnapshot.child( "timestamp" ).getValue();

                if (true) {
                    addMessageBox("You:-\n" + message, 1);
                } else {
                    //addMessageBox(userDetails.chatWith + ":-\n" + message, 2);
                }
                // Keep track of the messageID corresponding to the current message
                messageIDs.put( message, dataSnapshot.getKey() );

                // Keep track of the sender of the current message
                messageSenders.put( dataSnapshot.getKey(), from );

                // Keep track of the text content of the current message
                messageText.put( dataSnapshot.getKey(), message );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

        // Add an event handler to fetch and display all messages in the current chat
        helper.getDb().getReference(helper.getChatMessagePath() + chatRoom).limitToLast(MESSAGE_FETCH_LIMIT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();

                // Upon receiving new data, do not display previous messages more than once

                // Fetch and display the messages
                while (messages.hasNext()) {
                    String messageID = messages.next().getKey();

                    //chatConversation.setText( "" );

                    // Fetch all information corresponding to the current message
                    helper.getDb().getReference(helper.getMessagePath() + messageID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String from = (String) dataSnapshot.child("name").getValue();
                            String chatID = (String) dataSnapshot.child("chat_id").getValue();
                            String message = (String) dataSnapshot.child("text").getValue();
                            String timestamp = (String) dataSnapshot.child("timestamp").getValue();


                            if ( from.equals(displayName) ) {
                                addMessageBox(message, 1);}
                            else {
                                //String displayStr = displayName + ":\n";
                                addMessageBox(message, 2);
                            }
                            // Keep track of the messageID corresponding to the current message
                            messageIDs.put(message, dataSnapshot.getKey());

                            // Keep track of the sender of the current message
                            messageSenders.put(dataSnapshot.getKey(), from);

                            // Keep track of the text content of the current message
                            messageText.put(dataSnapshot.getKey(), message);

                            Log.d("TEST", message + " " + dataSnapshot.getKey());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("TEST", databaseError.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TEST", databaseError.toString());
            }
        });

    }


    public void addMessageBox (String message,int type){
        TextView textView = new TextView(MessageActivity.this);
        textView.setText(message);

        textView.setTextSize(20);
        //textView.setFont

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.chat_bubble_in);
            textView.setTextColor(Color.WHITE);
        } else {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.chat_bubble_out);
            textView.setTextColor(Color.BLACK);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    // Handles opening up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_main, menu);
        return true;
    }

    // Handles action in clicking on
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_potato_questions) {
            return true;
        } else if (id == R.id.menu_spin_wheel) {
            return true;
        } else if (id == R.id.menu_start_date) {
            return true;
        } else if (id == R.id.menu_end_date) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


