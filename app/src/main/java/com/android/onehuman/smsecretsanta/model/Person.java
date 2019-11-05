package com.android.onehuman.smsecretsanta.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class Person implements Parcelable {

    private int id;
    private String name;
    private String phone;
    private String mail;
    private List<Person> candidates;

    public Person() {
        this.candidates=new ArrayList<>();
    }

    protected Person(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
            this.phone = in.readString();
            this.mail = in.readString();
            this.candidates = new ArrayList<Person>();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getMail() {
        return this.mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Person> getCandidates(){ return this.candidates; }
    public void setCandidates(ArrayList<Person> c) {
        this.candidates = c;
    }
    public void addCandidates(Person p){ candidates.add(p); }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.mail);
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Person){
            Person ptr = (Person) v;
            retVal = ptr.getId() == this.id;
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Integer.valueOf(this.id);
        return hash;
    }



}