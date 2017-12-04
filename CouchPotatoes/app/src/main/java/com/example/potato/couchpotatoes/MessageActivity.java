package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
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
    Button b_select_spinner;

  //Map<String, String> messageIDs = new HashMap<>();
  //Map<String, String> messageSenders = new HashMap<>();
  //Map<String, String> messageText = new HashMap<>();
    ArrayList<String> messageTime = new ArrayList<>();

    final int MESSAGE_FETCH_LIMIT = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // places toolbar into the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        b_select_spinner = (Button) findViewById(R.id.b_select_spinner);
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

        // Check if a spinner sent a message
        String spinner_message = getIntent().getStringExtra("message");
        if(!(spinner_message.equals("1"))){
            messageID = helper.getNewChildKey(helper.getChatMessagePath() + chatRoom);
            timestamp = helper.getNewTimestamp();
            message = spinner_message;

            // Clear the message text field on submitting a message
            messageArea.setText("");

            // Add the message to the chat.
            // Message data will be sent to the Firebase Database accordingly.
            if (!(message.equals(""))) {
                helper.addToChatMessage(chatRoom, messageID);
                helper.addToMessage(messageID, userID, displayName, chatRoom, timestamp, message);
            }
        }

        // Send button for messages
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



                // Fetch and display the messages
                while (messages.hasNext()) {
                    String messageID = messages.next().getKey();

                    // Fetch all information corresponding to the current message
                    helper.getDb().getReference(helper.getMessagePath() + messageID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                            String from = (String) dataSnapshot.child("name").getValue();
                            String chatID = (String) dataSnapshot.child("chat_id").getValue();
                            String message = (String) dataSnapshot.child("text").getValue();
                            String timestamp = (String) dataSnapshot.child("timestamp").getValue();
                            String timeString = "";

                            //Compare the last msg timestamp with the cur one, add timestamp if theres a gap
                            if (messageTime.size() >= 1) {
                                timeString = getTimeString(messageTime.get(messageTime.size() - 1), timestamp);
                            } else {
                                timeString = getTimeString(timestamp);
                            }

                            if (!(timeString.equals(""))) {
                                addMessageBox(timeString, 1, true);
                            }

                            if ( from.equals(displayName) ) {
                                addMessageBox(message, 1, false);}
                            else {
                                //String displayStr = displayName + ":\n";
                                addMessageBox(message, 2, false);
                            }



                            // Keep track of the messageID corresponding to the current message
                            //messageIDs.put(message, dataSnapshot.getKey());

                            // Keep track of the sender of the current message
                            //messageSenders.put(dataSnapshot.getKey(), from);

                            // Keep track of the text content of the current message
                            //messageText.put(dataSnapshot.getKey(), message);

                            //Keep track of the time of the current message
                            messageTime.add(timestamp);

                            Log.d("TEST", message + " " + dataSnapshot.getKey());
                        } }

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


        // OnClick to get to spinners
        b_select_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DIALOG FOR SPINNER
                String spinners[] = {"Spin the Bottle: Nice","Spin the Bottle: Naughty","Spin the Bottle: Food", "Spin the Bottle: Activity"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setIcon(R.mipmap.empty_wheel)
                        .setTitle("Choose Your Spinner!")
                        .setItems(spinners, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                switch (which) {
                                    case 0:
                                        intent = new Intent(((Dialog) dialog).getContext(), SpinBottleActivity.class);
                                        intent.putExtra("key", 1);
                                        intent.putExtra( "chatID", chatRoom );
                                        intent.putExtra("otherUsers", companion);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        System.out.println(LoginActivity.class);
                                        intent = new Intent(((Dialog) dialog).getContext(), SpinBottleActivity.class);
                                        intent.putExtra("key", 0);
                                        intent.putExtra( "chatID", chatRoom );
                                        intent.putExtra("otherUsers", companion);
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        intent = new Intent(((Dialog) dialog).getContext(), SpinToChooseActivity.class);
                                        intent.putExtra("key", 1);
                                        intent.putExtra( "chatID", chatRoom );
                                        intent.putExtra("otherUsers", companion);
                                        startActivity(intent);
                                        break;
                                    case 3:
                                        intent = new Intent(((Dialog) dialog).getContext(), SpinToChooseActivity.class);
                                        intent.putExtra("key", 0);
                                        intent.putExtra( "chatID", chatRoom );
                                        intent.putExtra("otherUsers", companion);
                                        startActivity(intent);
                                        break;

                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something else
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int which) {
                                // Do something else
                            }
                        });
                builder.create().show();
            }
        });
    }
        /*
         * Description: Edge case: This executes for the very first time a message is sent. Date
         * should always be displayed for first message
         */
        public String getTimeString(String curMsg) {
            String timeStr = "";
            String hourStr = "";
            String curMsgDate = curMsg.split("  ")[0];
            String curMsgTime = curMsg.split("  ")[1];

            int intCurMsgDate = Integer.parseInt(curMsgDate.replaceAll("-", ""));
            int intCurMsgTime = Integer.parseInt(curMsgTime.replaceAll(":", ""));

            String[] date = curMsgDate.split("-");
            String[] time = curMsgTime.split(":");

            //Determine if its AM or PM
            int hour = Integer.parseInt(time[0]);
            if (hour >= 12) {
                hourStr = (hour - 12) + ":" + time[1] + " PM";
            } else {
                hourStr = time[0] + ":" + time[1] + " AM";
            }

            //If the message has been longer than 6 day

            String monthString;
                //Date[1] is the month
            switch (date[1]) {
                    case "1":
                        monthString = "Jan";
                        break;
                    case "2":
                        monthString = "Feb";
                        break;
                    case "3":
                        monthString = "Mar";
                        break;
                    case "4":
                        monthString = "Apr";
                        break;
                    case "5":
                        monthString = "May";
                        break;
                    case "6":
                        monthString = "Jun";
                        break;
                    case "7":
                        monthString = "Jul";
                        break;
                    case "8":
                        monthString = "Aug";
                        break;
                    case "9":
                        monthString = "Sep";
                        break;
                    case "10":
                        monthString = "Oct";
                        break;
                    case "11":
                        monthString = "Nov";
                        break;
                    case "12":
                        monthString = "Dec";
                        break;
                    default:
                        monthString = "Invalid month";
                        break;
            }

                //Remove the 0 in front of day 01, 02, etc.
            int day = Integer.parseInt(date[2]);
            timeStr = monthString + " " + day + ", " + hourStr;

            return timeStr;
        }

        //Determine if they are playing hard to get by checking the timestamp difference
        public String getTimeString(String lastMsg, String curMsg) {

            String timeStr = "";
            String hourStr = "";


            String lastMsgDate = lastMsg.split("  ")[0];

            Log.d("MESSAGE", curMsg + ", current msg");

            String curMsgDate = curMsg.split("  ")[0];
            String lastMsgTime = lastMsg.split("  ")[1];
            String curMsgTime = curMsg.split("  ")[1];

            int intLastMsgDate = Integer.parseInt(lastMsgDate.replaceAll("-", ""));
            int intCurMsgDate = Integer.parseInt(curMsgDate.replaceAll("-", ""));
            int intLastMsgTime = Integer.parseInt(lastMsgTime.replaceAll(":", ""));
            int intCurMsgTime = Integer.parseInt(curMsgTime.replaceAll(":", ""));

            String[] date = curMsgDate.split("-");
            String[] time = curMsgTime.split(":");

            //Determine if its AM or PM
            int hour = Integer.parseInt(time[0]);
            if (hour >= 12) {
                hourStr = (hour - 12) + ":" + time[1] + " PM";
            } else {
                hourStr = time[0] + ":" + time[1] + " AM";
            }

            Calendar c = Calendar.getInstance();
            //Date today = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curDate = df.format(c.getTime()).split(" ")[0];
            //String curTime = df.format(c.getTime()).split(" ")[1];

            int intCurDate = Integer.parseInt(curDate.replaceAll("-", ""));
            //int intCurTime = Integer.parseInt(curTime.replaceAll(":", ""));

            if (intCurDate - intCurMsgDate < 1) {
                if (Math.abs(intCurMsgTime - intLastMsgTime) >= 30000) {
                    timeStr = hourStr;

                }
                //If the message has been longer than 1 day
            } else if ((intCurMsgDate - intLastMsgDate) >=1) {
                String monthString;
                //Date[1] is the month
                switch (date[1]) {
                    case "1":
                        monthString = "Jan";
                        break;
                    case "2":
                        monthString = "Feb";
                        break;
                    case "3":
                        monthString = "Mar";
                        break;
                    case "4":
                        monthString = "Apr";
                        break;
                    case "5":
                        monthString = "May";
                        break;
                    case "6":
                        monthString = "Jun";
                        break;
                    case "7":
                        monthString = "Jul";
                        break;
                    case "8":
                        monthString = "Aug";
                        break;
                    case "9":
                        monthString = "Sep";
                        break;
                    case "10":
                        monthString = "Oct";
                        break;
                    case "11":
                        monthString = "Nov";
                        break;
                    case "12":
                        monthString = "Dec";
                        break;
                    default:
                        monthString = "Invalid month";
                        break;
                }
                //Remove the 0 in front of day 01, 02, etc.
                int day = Integer.parseInt(date[2]);
                timeStr = monthString + " " + day + ", " + hourStr;

            }
            //If the message has been longer than 3 hours
            else if ((intCurMsgTime - intLastMsgTime) >= 30000) {
                timeStr = hourStr;
            }
            return timeStr;
	    }

        public void addMessageBox (String message, int type, boolean isTimeString){
            TextView textView = new TextView(MessageActivity.this);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if (isTimeString) {
                lp2.gravity = Gravity.CENTER_HORIZONTAL;
                textView.setTextColor(Color.GRAY);
                textView.setTextSize(14);
                textView.setText(message);

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


