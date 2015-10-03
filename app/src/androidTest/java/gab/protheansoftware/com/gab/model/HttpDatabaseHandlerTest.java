package gab.protheansoftware.com.gab.model;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by oskar on 2015-10-03.
 */
public class HttpDatabaseHandlerTest extends TestCase {
    HttpDatabaseHandler db;
    @Before
    public void initialize() {
    }
    @Test
    public void testAddUser() throws Exception {
        db = new HttpDatabaseHandler();

        db.addUser("asdasdww22", 8);
    }
}