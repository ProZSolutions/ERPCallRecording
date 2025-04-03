package in.proz.prozcallrecorder;


import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServiceClassNew extends Service {
    private static final String TAG = "PROZCall";
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder;
    private boolean isRecording = false;
    private String filePath;

    public void startRecording() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CallRecordProz");
        if (!dir.exists()) dir.mkdirs();

        filePath = dir.getAbsolutePath() + "/Call_" + System.currentTimeMillis() + ".pcm";

        recorder.startRecording();
        isRecording = true;

        new Thread(() -> {
            writeAudioToFile(filePath, bufferSize);
        }).start();

        Log.d(TAG, "Recording started...");
    }

    private void writeAudioToFile(String filePath, int bufferSize) {
        byte[] data = new byte[bufferSize];
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
            while (isRecording) {
                int read = recorder.read(data, 0, bufferSize);
                if (read > 0) {
                    fos.write(data, 0, read);
                }
            }
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing audio file: " + e.getMessage());
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.d(TAG, "Recording stopped. File saved at: " + filePath);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

