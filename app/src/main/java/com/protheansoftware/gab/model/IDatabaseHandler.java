package com.protheansoftware.gab.model;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by oskar on 2015-09-26.
 */
public interface IDatabaseHandler {
    void addUser(String name, long id, ArrayList<String> interests);
    ArrayList<String> getInterests(int id);
    int getMyId() throws SQLException;
    void sessionStart(String wifi);
    void sessionStop();
    void addLike(int likeId, String likeName);
    void addDislike(int likeId, String likeName);
    ArrayList<Like> getDislikes() throws SQLException;
    void removeLike(int likeId);
    ArrayList<Profile> getPotentialMatches() throws SQLException;
    ArrayList<Profile> getMatches() throws SQLException;
    boolean hasLikedMe(int targetId) throws SQLException;
    Profile getUser(int id) throws SQLException;

    ArrayList<Message> getConversation(int user_id);
    void saveMessage(int recieverId, String message, String sinch_id);
}