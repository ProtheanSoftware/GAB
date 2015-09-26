package gab.protheansoftware.com.gab.model;

/**
 * Created by oskar on 2015-09-26.
 */
public class Profile {
    private final String name;
    private final int id;
    public Profile(String name, int id){
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
