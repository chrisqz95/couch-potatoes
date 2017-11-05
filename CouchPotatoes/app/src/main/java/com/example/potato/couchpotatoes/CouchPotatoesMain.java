package com.example.potato.couchpotatoes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chris on 11/5/17.
 */

public class CouchPotatoesMain {

    public static void main(String[] args) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.setValue("Poop");
    }

}
