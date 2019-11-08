package com.android.onehuman.smsecretsanta.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {

    private int groupID;
    private String groupName;
    private String maxPrice;


    public Group() {}

    protected Group(Parcel in) {
            this.groupID = in.readInt();
            this.groupName = in.readString();
            this.maxPrice = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupID);
        dest.writeString(this.groupName);
        dest.writeString(this.maxPrice);
    }

    public int getGroupID() { return groupID; }
    public String getGroupName() { return groupName; }
    public String getMaxPrice() { return maxPrice; }
    public void setGroupID(int groupID) { this.groupID = groupID; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setMaxPrice(String maxPrice) { this.maxPrice = maxPrice; }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Group){
            Group ptr = (Group) v;
            retVal = ptr.getGroupID() == this.groupID;
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Integer.valueOf(this.groupID);
        return hash;
    }



}