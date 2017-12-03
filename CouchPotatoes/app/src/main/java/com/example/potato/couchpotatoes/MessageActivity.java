package com.example.potato.couchpotatoes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
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
    ArrayList<String> messageTime = new ArrayList<>();

    final int MESSAGE_FETCH_LIMIT = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
                            boolean gapMsg = false;

                            //Compare the last msg timestamp with the cur one, add timestamp if theres a gap
                            if (messageTime.size() >= 1) {
                                gapMsg = isGapBetweenMsg(messageTime.get(messageTime.size() - 1), timestamp);
                            }

                            if (gapMsg) {
                                addMessageBox(timestamp, 1, gapMsg);
                            }

                            if ( from.equals(displayName) ) {
                                addMessageBox(message, 1, false);}
                            else {
                                //String displayStr = displayName + ":\n";
                                addMessageBox(message, 2, false);
                            }

                            // Keep track of the messageID corresponding to the current message
                            messageIDs.put(message, dataSnapshot.getKey());

                            // Keep track of the sender of the current message
                            messageSenders.put(dataSnapshot.getKey(), from);

                            // Keep track of the text content of the current message
                            messageText.put(dataSnapshot.getKey(), message);

                            //Keep track of the time of the current message
                            messageTime.add(timestamp);

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

    //Determine if they are playing hard to get by checking the timestamp difference
    public boolean isGapBetweenMsg(String lastMsg, String curMsg) {
        int lastMsgDate = Integer.parseInt(
                (lastMsg.split("  ")[0].replaceAll("-", "")));

        int curMsgDate = Integer.parseInt(
                (curMsg.split("  ")[0].replaceAll("-", "")));

        int lastMsgTime = Integer.parseInt(
                (lastMsg.split("  ")[1].replaceAll(":", "")));

        int curMsgTime = Integer.parseInt(
                (curMsg.split("  ")[1].replaceAll(":", "")));

        //Longer than a day
        if ((curMsgDate - lastMsgDate) >= 1) return true;

        //Longer than 3 hours
        return ((curMsgTime - lastMsgTime) >= 30000);
    }

    // Handles action in clicking on an item in the options menu
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

		return super.onOptionsItemSelected(item);
    }

    public void addMessageBox (String message,int type, boolean gapMsg){
        TextView textView = new TextView(MessageActivity.this);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (gapMsg) {
            lp2.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(18);
            String[] time = message.split("  ")[1].split(":");
            //Determine AM or PM
            int hour = Integer.parseInt(time[0]);
            String timeStr;
            if (hour >= 12) {
                timeStr = (hour-12) + " : " + time[1] + " PM";
            } else {
                timeStr = time[0] + " : " + time[1] + " AM";
            }

            textView.setText(timeStr);

        } else if (type == 1) {
            textView.setText(message);
            textView.setTextSize(20);
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setText(message);
            textView.setTextSize(20);
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
            textView.setTextColor(Color.BLACK);
        }

        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}

