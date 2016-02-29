package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class DeviceGraph {

    @SerializedName("RemoteDeviceId")
    @Expose
    private Integer RemoteDeviceId;
    @SerializedName("Coordinates")
    @Expose
    private List<Coordinate> Coordinates = new ArrayList<Coordinate>();
    @SerializedName("UpdateTime")
    @Expose
    private String UpdateTime;

    /**
     * @return The RemoteDeviceId
     */
    public Integer getRemoteDeviceId() {
        return RemoteDeviceId;
    }

    /**
     * @param RemoteDeviceId The RemoteDeviceId
     */
    public void setRemoteDeviceId(Integer RemoteDeviceId) {
        this.RemoteDeviceId = RemoteDeviceId;
    }

    /**
     * @return The Coordinates
     */
    public List<Coordinate> getCoordinates() {
        return Coordinates;
    }

    /**
     * @param Coordinates The Coordinates
     */
    public void setCoordinates(List<Coordinate> Coordinates) {
        this.Coordinates = Coordinates;
    }

    /**
     * @return The UpdateTime
     */
    public String getUpdateTime() {
        return UpdateTime;
    }

    /**
     * @param UpdateTime The UpdateTime
     */
    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

}