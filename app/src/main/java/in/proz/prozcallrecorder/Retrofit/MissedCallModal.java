package in.proz.prozcallrecorder.Retrofit;

public class MissedCallModal {
    String m_mobile_no,m_datetime;

    public MissedCallModal(String m_mobile_no, String m_datetime) {
        this.m_mobile_no = m_mobile_no;
        this.m_datetime = m_datetime;
    }

    public String getM_mobile_no() {
        return m_mobile_no;
    }

    public String getM_datetime() {
        return m_datetime;
    }
}
