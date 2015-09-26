package gab.protheansoftware.com.gab.model;

import java.util.ArrayList;

/**
 * Created by oskar on 2015-09-26.
 */
public interface IDatabaseHandler {
    void addUser(String name, int id);
    int getMyId();
//    void sessionStart();
 //   void sessionStop();
    void addLike(int likeId, String likeName);
    void removeLike(int likeId);
    ArrayList<Profile> getMatches();
}
