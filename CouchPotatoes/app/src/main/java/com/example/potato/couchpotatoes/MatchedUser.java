package com.example.potato.couchpotatoes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Contains information about a matched user.
 */
public class MatchedUser extends User implements Parcelable {

    private Bitmap profilePic;
    private ArrayList<Interest> interests;

//    /**
//     * No email.
//     * @param uid
//     * @param firstName
//     * @param middleName
//     * @param lastName
//     * @param dob
//     * @param gender
//     * @param city
//     * @param state
//     * @param country
//     * @param bio
//     * @param latitude
//     * @param longitude
//     * @param locked
//     * @param suspended
//     */
//    protected MatchedUser ( String uid, String firstName, String middleName, String lastName, String dob, String gender, String city, String state, String country, String bio,
//                         double latitude, double longitude, boolean locked, boolean suspended ) {
//        super( null, uid, firstName, middleName, lastName, dob, gender, city, state, country, bio,
//                latitude, longitude, locked, suspended );
//    }

    protected MatchedUser(String uid) {
        super( null, uid, null, null, null,
                null, null, null, null, null, null,
                0, 0, false, false );
        interests = new ArrayList<>();
    }

    // begin parcelable methods
    // suppress uncheck warnings for readArrayList
    @SuppressWarnings("unchecked")
    protected MatchedUser(Parcel in) {
        super(in);
        this.profilePic = in.readParcelable(Bitmap.class.getClassLoader());
        this.interests = in.readArrayList(Interest.class.getClassLoader());
    }

    public static final Parcelable.Creator<MatchedUser> CREATOR = new Parcelable.Creator<MatchedUser>() {

        @Override
        public MatchedUser createFromParcel(Parcel in) {
            return new MatchedUser(in);
        }

        @Override
        public MatchedUser[] newArray(int size) {
            return new MatchedUser[size];
        }
    };

    @Override
    public int describeContents() {
        // Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flag) {
        super.writeToParcel(out, flag);
        out.writeValue(this.profilePic);
        out.writeList(this.interests);
    }

    // end parcelable methods

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public ArrayList<Interest> getInsterests() {
        return interests;
    }

    public void addInterest(Interest interest) {
        interests.add(interest);
    }

    @Override
    public void setEmail(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUid(String uid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLatitude(double latitude) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLongitude(double longitude) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLocked() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocked(boolean locked) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSuspended() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSuspended(boolean suspended) {
        throw new UnsupportedOperationException();
    }
}
