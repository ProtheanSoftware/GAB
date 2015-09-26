package gab.protheansoftware.com.gab.model;

import org.junit.Test;



/**
 * @author oskar
 * Created by oskar on 2015-09-26.
 */
public class MySQLDatabaseHandlerTest {
    @Test
    public void testAddUser() throws Exception {
        MySQLDatabaseHandler db = new MySQLDatabaseHandler();
        db.addUser("asdf", 6);
    }
}