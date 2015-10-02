package gab.protheansoftware.com.gab.model;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by oskar on 2015-09-26.
 */
public interface IDatabaseHandler {
    void addUser(String name, int id);
    int getMyId() throws SQLException;
//    void sessionStart();
 //   void sessionStop();
    void addLike(int likeId, String likeName);
    void removeLike(int likeId);
    ArrayList<Profile> getPotentialMatches() throws SQLException;
    ArrayList<Profile> getMatches() throws SQLException;
    boolean hasLikedMe(int targetId) throws SQLException;
    Profile getUser(int id) throws SQLException;
}
