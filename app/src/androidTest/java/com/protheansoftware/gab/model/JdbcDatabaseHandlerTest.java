package com.protheansoftware.gab.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;


/**
 * @author oskar
 * Created by oskar on 2015-09-26.
 */
public class JdbcDatabaseHandlerTest {
    JdbcDatabaseHandler db;
    ArrayList<Integer> ids;
    @Before
    public void initialize() {
        db = new JdbcDatabaseHandler();
        ids = new ArrayList<Integer>();
        Random rand = new Random();
        for(int i=0;i<10;i++) {
            ids.add(rand.nextInt(200));
        }
        for(int i=0;i<10;i++) {
            db.addUser("TEST_USER: " + ids.get(i), ids.get(i));
        }
    }
    @Test
    public void testAddUser() throws Exception {
        if (!(db.getPotentialMatches().size() > 10)){
            throw new AssertionError();
        }
    }
    @Test
    public void testGetMyID() throws Exception {
        int id =  db.getMyId();
        if (id != 1){
            throw new AssertionError();
        }
    }
    @Test
     public void testAddLike() throws Exception {
        for(int i=0;i<5;i++){
            db.addLike(new JdbcDatabaseHandler(ids.get(i)).getMyId(),"TEST_USER: " + ids.get(i));
        }
        for(int i=0;i<2;i++){
            JdbcDatabaseHandler dbtemp = new JdbcDatabaseHandler(ids.get(i));
            dbtemp.addLike(1, "asdf");
        }
        for(int i=4;i<7;i++) {
            JdbcDatabaseHandler dbtemp = new JdbcDatabaseHandler(ids.get(i));
            dbtemp.addLike(1, "asdf");
        }
    }
    @Test
    public void testGetPotentialMatches() throws Exception{
        ArrayList<Profile> profiles = db.getPotentialMatches();
        if (profiles.size() <= 0) throw new AssertionError();
    }
    @Test
    public void testGetMatches() throws Exception{
        ArrayList<Profile> profiles = db.getMatches();
        if(profiles.size() != 3){
            throw new AssertionError();
        }
        for(int i=0;i<profiles.size();i++){
            System.out.println("MATCH: " + profiles.get(i).getName());
        }
    }
}
