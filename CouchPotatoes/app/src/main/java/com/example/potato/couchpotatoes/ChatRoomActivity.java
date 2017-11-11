package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatRoomActivity extends AppCompatActivity {
    DBHelper helper = new DBHelper();
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> listAdapter;
    ListView listView;
    String userID = helper.auth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        listView = (ListView) findViewById(R.id.chatList);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter( listAdapter );

        //helper = new DBHelper();

        helper.db.getReference( helper.getUserChatPath() + userID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> elems = dataSnapshot.getChildren().iterator();

                while ( elems.hasNext() ) {
                    String nextElem = elems.next().getKey();
                    listItems.add( nextElem );
                    Log.d( "TEST", nextElem );
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                String chatID = String.valueOf( parent.getItemAtPosition( position ));
                //String firstName = (String) getIntent().getExtras().get( "firstName" );
                //String middleName = (String) getIntent().getExtras().get( "middleName" );
                //String lastName = (String) getIntent().getExtras().get( "lastName" );
                //String userID = "1"; // TODO change to use DBHelper after logging in

                //Log.d( "TEST", "CLIKED " + chatID );
                Intent intent = new Intent( getApplicationContext(), MessageActivity.class );
                intent.putExtra( "userID",  userID );
                intent.putExtra( "chatID", chatID );
                //intent.putExtra( "firstName", firstName );
                //intent.putExtra( "middleName", middleName );
                //intent.putExtra( "lastName", lastName );
                startActivity( intent );
                //finish();
            }
        });
    }
}