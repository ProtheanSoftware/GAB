package gab.protheansoftware.com.gab.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


/**
 * @author oskar
 * Created by oskar on 2015-09-26.
 */
public class MySQLDatabaseHandlerTest {
    MySQLDatabaseHandler db;
    @Before
    public void initialize() {
        db = new MySQLDatabaseHandler();
    }
    @Test
    public void testAddUser() throws Exception {
        db.addUser("asdasdww22", 8);
    }
    @Test
    public void testGetMyID() throws Exception {
        int id =  db.getMyId();
        assert id == 2;
    }
    @Test
     public void testAddLike() throws Exception {
        db.addLike(2, "asdf");
    }
    @Test
    public void testGetPotentialMatches() throws Exception{
        ArrayList<Profile> profiles = db.getPotentialMatches();
        assert profiles.size()>0;
    }
    @Test
    public void testGetMatches() throws Exception{
        ArrayList<Profile> profiles = db.getMatches();
        assert profiles.size()>0;
    }
}