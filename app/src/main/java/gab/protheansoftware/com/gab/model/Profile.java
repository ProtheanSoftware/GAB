package gab.protheansoftware.com.gab.model;

/**
 * Created by oskar on 2015-09-26.
 */
public class Profile {
    private final String name;
    private final int id;
    private final int fbId;

    public Profile(String name, int id, int fbId){
        this.name = name;
        this.id = id;
        this.fbId = fbId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFbId() {
        return fbId;
    }
}
