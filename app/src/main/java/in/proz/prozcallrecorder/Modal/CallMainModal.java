package in.proz.prozcallrecorder.Modal;

import com.google.gson.annotations.SerializedName;

public class CallMainModal {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private InnerCallModa data;

    @SerializedName("count")
    private int count;

    public String getStatus() {
        return status;
    }

    public InnerCallModa getData() {
        return data;
    }

    public int getCount() {
        return count;
    }
}
