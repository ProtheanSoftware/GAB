package gab.protheansoftware.com.gab.model;

/**
 * Created by oskar on 2015-10-03.
 */
public class Like {
    private int id;
    private int originId;
    private int likeId;
    private String likeName;

    public Like(int id, int originId, int likeId, String likeName){
        this.id = id;
        this.originId = originId;
        this.likeId = likeId;
        this.likeName = likeName;
    }

    public int getId() {
        return id;
    }

    public int getOriginId() {
        return originId;
    }

    public int getLikeId() {
        return likeId;
    }

    public String getLikeName() {
        return likeName;
    }
}
