package com.appodex.eventauth;

import java.io.Serializable;

public class Event implements Serializable {
    private final String mEventId;
    private final String mName, mSummary, mDate, mTime, mCoverPicUrl;

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
}
