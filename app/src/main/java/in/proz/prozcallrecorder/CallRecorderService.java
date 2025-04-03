package in.proz.prozcallrecorder;




import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.proz.prozcallrecorder.Retrofit.APIInterface;
import in.proz.prozcallrecorder.Retrofit.ApiClient;
import in.proz.prozcallrecorder.Retrofit.CommonClass;
import in.proz.prozcallrecorder.Retrofit.CommonPojo;
import in.proz.prozcallrecorder.Retrofit.MissedCallModal;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRecorderService extends Service {
    private MediaRecorder recorder;
    private boolean isRecording = false;
     private File audioFile;
    private String phoneNumber;


    private String callType;
    private String incomingNumber;
    CommonClass commonClass =new CommonClass();

    private String deviceNumber;
    private long callStartTime;
    String Tag="MobileCallRecording";
    String Page ="CallRecordingService";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    String start_call_time , end_call_time;
    private static final String CHANNEL_ID = "CallRecordingServiceChannel";




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.e("CallRecordingService", "Intent is null");
            stopSelf(); // Stop service to avoid unexpected behavior
            return START_NOT_STICKY;
        }



        phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        Log.d(Tag," phone "+phoneNumber+" on Start Comade");
        callType = intent.getStringExtra("call_type");
        incomingNumber = intent.getStringExtra("incoming_number");
        deviceNumber = commonClass.getSharedPref(getApplicationContext(),"device_no"); // Fetch device number dynamically
        callStartTime = System.currentTimeMillis();



        Log.d(Tag,Page+" onStartCommand "+callType+" incommin "+incomingNumber+" device number "+
                deviceNumber+" call start "+callStartTime);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = createNotification("Recording Call....");
            startForeground(1, notification);
        }

        if (callType != null && !callType.isEmpty() && incomingNumber != null && !incomingNumber.isEmpty()) {
            Log.d(Tag, Page + " Starting recording...");
            startRecording();
        } else {
            Log.e("CallRecordingService", "Call is not active, skipping recording.");
            stopSelf(); // Stop the service if no active call
        }
        return START_STICKY;
    }
    private void createNotificationChannel() {
        Log.d(Tag,Page+" create notification  ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(Tag,Page+" create notification if  ");

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Call Recording Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                Log.d(Tag,Page+" create not manager!null  ");
                manager.createNotificationChannel(serviceChannel);
            }else{
                Log.d(Tag,Page+" manager null ");

            }
        }
    }
    private Notification createNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "CallRecording",
                    "Call Recording Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CallRecording")
                .setContentTitle("Call Recorder")
                .setContentText(message)
                .setSmallIcon(R.drawable.make_call) // Your app icon
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }
    private Notification getNotification() {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    "call_recorder",
                    "Call Recorder",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "call_recorder")
                .setContentTitle("Recording Call")
                .setContentText("Call recording in progress")
                .setSmallIcon(R.drawable.outgoing_call)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }
        private void startRecording() {
            try {
                start_call_time = simpleDateFormat.format(new Date());
                Log.d(Tag," come to try block ");
                String fileName = "temp_audio_recording_" + System.currentTimeMillis() + ".mp3";
                audioFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName); // App-specific storage

                Log.d(Tag," file "+audioFile);
                recorder = new MediaRecorder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12 and above - Limited call recording support
                    Log.d(Tag,"Android 12 and Above ");
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Use MIC as a fallback
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d(Tag,"Middle of Android ");
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                } else {
                    Log.d(Tag,"Andorid below 10 ");
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                }


               // recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setOutputFile(audioFile.getAbsolutePath());
                recorder.setAudioEncodingBitRate(128000);
                recorder.setAudioSamplingRate(44100);
                Log.d(Tag," call record start ");
                recorder.prepare();
                recorder.start();
                isRecording = true;
            } catch (Exception e) {
                Log.d(Tag," error "+e.getMessage());
                e.printStackTrace();
            }
    }

    private void stopRecording() {
        end_call_time = simpleDateFormat.format(new Date());
        Log.d(Tag," on stop ");

        try {
            if (isRecording && recorder != null) {
                Log.d(Tag, "is stop record");
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;

                uploadRecording(audioFile);
            }
        } catch (IllegalStateException e) {
            Log.e(Tag, "Error stopping recording: " + e.getMessage());
        } finally {
            if (recorder != null) {
                recorder.release();
                recorder = null;
                isRecording = false;
            }
        }
    }
    private void uploadRecording(File audioFile) {
        List<MissedCallModal> missedCalls = CallReceiver.getMissedCalls();
        Log.d(Tag,Page+" uploading call record  ");
        Log.d(Tag,Page+" cDevice "+deviceNumber+" in "+incomingNumber+
                " call "+callType+" missed "+missedCalls+
                " reci path "+audioFile+" start time "+start_call_time+" end time "+end_call_time);

       // File path = new File(recordingPath);
        Log.d(Tag,Page+" file path "+audioFile+" is exist "+audioFile.exists());
        if(!TextUtils.isEmpty(deviceNumber) && !TextUtils.isEmpty(incomingNumber) &&
                !TextUtils.isEmpty(callType)  ){
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), audioFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("attachment", audioFile .getName(), fileBody);
            Log.d(Tag,Page+" file path "+filePart);

            if(filePart==null){
                Log.d(Tag,Page+" File path null ");

            }else{

                incomingNumber = incomingNumber.replace("+91","");
                incomingNumber = incomingNumber.replace("91","");
                deviceNumber = deviceNumber.replace("91","");
                deviceNumber = deviceNumber.replace("91","");
                RequestBody callTypeB = RequestBody.create(MediaType.parse("text/plain"), callType);
                RequestBody mobileNo = RequestBody.create(MediaType.parse("text/plain"), incomingNumber);
                RequestBody deviceMobileNo = RequestBody.create(MediaType.parse("text/plain"), deviceNumber);
                RequestBody callStartTime = RequestBody.create(MediaType.parse("text/plain"), start_call_time);
                RequestBody callEndTime = RequestBody.create(MediaType.parse("text/plain"), end_call_time);
                RequestBody missedCall = missedCalls != null && !missedCalls.isEmpty() ?
                        RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(missedCalls)) : null;

                APIInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(),"token")).create(APIInterface.class);
                Call<CommonPojo> call = apiInterface.insertCallRecording
                        (callTypeB,
                                mobileNo,
                                deviceMobileNo,
                                callStartTime,
                                callEndTime
                                ,missedCall,filePart);


                Log.d(Tag,Page+" File url "+call.request().url());

                call.enqueue(new Callback<CommonPojo>() {
                    @Override
                    public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                        Log.d(Tag,Page+" code "+response.code());

                        if(response.isSuccessful()){
                            if(response.code()==200){
                                Log.d(Tag,Page+" success "+response.body().getStatus());
                                if(response.body().getStatus().equals("success")){
                                    Log.d(Tag,Page+" File inserted success");
                                    if (audioFile.exists()) {
                                        if (audioFile.delete()) {
                                            Log.d(Tag, "File deleted successfully");
                                        } else {
                                            Log.e(Tag, "Failed to delete file");
                                        }
                                    }
                                    openMainActivity("Call Recording Saved Successfully");
                                }else{
                                    openMainActivity("Failed to save Call Recording");
                                    Log.d(Tag,Page+" File insert failed ");
                                }
                            }else{
                                openMainActivity("Failed to save Call Recording");
                                Log.d(Tag,Page+" failed !200 ");
                            }
                        }else{
                            if (response.code() == 401) {
                                // Print error details for 401
                                try {
                                    String errorBody = response.errorBody().string();
                                    openMainActivity(errorBody);
                                    Log.e(Tag, Page + " 401 Unauthorized Error: " + errorBody);
                                } catch (IOException e) {
                                    Log.e(Tag, Page + " Error reading error body: " + e.getMessage());
                                }
                            } else {
                                Log.e(Tag, Page + " Error Response: " + response.code());
                                try {
                                    String errorBody = response.errorBody().string();
                                    openMainActivity(errorBody);

                                    Log.e(Tag, Page + " Error Details: " + errorBody);
                                } catch (IOException e) {
                                    Log.e(Tag, Page + " Error reading error body: " + e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonPojo> call, Throwable t) {
                        Log.d(Tag,Page+"  upload error "+t.getMessage());

                    }
                });
            }

        }else{
            Log.d(Tag,Page+" device / incom /outgo/reco path empty  ");

        }







        // Call your API here with deviceNumber, incomingNumber, callStartTime, recordingPath, callType, and missedCalls
    }


/*
    private void uploadRecording(File audioFile) {
        if (audioFile == null || !audioFile.exists()) {
            Log.e(Tag, "File does not exist for upload!");
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mp3"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);


        ProgressBar progressBar = new ProgressBar(CallRecorderService.this);
        if(body!=null){
            progressBar.setVisibility(View.VISIBLE);
            APIInterface apiInterface= ApiClient.getApiClient().create(APIInterface.class);
            Call<CommonPojo> call =apiInterface.uploadMP3File(body);
            call.enqueue(new Callback<CommonPojo>() {
                @Override
                public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                    progressBar.setVisibility(View.GONE);

                    if(response.isSuccessful()){
                        if(response.code()==200){
                            if(response.body().getStatus().equals("success")){
                                Toast.makeText(getApplicationContext(),"MP3 uploaded successfully",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Failed to upload MP3",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"File Upload Error",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"File Upload Error",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CommonPojo> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),"File Upload Error",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
*/

    @Override
    public void onDestroy() {
        Log.d(Tag," on destroy");
        stopRecording();
        super.onDestroy();
    }
    private void openMainActivity(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message",message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
