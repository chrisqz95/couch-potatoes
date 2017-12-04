package com.example.potato.couchpotatoes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Admin on 11/7/17.
 */

public class CurrentUser extends User {
    private LinkedList<String> matchedUsersId = new LinkedList<>();
        // may or may not need. Alternative is to populate one a linked list in HomeActivity instead.
    private LinkedList<MatchedUser> matchedUsers = new LinkedList<>();

    public CurrentUser () {}

    /*
    * the constructor that initializes the CurrentUser object
    **/
    public CurrentUser ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                  String gender, String city, String state, String country, String bio,
                  double latitude, double longitude, boolean locked, boolean suspended )
    {
        super( email, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
                latitude, longitude, locked, suspended );
    }

    public String getIntroduction() {
        return "Name: " + this.getFirstName() + " " + this.getLastName();
    }

    /* Everything below here is for implementing Parcelable */

    public static final Parcelable.Creator<CurrentUser> CREATOR = new Creator<CurrentUser>() {
        @Override
        public CurrentUser createFromParcel(Parcel in) {
            return new CurrentUser(in);
        }

        @Override
        public CurrentUser[] newArray(int size) {
            return new CurrentUser[size];
        }
    };

    @Override
    public int describeContents() {
        // Auto-generated method stub
        return 0;
    }

    /*
    * Retrieving CurrentUser data from Parcel object
    * This constructor is invoked by the method createFromParcel() of the object CREATOR
    **/
    // pass in all information from the Parcel object into the CurrentUser object
    // the CurrentUser object is used in the receiving activity
    // NOTE: the order that we write into CurrentUser MUST be the same as the order we read it
    protected CurrentUser(Parcel in) {
        super(in);
    }

    /*
    * Storing the CurrentUser data to Parcel object in the parameter
    **/
    // this will be used in the sending activity
    // once its fields are written, the Parcel object 'dest' will be sent to the receiving activity
    // NOTE: the order that we write into CurrentUser MUST be the same as the order we read it
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
