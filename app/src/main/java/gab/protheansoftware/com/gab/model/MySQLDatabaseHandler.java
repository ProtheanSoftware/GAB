package gab.protheansoftware.com.gab.model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by oskar on 2015-09-26.
 */
public class MySQLDatabaseHandler implements IDatabaseHandler {
    @Override
    public void addUser(String name, int id) {
        Connection con = null;
        PreparedStatement pstatement = null;

        String url = "jdbc:mysql://" + Secrets.DB_IP + "/gab";
        String user = Secrets.DB_USER;
        String password = Secrets.DB_PASSWORD;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        try{
            con = DriverManager.getConnection(url, user, password);

            pstatement = con.prepareStatement("INSERT INTO t_users('id', 'name', 'fb_id') VALUES(?,?,?)");
            pstatement.setString(1, null);
            pstatement.setString(2, name);
            pstatement.setString(3, String.valueOf(id));
            pstatement.executeUpdate();
        }catch (SQLException ex){
            Logger lgr = Logger.getLogger(MySQLDatabaseHandler.class.getName());
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
                Logger lgr = Logger.getLogger(MySQLDatabaseHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public int getMyId() {
        return 0;
    }

    @Override
    public void addLike(int likeId, String likeName) {

    }

    @Override
    public void removeLike(int likeId) {

    }

    @Override
    public ArrayList<Profile> getMatches() {
        return null;
    }
}
