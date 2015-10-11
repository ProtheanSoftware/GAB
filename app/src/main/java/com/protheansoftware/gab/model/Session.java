package com.protheansoftware.gab.model;

import java.util.Date;

/**
 * Created by time on 2015-10-10.
 */
public class Session {
    public final int session_id;
    public final int user_id;
    public final String ssid;
    public final Date timestamp;
    public Session(int session_id, int user_id, final String ssid, final Date timestamp) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.ssid = ssid;
        this.timestamp = (Date) timestamp.clone();
    }
}
