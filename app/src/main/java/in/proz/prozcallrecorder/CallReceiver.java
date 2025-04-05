package in.proz.prozcallrecorder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.prozcallrecorder.Retrofit.CommonClass;
import in.proz.prozcallrecorder.Retrofit.MissedCallModal;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.prozcallrecorder.Retrofit.MissedCallModal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.prozcallrecorder.Retrofit.MissedCallModal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.prozcallrecorder.Retrofit.MissedCallModal;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";
    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static String savedNumber = null;
    private static boolean isIncomingCall = false;
    private static boolean callStarted = false;

    private static final List<MissedCallModal> missedCalls = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            savedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            isIncomingCall = false;
            callStarted = false;
            Log.d(TAG, "Outgoing number detected: " + savedNumber);
            return;
        }

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (state == null) return;

        Log.d(TAG, "Call State: " + state + ", Number: " + incomingNumber);

        if (!TextUtils.isEmpty(incomingNumber)) {
            savedNumber = incomingNumber;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            isIncomingCall = true;
            callStarted = false;
            Log.d(TAG, "Phone is ringing: " + savedNumber);

        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            if (!callStarted) {
                if (isIncomingCall) {
                    Log.d(TAG, "Incoming call answered: " + savedNumber);
                    startRecordingService(context, "incoming", savedNumber);
                } else {
                    Log.d(TAG, "Outgoing call started: " + savedNumber);
                    startRecordingService(context, "outgoing", savedNumber);
                }
                callStarted = true;
            }

        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING) && isIncomingCall && !callStarted) {
                // Missed call
                Log.d(TAG, "Missed call from: " + savedNumber);
                missedCalls.add(new MissedCallModal(savedNumber, dateFormat.format(new Date())));
                Toast.makeText(context, "Missed Call: " + savedNumber, Toast.LENGTH_SHORT).show();
            }

            // Stop the service
            context.stopService(new Intent(context, CallRecorderService.class));
            savedNumber = null;
            isIncomingCall = false;
            callStarted = false;
        }

        lastState = state;
    }

    private void startRecordingService(Context context, String callType, String number) {
        if (TextUtils.isEmpty(number)) {
            Log.e(TAG, "startRecordingService: number is null or empty.");
            return;
        }

        Intent serviceIntent = new Intent(context, CallRecorderService.class);
        serviceIntent.putExtra("call_type", callType);
        serviceIntent.putExtra("incoming_number", number);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    public static List<MissedCallModal> getMissedCalls() {
        return missedCalls;
    }
}
