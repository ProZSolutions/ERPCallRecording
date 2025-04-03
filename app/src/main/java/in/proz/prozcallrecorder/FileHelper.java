package in.proz.prozcallrecorder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileHelper {
    public static File getRecordingDirectory(Context context) {
        File musicDir;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11+ (Scoped Storage)
            musicDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PROZCall");
            Log.d("PROZCall"," call1 "+musicDir);
        } else {
            // Older Android Versions
            musicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "PROZCall");
            Log.d("PROZCall"," call2 "+musicDir);

        }

        // Create directory if it does not exist
        if (!musicDir.exists()) {
            boolean success = musicDir.mkdirs();
            if (!success) {
                Log.d("PROZCall"," failed to create dir ");

                System.out.println("Failed to create directory: " + musicDir.getAbsolutePath());
            }
        }

        return musicDir;
    }

    public static File getRecordingFile(Context context, String fileName) {
        Log.d("PROZCall"," getRectord  "+fileName);

        File dir = getRecordingDirectory(context);
        return new File(dir, fileName);
    }
}
