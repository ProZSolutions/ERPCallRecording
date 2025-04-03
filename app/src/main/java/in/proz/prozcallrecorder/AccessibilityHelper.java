package in.proz.prozcallrecorder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class AccessibilityHelper {
    private static final String TAG = "PROZCall";

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        String serviceId = context.getPackageName() + "/" + accessibilityService.getName();
        String enabledServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        boolean accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0) == 1;

        if (enabledServices != null) {
            for (String service : enabledServices.split(":")) {
                if (service.equalsIgnoreCase(serviceId)) {
                    Log.d(TAG, "Accessibility Service is ENABLED.");
                    return true;
                }
            }
        }

        Log.d(TAG, "Accessibility Service is NOT enabled.");
        return accessibilityEnabled;
    }
}
