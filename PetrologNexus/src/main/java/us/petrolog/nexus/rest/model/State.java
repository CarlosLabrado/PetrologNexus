package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class State {

    @SerializedName("RemoteDeviceId")
    @Expose
    private Integer RemoteDeviceId;
    @SerializedName("LasUpdateSince")
    @Expose
    private String LasUpdateSince;
    @SerializedName("WellStatus")
    @Expose
    private Boolean WellStatus;
    @SerializedName("PumpOff")
    @Expose
    private Boolean PumpOff;
    @SerializedName("StrokesThisCycle")
    @Expose
    private Integer StrokesThisCycle;
    @SerializedName("StrokesLastCycle")
    @Expose
    private Integer StrokesLastCycle;
    @SerializedName("SecondsToNextStart")
    @Expose
    private Integer SecondsToNextStart;
    @SerializedName("SecondsToNextStartFormatted")
    @Expose
    private String SecondsToNextStartFormatted;
    @SerializedName("Automatic")
    @Expose
    private Boolean Automatic;
    @SerializedName("Efficiency")
    @Expose
    private Integer Efficiency;
    @SerializedName("SignalStrength")
    @Expose
    private Integer SignalStrength;
    @SerializedName("KickoffCount")
    @Expose
    private Integer KickoffCount;
    @SerializedName("MessageNumber")
    @Expose
    private Integer MessageNumber;
    @SerializedName("PercentFillage")
    @Expose
    private Integer PercentFillage;
    @SerializedName("NoLoad")
    @Expose
    private Boolean NoLoad;
    @SerializedName("NoPosition")
    @Expose
    private Boolean NoPosition;
    @SerializedName("LastCommand")
    @Expose
    private String LastCommand;
    @SerializedName("TimeOut")
    @Expose
    private Integer TimeOut;
    @SerializedName("PercentFillageSetting")
    @Expose
    private Integer PercentFillageSetting;
    @SerializedName("RemoteDeviceStatus")
    @Expose
    private Integer RemoteDeviceStatus;
    @SerializedName("RemoteDeviceStatusDescription")
    @Expose
    private String RemoteDeviceStatusDescription;

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
     * @return The LasUpdateSince
     */
    public String getLasUpdateSince() {
        return LasUpdateSince;
    }

    /**
     * @param LasUpdateSince The LasUpdateSince
     */
    public void setLasUpdateSince(String LasUpdateSince) {
        this.LasUpdateSince = LasUpdateSince;
    }

    /**
     * @return The WellStatus
     */
    public Boolean getWellStatus() {
        return WellStatus;
    }

    /**
     * @param WellStatus The WellStatus
     */
    public void setWellStatus(Boolean WellStatus) {
        this.WellStatus = WellStatus;
    }

    /**
     * @return The PumpOff
     */
    public Boolean getPumpOff() {
        return PumpOff;
    }

    /**
     * @param PumpOff The PumpOff
     */
    public void setPumpOff(Boolean PumpOff) {
        this.PumpOff = PumpOff;
    }

    /**
     * @return The StrokesThisCycle
     */
    public Integer getStrokesThisCycle() {
        return StrokesThisCycle;
    }

    /**
     * @param StrokesThisCycle The StrokesThisCycle
     */
    public void setStrokesThisCycle(Integer StrokesThisCycle) {
        this.StrokesThisCycle = StrokesThisCycle;
    }

    /**
     * @return The StrokesLastCycle
     */
    public Integer getStrokesLastCycle() {
        return StrokesLastCycle;
    }

    /**
     * @param StrokesLastCycle The StrokesLastCycle
     */
    public void setStrokesLastCycle(Integer StrokesLastCycle) {
        this.StrokesLastCycle = StrokesLastCycle;
    }

    /**
     * @return The SecondsToNextStart
     */
    public Integer getSecondsToNextStart() {
        return SecondsToNextStart;
    }

    /**
     * @param SecondsToNextStart The SecondsToNextStart
     */
    public void setSecondsToNextStart(Integer SecondsToNextStart) {
        this.SecondsToNextStart = SecondsToNextStart;
    }

    /**
     * @return The SecondsToNextStartFormatted
     */
    public String getSecondsToNextStartFormatted() {
        return SecondsToNextStartFormatted;
    }

    /**
     * @param SecondsToNextStartFormatted The SecondsToNextStartFormatted
     */
    public void setSecondsToNextStartFormatted(String SecondsToNextStartFormatted) {
        this.SecondsToNextStartFormatted = SecondsToNextStartFormatted;
    }

    /**
     * @return The Automatic
     */
    public Boolean getAutomatic() {
        return Automatic;
    }

    /**
     * @param Automatic The Automatic
     */
    public void setAutomatic(Boolean Automatic) {
        this.Automatic = Automatic;
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
     * @return The SignalStrength
     */
    public Integer getSignalStrength() {
        return SignalStrength;
    }

    /**
     * @param SignalStrength The SignalStrength
     */
    public void setSignalStrength(Integer SignalStrength) {
        this.SignalStrength = SignalStrength;
    }

    /**
     * @return The KickoffCount
     */
    public Integer getKickoffCount() {
        return KickoffCount;
    }

    /**
     * @param KickoffCount The KickoffCount
     */
    public void setKickoffCount(Integer KickoffCount) {
        this.KickoffCount = KickoffCount;
    }

    /**
     * @return The MessageNumber
     */
    public Integer getMessageNumber() {
        return MessageNumber;
    }

    /**
     * @param MessageNumber The MessageNumber
     */
    public void setMessageNumber(Integer MessageNumber) {
        this.MessageNumber = MessageNumber;
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
     * @return The NoLoad
     */
    public Boolean getNoLoad() {
        return NoLoad;
    }

    /**
     * @param NoLoad The NoLoad
     */
    public void setNoLoad(Boolean NoLoad) {
        this.NoLoad = NoLoad;
    }

    /**
     * @return The NoPosition
     */
    public Boolean getNoPosition() {
        return NoPosition;
    }

    /**
     * @param NoPosition The NoPosition
     */
    public void setNoPosition(Boolean NoPosition) {
        this.NoPosition = NoPosition;
    }

    /**
     * @return The LastCommand
     */
    public String getLastCommand() {
        return LastCommand;
    }

    /**
     * @param LastCommand The LastCommand
     */
    public void setLastCommand(String LastCommand) {
        this.LastCommand = LastCommand;
    }

    /**
     * @return The TimeOut
     */
    public Integer getTimeOut() {
        return TimeOut;
    }

    /**
     * @param TimeOut The TimeOut
     */
    public void setTimeOut(Integer TimeOut) {
        this.TimeOut = TimeOut;
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

    /**
     * @return The RemoteDeviceStatus
     */
    public Integer getRemoteDeviceStatus() {
        return RemoteDeviceStatus;
    }

    /**
     * @param RemoteDeviceStatus The RemoteDeviceStatus
     */
    public void setRemoteDeviceStatus(Integer RemoteDeviceStatus) {
        this.RemoteDeviceStatus = RemoteDeviceStatus;
    }

    /**
     * @return The RemoteDeviceStatusDescription
     */
    public String getRemoteDeviceStatusDescription() {
        return RemoteDeviceStatusDescription;
    }

    /**
     * @param RemoteDeviceStatusDescription The RemoteDeviceStatusDescription
     */
    public void setRemoteDeviceStatusDescription(String RemoteDeviceStatusDescription) {
        this.RemoteDeviceStatusDescription = RemoteDeviceStatusDescription;
    }

}