package gab.protheansoftware.com.gab.model;


import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by oskar on 2015-09-26.
 */
public class JdbcDatabaseHandler implements IDatabaseHandler {
    private static final int my_fb_id = 6;

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
        ArrayList<Profile> profiles = selectFromUsers("SELECT * FROM `t_users` WHERE `fb_id` =" + my_fb_id + " LIMIT 0 , 30;");
        if(profiles.size()>0){
            user_id = profiles.get(0).getId();
        }
        return user_id;
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
    public void removeLike(int likeId) {

    }
    @Override
    public ArrayList<Profile> getPotentialMatches() throws SQLException {

        ArrayList<Profile> profiles = selectFromUsers("SELECT * FROM `t_users` LIMIT 0 , 30;");

        return profiles;
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

}
