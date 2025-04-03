package in.proz.prozcallrecorder;


import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class CallAccessibilityService extends AccessibilityService {

    private static final String TAG = "PROZCall";
    private ServiceClassNew callRecorderService = new ServiceClassNew();
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            String eventText = event.getText().toString();

            if (eventText.contains("incoming") || eventText.contains("outgoing")) {
                callRecorderService.startRecording();
                Log.d(TAG, "Call detected! Starting recording...");
            } else if (eventText.contains("call ended")) {
                callRecorderService.stopRecording();
                Log.d(TAG, "Call ended! Stopping recording...");
            }
        }
    }

    @Override

    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted.");

    }
}
