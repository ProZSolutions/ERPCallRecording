package in.proz.prozcallrecorder.Modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.proz.prozcallrecorder.Retrofit.MissedCallModal;


public class CallListModal {
    @SerializedName("call_type")
    private String callType;

    @SerializedName("mobile_no")
    private String mobileNo;

    @SerializedName("device_mobile_no")
    private String deviceMobileNo;

    @SerializedName("call_start_time")
    private String callStartTime;

    @SerializedName("call_end_time")
    private String callEndTime;

    @SerializedName("duration")
    private String duration;

    @SerializedName("attachment")
    private String attachment;

    @SerializedName("missed_call")
    private List<MissedCallModal> missedCalls;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("id")
    private int id;

    @SerializedName("created_by")
    private String createdBy;

    public String getCallType() {
        return callType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getDeviceMobileNo() {
        return deviceMobileNo;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getAttachment() {
        return attachment;
    }

    public List<MissedCallModal> getMissedCalls() {
        return missedCalls;
    }

    public String getUuid() {
        return uuid;
    }

    public int getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
