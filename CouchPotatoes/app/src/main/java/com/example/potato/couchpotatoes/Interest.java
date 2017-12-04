package com.example.potato.couchpotatoes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Object representing a topic of interest and subcategories to be interested in.
 */
public class Interest implements Parcelable {
    private String interestName;
    private ArrayList<String> interestCategories;

    public Interest(String interestName) {
        this.interestName = interestName;
        interestCategories = new ArrayList<>();
    }

    public Interest(Parcel in) {
        this.interestName = in.readString();
        Object obj = in.readArrayList(String.class.getClassLoader());
        interestCategories = in.createStringArrayList();
    }

    public String getInterestName() {
        return interestName;
    }

    public ArrayList<String> getInterestCategories() {
        return interestCategories;
    }

    public void addInterestCategory(String category) {
        interestCategories.add(category);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Interest(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new Interest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.interestName);
        out.writeStringList(interestCategories);
    }
}
