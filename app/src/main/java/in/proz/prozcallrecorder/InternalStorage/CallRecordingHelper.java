package in.proz.prozcallrecorder.InternalStorage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.util.Arrays;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

public class CallRecordingHelper {

    public static String getCallRecordingPath(Context context, String incomingNumber) {
        String normalizedIncoming = normalizePhoneNumber(incomingNumber);
        String contactName = getContactNameFromNumber(context, normalizedIncoming);
        Log.d("RKListing", "Looking for: " + normalizedIncoming + " or contact: " + contactName);

        String[] possiblePaths = {
                "/MIUI/sound_recorder/call_rec",   // Xiaomi
                "/Recordings/Call",                // Samsung
                "/Sounds/CallRecordings",          // OnePlus
                "/CallRecordings",                 // Oppo/Vivo
                "/sdcard/Call"                     // Generic
        };

        File latestFile = null;
        long latestModified = 0;

        for (String path : possiblePaths) {
            File recordingDir = new File(Environment.getExternalStorageDirectory(), path);
            Log.d("RKListing", "Checking directory: " + recordingDir.getAbsolutePath());

            if (!recordingDir.exists() || !recordingDir.isDirectory()) continue;

            File[] files = recordingDir.listFiles();
            if (files == null || files.length == 0) continue;

            // Sort files by lastModified descending (latest first)
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            for (File file : files) {
                if (file == null || !file.isFile()) continue;

                String fileName = file.getName();
                if (!fileName.endsWith(".m4a") && !fileName.endsWith(".mp3") && !fileName.endsWith(".wav"))
                    continue;

                Log.d("RKListing", "File: " + fileName);

                String baseName = fileName.replace("Call recording ", "").replaceAll("\\.(m4a|mp3|wav)", "");
                String assumedName = baseName.split("_")[0].trim();
                Log.d("RKListing", "Assumed name: " + assumedName);

                String assumedNormalized = normalizePhoneNumber(assumedName);

                boolean match = false;
                if (normalizedIncoming.equals(assumedNormalized)) {
                    match = true;
                    Log.d("RKListing", "Matched number: " + assumedNormalized);
                } else if (contactName != null && contactName.equalsIgnoreCase(assumedName)) {
                    match = true;
                    Log.d("RKListing", "Matched contact name: " + contactName);
                }

                if (match && file.lastModified() > latestModified) {
                    latestModified = file.lastModified();
                    latestFile = file;
                    Log.d("RKListing", "New latest matching file: " + file.getAbsolutePath());
                    break; // Optionally break if only the newest is needed
                }
            }
        }

        if (latestFile != null) {
            return latestFile.getAbsolutePath();
        }

        Log.d("RKListing", "No matching recording found.");
        return null;
    }

    private static String getContactNameFromNumber(Context context, String number) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = cr.query(uri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                if (normalizePhoneNumber(contactNumber).equals(number)) {
                    cursor.close();
                    return contactName;
                }
            }
            cursor.close();
        }
        return null;
    }

    private static String normalizePhoneNumber(String number) {
        if (number == null) return "";
        return number.replaceAll("[^0-9]", "").replaceFirst("^91", "").replaceFirst("^0", "");
    }
}
