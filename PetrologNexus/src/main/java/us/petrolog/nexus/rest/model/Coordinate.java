package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class Coordinate {

    @SerializedName("X")
    @Expose
    private Integer X;
    @SerializedName("Y")
    @Expose
    private Integer Y;

    /**
     * @return The X
     */
    public Integer getX() {
        return X;
    }

    /**
     * @param X The X
     */
    public void setX(Integer X) {
        this.X = X;
    }

    /**
     * @return The Y
     */
    public Integer getY() {
        return Y;
    }

    /**
     * @param Y The Y
     */
    public void setY(Integer Y) {
        this.Y = Y;
    }

}