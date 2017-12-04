package com.example.potato.couchpotatoes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chris on 11/5/17.
 */

public abstract class User {
    private String email;
    private String uid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String city;
    private String state;
    private String country;
    private String bio;
    private double latitude;
    private double longitude;
    private boolean locked;
    private boolean suspended;

    public User () {}

    public User ( String email, String uid, String firstName, String middleName, String lastName, String dob,
                  String gender, String city, String state, String country, String bio,
                  double latitude, double longitude, boolean locked, boolean suspended ) {
        this.email = email;
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.city = city;
        this.state = state;
        this.country = country;
        this.bio = bio;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locked = locked;
        this.suspended = suspended;
    }

    // this stays here in case we want to make current user in the future. makes it easier
    protected User(Parcel in) {
        this.email = in.readString();
        this.uid = in.readString();
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.dob = in.readString();
        this.gender = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.bio = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        boolean[] isLockedOrSuspended = new boolean[2];
        in.readBooleanArray( isLockedOrSuspended );
        this.locked = isLockedOrSuspended[0];
        this.suspended = isLockedOrSuspended[1];
    }

    // this stays here in case we want to make current user in the future. makes it easier.
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getEmail());
        dest.writeString(this.getUid());
        dest.writeString(this.getFirstName());
        dest.writeString(this.getMiddleName());
        dest.writeString(this.getLastName());
        dest.writeString(this.getDob());
        dest.writeString(this.getGender());
        dest.writeString(this.getCity());
        dest.writeString(this.getState());
        dest.writeString(this.getCountry());
        dest.writeString(this.getBio());
        dest.writeDouble(this.getLatitude());
        dest.writeDouble(this.getLongitude());
        boolean[] isLockedOrSuspended = new boolean[] {
                this.isLocked(), this.isSuspended() };
        dest.writeBooleanArray( isLockedOrSuspended );

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
