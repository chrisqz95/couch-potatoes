package com.example.potato.couchpotatoes;

/**
 * Created by chris on 11/5/17.
 */

public abstract class User {
    private String uid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String birth_date; // TODO Do not use Camel Case here; Need to update on SQL database first before change
    private String gender;
    private String bio;
    private String profilePic;
    private double latitude;
    private double longitude;
    private boolean locked;
    private boolean suspended;

    public User () {}

    public User ( String uid, String firstName, String middleName, String lastName, String birth_date,
                  String gender, String bio, String profilePic, double latitude, double longitude,
                  boolean locked, boolean suspended ) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birth_date = birth_date;
        this.gender = gender;
        this.bio = bio;
        this.profilePic = profilePic;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locked = locked;
        this.suspended = suspended;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(String birthDate) {
        this.birth_date = birthDate;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Concatenates first, middle, and last names and returns result.
     *
     * @return Returns result of concatenating all names.
     */
    public String getDisplayName() {
        String name = "";

        if ( firstName != null ) {
            name += firstName;
        }
        if ( middleName != null ) {
            name += " ";
            name += middleName;
        }
        if ( lastName != null ) {
            name += " ";
            name += lastName;
        }

        return name;
    }
}
