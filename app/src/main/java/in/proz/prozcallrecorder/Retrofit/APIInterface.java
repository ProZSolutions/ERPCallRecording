package in.proz.prozcallrecorder.Retrofit;

import java.util.List;

import in.proz.prozcallrecorder.Modal.CallMainModal;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {
    @Multipart
    @POST("calltracking-create")
    Call<CommonPojo> insertCallRecording(
            @Part("call_type") RequestBody call_type,
            @Part("mobile_no") RequestBody mobile_no,
            @Part("device_mobile_no") RequestBody device_mobile_no,
            @Part("call_start_time") RequestBody call_start_time,
            @Part("call_end_time") RequestBody call_end_time,
            @Part("missed_call") RequestBody  missedCallModals,
            @Part MultipartBody.Part call_recording );

    @Multipart
    @POST("calltracking-create")
    Call<CommonPojo> insertCallRecordingWithoiy(
            @Part("call_type") String call_type,
            @Part("mobile_no") String mobile_no,
            @Part("device_mobile_no") String device_mobile_no,
            @Part("call_start_time") String call_start_time,
            @Part("call_end_time") String call_end_time,
            @Part("missed_call[]") List<MissedCallModal> missedCallModals,
            @Part MultipartBody.Part call_recording );
    @POST("calltracking-list")
    Call<CallMainModal> getCallTrackList(@Query("call_type") String call_type
            , @Query("device_mobile_no") String device_mobile_no,
                                         @Query("page") int pageNo);
    @POST("login")
    Call<CommonPojo> getLogin(@Query("username") String username ,@Query("password") String password);
}
