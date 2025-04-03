package in.proz.prozcallrecorder.Modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InnerCallModa {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<CallListModal> callDetailsList;

    public int getCurrentPage() {
        return currentPage;
    }

    public List<CallListModal> getCallDetailsList() {
        return callDetailsList;
    }
}
