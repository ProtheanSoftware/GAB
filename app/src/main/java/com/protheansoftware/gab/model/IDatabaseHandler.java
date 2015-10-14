package com.protheansoftware.gab.model;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by oskar on 2015-09-26.
 */
public interface IDatabaseHandler {
    /**
     * Adds user to database
     * @param name Name of user
     * @param id Facebook id of user
     * @param interests Interests of user, later parsed to JSon
     */
    void addUser(String name, long id, ArrayList<String> interests);

    /**
     * Gets interests from user with id: id
     * @param id Target user ID
     * @return ArrayList of interests(String)
     */
    ArrayList<String> getInterests(int id);

    /**
     * Gets your database ID
     * @return Id int
     * @throws SQLException
     */
    int getMyId();

    /**
     * Starts a session for the user using a string wifi
     * @param wifi The wifi the user is on
     */
    void sessionStart(String wifi);

    /**
     * Stops all current sessions for the user
     */
    void sessionStop();

    /**
     * Add a like
     * @param likeId The like target user ID
     * @param likeName The like target name
     */
    void addLike(int likeId, String likeName);

    /**
     * Add a dislike
     * @param likeId The dislike target user ID
     * @param likeName The dislike target name
     */
    void addDislike(int likeId, String likeName);

    /**
     * Gets all dislike for the current user
     * @return Array of Likes, same data as dislikes.
     * @throws SQLException
     */
    ArrayList<Like> getDislikes() throws SQLException;

    /**
     * Removes a like from the database, this can be used for "unmatching" a user.
     * @param likeId target user ID
     */
    void removeLike(int likeId);

    /**
     * Gets users the current user have not yet interacted with, i.e not "disliked" or "liked"
     * @return ArrayList of Profiles.
     * @throws SQLException
     */
    ArrayList<Profile> getPotentialMatches() throws SQLException;

    /**
     * Gets users the current user have liked that have liked him/her back.
     * I.e users him/her have matched with
     * @return ArrayList of Profiles.
     * @throws SQLException
     */
    ArrayList<Profile> getMatches() throws SQLException;

    /**
     * Checks if the target user have liked the current user
     * @param targetId target user
     * @return true if the target user have liked the current user
     * @throws SQLException
     */
    boolean hasLikedMe(int targetId) throws SQLException;

    /**
     * Generates a profile from the database using a database ID
     * @param id The user id we will generate profile for
     * @return Profile of user with target id
     * @throws SQLException
     */
    Profile getUser(int id) throws SQLException;

    /**
     * Gets conversation I.e all messages between the current user and the target user with user_id
     * @param user_id Target user
     * @return ArrayList of (30)messages between the two users
     */
    ArrayList<Message> getConversation(int user_id);

    /**
     * Saves the message in database.
     * sinch_id is required since we need to check when sinch tries to resend undelivered messages we already
     * have saved in our database; so they are not doublesent.
     * @param recieverId The recipient of the message
     * @param message The textbody of the message
     * @param sinch_id The id of the message sinch sends
     */
    void saveMessage(int recieverId, String message, String sinch_id);
}