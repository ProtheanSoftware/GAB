package com.protheansoftware.gab.model;

import java.util.Date;

/**
 * Data model for handling session data
 * @author david
 * Created by David Ström on 2015-10-10.
 */
public class Session {
    public final int session_id;
    public final int user_id;
    public final String dgw;
    public final Date timestamp;
    public Session(int session_id, int user_id, final String dgw, final Date timestamp) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.dgw = dgw;
        this.timestamp = (Date) timestamp.clone();
    }
}
