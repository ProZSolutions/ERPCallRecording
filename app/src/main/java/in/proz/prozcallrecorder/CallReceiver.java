package in.proz.prozcallrecorder;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.prozcallrecorder.Retrofit.MissedCallModal;

public class CallReceiver extends BroadcastReceiver {
    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static String incomingNumber;
    String Tag="MobileCallRecording";
    String Page ="CallRecordingService";
    private static List<MissedCallModal> missedCalls = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
       // String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            Log.d(Tag,Page+" incoming   "+intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        }else{
            Log.d(Tag,Page+" incoming number not present");
        }
        /*if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Intent serviceIntent = new Intent(context, CallRecorderService.class);
            serviceIntent.putExtra("PHONE_NUMBER", incomingNumber);
            context.startService(serviceIntent);
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            context.stopService(new Intent(context, CallRecorderService.class));
        }*/
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Intent serviceIntent = new Intent(context,
                    CallRecorderService.class);
            serviceIntent.putExtra("call_type", "incoming");
            serviceIntent.putExtra("incoming_number", incomingNumber);
            context.startService(serviceIntent);
            Log.d(Tag,Page+" incoming call");

        }

        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && !lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Intent serviceIntent = new Intent(context, CallRecorderService.class);
            serviceIntent.putExtra("call_type", "outgoing");
            serviceIntent.putExtra("incoming_number", incomingNumber);
            context.startService(serviceIntent);
            Log.d(Tag,Page+" outgoing call");

        }

        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING) && incomingNumber != null) {
                if(!TextUtils.isEmpty(incomingNumber)){
                    missedCalls.add(new MissedCallModal(incomingNumber,simpleDateFormat.format(new Date())));
                }
                Log.d(Tag,Page+" missed call no "+incomingNumber);
                Toast.makeText(context, "Missed Call: " + incomingNumber, Toast.LENGTH_LONG).show();
            }
            context.stopService(new Intent(context, CallRecorderService.class));
        }else{
            Log.d(Tag,Page+" missed call not present");
        }

        lastState = state;
    }
    public static List<MissedCallModal> getMissedCalls() {
        return missedCalls;
    }
}

