package com.example.potato.couchpotatoes;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.Queue;

/**
 * Created by chris on 11/5/17.
 */

public class DBHelper {

    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseUser user;

    public DBHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
    }

    public boolean loginUser(String email, String password) {
        if (auth.signInWithEmailAndPassword(email, password).isSuccessful()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean createUser(String email, String password) {
        if (auth.createUserWithEmailAndPassword(email, password).isSuccessful()) {
            return true;
        }
        else {
            return false;
        }
    }

    public Queue<MatchedUser> getMatchedUsers(FirebaseUser user) {
        return null;
    }

}
