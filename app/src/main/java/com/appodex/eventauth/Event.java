package com.appodex.eventauth;

import java.io.Serializable;

public class Event implements Serializable {
    private final String mEventId;
    private String mName, mSummary, mDate, mTime, mCoverPicUrl, mSharedBy;

    public Event(String eventId,
                 String name,
                 String summary,
                 String date,
                 String time,
                 String coverPicUrl) {
        mEventId = eventId;
        mName = name;
        mSummary = summary;
        mDate = date;
        mTime = time;
        mCoverPicUrl = coverPicUrl;
        mSharedBy = null;
    }

    public Event(String eventId,
                 String name,
                 String summary,
                 String date,
                 String time,
                 String coverPicUrl,
                 String sharedBy) {
        mEventId = eventId;
        mName = name;
        mSummary = summary;
        mDate = date;
        mTime = time;
        mCoverPicUrl = coverPicUrl;
        mSharedBy = sharedBy;
    }

    public String getEventId() {
        return mEventId;
    }

    public String getName() {
        return mName;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getCoverPicUrl() {
        return mCoverPicUrl;
    }

    public String getSharedBy() {
        return mSharedBy;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setSummary(String summary) {
        this.mSummary = summary;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public void setCoverPicUrl(String coverPicUrl) {
        this.mCoverPicUrl = coverPicUrl;
    }
}
