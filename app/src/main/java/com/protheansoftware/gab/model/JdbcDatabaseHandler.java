package com.protheansoftware.gab.model;


import android.util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by oskar on 2015-09-26.
 */
public class JdbcDatabaseHandler implements IDatabaseHandler {
    private int my_fb_id;
    private int myId;

    private static JdbcDatabaseHandler instance;

    public static final String TAG = "MYSQLDBH";
    //public JdbcDatabaseHandler(int id){
    //    this.my_fb_id = id;
    //}
    private JdbcDatabaseHandler(){
        my_fb_id = 7;
        myId = -1;
        Log.d(TAG, "Setting id..");
        try {
            myId = getMyId();
        } catch (SQLException e) {
        }
        Log.d(TAG, "You id is: " + myId);
    }

    public static JdbcDatabaseHandler getInstance(){
        if(instance == null){
           instance = new JdbcDatabaseHandler();
        }
        return instance;
    }

    private ArrayList<Like> selectFromLikes(String query){
        ArrayList<Like> likes = new ArrayList<Like>();

        Connection con = null;
        Statement statement =  null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            statement = con.createStatement();

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()){
                Like temp = new Like(rs.getInt("id"), rs.getInt("origin_id"), rs.getInt("like_id"), rs.getString("like_name"));
                likes.add(temp);
            }

        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return likes;
    }

    private ArrayList<Profile> selectFromUsers(String query){
        ArrayList<Profile> profiles = new ArrayList<Profile>();

        Connection con = null;
        Statement statement =  null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            statement = con.createStatement();

            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                Profile temp = new Profile(rs.getString("name"),rs.getInt("user_id"), rs.getInt("fb_id"));
                profiles.add(temp);
            }

        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return profiles;
    }

    @Override
    public void addUser(String name, int id) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT INTO t_users(user_id, name, fb_id) VALUES(?,?,?);");
            pstatement.setString(1, null);
            pstatement.setString(2, name);
            pstatement.setString(3, String.valueOf(id));
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public int getMyId() throws SQLException {
        int user_id = -1;
        if(myId != -1){
            return myId;
        }
        ArrayList<Profile> profiles = selectFromUsers("SELECT * FROM `t_users` WHERE `fb_id` =" + my_fb_id + " LIMIT 0 , 30;");
        if(profiles.size()>0){
            user_id = profiles.get(0).getId();
        }
        myId = user_id;
        return user_id;
    }

    @Override
    public void sessionStart(String wifi) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT INTO t_sessions(session_id, user_id, wifi, timestamp) VALUES(?,?,?,?);");
            pstatement.setString(1, null);
            pstatement.setString(2, String.valueOf(getMyId()));
            pstatement.setString(3, wifi);
            pstatement.setString(4, null);
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void sessionStop() {

    }

    @Override
    public void addLike(int likeId, String likeName) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT INTO t_likes(id, origin_id, like_id, like_name) VALUES(?,?,?,?);");
            pstatement.setString(1, null);
            pstatement.setString(2, String.valueOf(getMyId()));
            pstatement.setString(3, String.valueOf(likeId));
            pstatement.setString(4, likeName);
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

    }

    @Override
    public void addDislike(int likeId, String likeName) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT INTO t_dislikes(id, origin_id, like_id, like_name) VALUES(?,?,?,?);");
            pstatement.setString(1, null);
            pstatement.setString(2, String.valueOf(getMyId()));
            pstatement.setString(3, String.valueOf(likeId));
            pstatement.setString(4, likeName);
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public ArrayList<Like> getDislikes() throws SQLException{
        ArrayList<Like> dislikes = new ArrayList<Like>();

        selectFromLikes("SELECT * FROM `t_dislikes` WHERE `origin_id`= " + getMyId() + " LIMIT 0 , 30;");

        return dislikes;
    }

    @Override
    public void removeLike(int likeId) {

    }

    @Override
    public ArrayList<Profile> getPotentialMatches() throws SQLException {
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        ArrayList<Profile> allUsers = selectFromUsers("SELECT * FROM `t_users` LIMIT 0 , 60;");
        for(Profile temp: allUsers){
            if(!userExistsInDislikes(temp)){
                if(!userExistsInLikes(temp)){
                    profiles.add(temp);
                }
            }
        }
        return profiles;
    }

    private boolean userExistsInLikes(Profile user) {
        try {
            ArrayList<Like> myLikes = selectFromLikes("SELECT * FROM `t_likes` WHERE `origin_id`= " + getMyId() + " LIMIT 0 , 30;");
            for(Like temp: myLikes){
                if(temp.getLikeId() == user.getId()){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    private boolean userExistsInDislikes(Profile user) {
        try {
            ArrayList<Like> myDislikes = getDislikes();
            for(Like temp: myDislikes){
                if(temp.getLikeId() == user.getId()){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    public ArrayList<Profile> getMatches() throws SQLException {
        ArrayList<Like> likes = selectFromLikes("SELECT * FROM `t_likes` WHERE `origin_id`= " + getMyId() + " LIMIT 0 , 30;");
        ArrayList<Profile> matches = new ArrayList<Profile>();
        for(Like temp : likes){
            if(hasLikedMe(temp.getLikeId())){
                matches.add(getUser(temp.getLikeId()));
            }
        }
        return matches;
    }

    @Override
    public boolean hasLikedMe(int targetId) throws SQLException {
        ArrayList<Like> likes = selectFromLikes("SELECT * FROM `t_likes` WHERE `origin_id` =" + targetId + " AND `like_id` =" + getMyId() + " LIMIT 0 , 30;");
        return likes.size()>0;
    }

    @Override
    public Profile getUser(int id) throws SQLException {
        Profile profile = null;
        ArrayList<Profile> profiles = selectFromUsers("SELECT * FROM `t_users` WHERE `user_id` =" + id + " LIMIT 0 , 30;");
        return profiles.get(0);
    }
    public Profile getUserFromFBID(int fbID) throws SQLException {
        Profile profile = null;
        ArrayList<Profile> profiles = selectFromUsers("SELECT * FROM `t_users` WHERE `fb_id` =" + fbID + " LIMIT 0 , 30;");
        return profiles.get(0);
    }

    @Override
    public ArrayList<Message> getConversation(int user_id) {

        ArrayList<Message> messages = new ArrayList<Message>();

        Connection con = null;
        Statement statement =  null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            statement = con.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * " +
                    "FROM `t_messages` " +
                    "WHERE `sender_id` ="+getMyId()+" " +
                    "AND `reciever_id` ="+user_id+" " +
                    "OR `sender_id` ="+user_id+" " +
                    "AND `reciever_id` ="+getMyId()+" " +
                    "LIMIT 0, 30;");
            while (rs.next()){
                Message temp = new Message(rs.getInt("message_id"),rs.getInt("sender_id"), rs.getInt("reciever_id"), rs.getString("message"));
                messages.add(temp);
            }

        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return messages;
    }

    @Override
    public void saveMessage(int recieverId, String message, String sinch_id) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT IGNORE INTO t_messages(message_id, sender_id, reciever_id, message, sinch_id) VALUES(?,?,?,?,?);");
            pstatement.setString(1, null);
            pstatement.setString(2, String.valueOf(getMyId()));
            pstatement.setString(3, String.valueOf(recieverId));
            pstatement.setString(4, message);
            pstatement.setString(5, String.valueOf(sinch_id));
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(JdbcDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
