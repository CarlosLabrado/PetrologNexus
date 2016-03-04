package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class DeviceEfficiency {

    @SerializedName("RemoteDeviceId")
    @Expose
    private Integer RemoteDeviceId;
    @SerializedName("DateTimeStamp")
    @Expose
    private String DateTimeStamp;
    @SerializedName("Number")
    @Expose
    private Integer Number;
    @SerializedName("Efficiency")
    @Expose
    private Integer Efficiency;
    @SerializedName("PercentFillage")
    @Expose
    private Integer PercentFillage;
    @SerializedName("PercentFillageSetting")
    @Expose
    private Integer PercentFillageSetting;

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
     * @return The DateTimeStamp
     */
    public String getDateTimeStamp() {
        return DateTimeStamp;
    }

    /**
     * @param DateTimeStamp The DateTimeStamp
     */
    public void setDateTimeStamp(String DateTimeStamp) {
        this.DateTimeStamp = DateTimeStamp;
    }

    /**
     * @return The Number
     */
    public Integer getNumber() {
        return Number;
    }

    /**
     * @param Number The Number
     */
    public void setNumber(Integer Number) {
        this.Number = Number;
    }

    /**
     * @return The Efficiency
     */
    public Integer getEfficiency() {
        return Efficiency;
    }

    /**
     * @param Efficiency The Efficiency
     */
    public void setEfficiency(Integer Efficiency) {
        this.Efficiency = Efficiency;
    }

    /**
     * @return The PercentFillage
     */
    public Integer getPercentFillage() {
        return PercentFillage;
    }

    /**
     * @param PercentFillage The PercentFillage
     */
    public void setPercentFillage(Integer PercentFillage) {
        this.PercentFillage = PercentFillage;
    }

    /**
     * @return The PercentFillageSetting
     */
    public Integer getPercentFillageSetting() {
        return PercentFillageSetting;
    }

    /**
     * @param PercentFillageSetting The PercentFillageSetting
     */
    public void setPercentFillageSetting(Integer PercentFillageSetting) {
        this.PercentFillageSetting = PercentFillageSetting;
    }

}
