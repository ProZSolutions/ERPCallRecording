package in.proz.prozcallrecorder.MediaProjection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MediaProjectionRecorder {
    private static final String TAG = "MediaProjectionRecorder";
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 128000;

    private final Context context;
    private final MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private File outputFile;

    public MediaProjectionRecorder(Context context) {
        this.context = context;
        this.mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public Intent getProjectionIntent() {
        return mediaProjectionManager.createScreenCaptureIntent();
    }

    public void startRecording(int resultCode, Intent data, File file) throws IOException {
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        outputFile = file;
        setupMediaRecorder();
        mediaRecorder.prepare();
        mediaRecorder.start();
        createVirtualDisplay();
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(BIT_RATE);
        mediaRecorder.setAudioSamplingRate(SAMPLE_RATE);
        mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay(
                "MediaProjectionRecording",
                1280, 720, 1,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                null, null, null
        );
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
    }
}
