package com.appodex.eventauth;

public class Collaborator {

    private String mName, mEmail;
    public  Collaborator(String name, String email) {
        mName = name;
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getName() {
        return mName;
    }
}
