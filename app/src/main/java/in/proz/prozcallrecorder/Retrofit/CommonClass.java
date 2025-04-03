package in.proz.prozcallrecorder.Retrofit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonClass {



    public void putSharedPref(Context context, String tag, String value){
        SharedPreferences sp=context.getSharedPreferences(tag,0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(tag,value);
        editor.apply();
        editor.commit();
        Log.d("getEmployeeList"," putted ");
    }

    public String callFormat(String inpit){
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate=" ";
        try {
            // Parse the input date
            Date date = inputFormat.parse(inpit);
            // Format the date to the new pattern
            formattedDate = outputFormat.format(date);

            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    public String getFileName(Context context, Uri uri) {
        String fileName = "Unknown.pdf";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    fileName = cursor.getString(index);
                }
            }
        }
        return fileName;
    }
    public byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
    public String getFileTypeFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        // If the URI scheme is content
        if ("content".equals(uri.getScheme())) {
            // Use ContentResolver to get the MIME type
            return contentResolver.getType(uri);
        }

        // If the URI scheme is file
        if ("file".equals(uri.getScheme())) {
            // Extract the file extension from the URI path and get the MIME type
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        // For other URI schemes, return null or handle as needed
        return null;
    }
    public String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver contentResolver = context.getContentResolver();
            try {
                // Query the content resolver to get the filename
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }
    public String getSharedPref(Context context,String tag){
        SharedPreferences sp=context.getSharedPreferences(tag,0);
        return  sp.getString(tag,null);
    }
    public   boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    public  String formatDuration(String duration) {
        try {
            String[] parts = duration.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            StringBuilder result = new StringBuilder();
            if (hours > 0) result.append(hours).append(" hr ");
            if (minutes > 0) result.append(minutes).append(" min ");
            if (seconds > 0) result.append(seconds).append(" sec");

            return result.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return duration;
        }
    }
    public String commonURL(){
        return  "https://erpbackendtesting.proz.in/api/all/";
    }
    public String LoginURL(){
        return  "https://erpbackendtesting.proz.in/api/";
    }
    public String bannerPath(){ return "https://mambathlete.com/upload/images/banner/"; }
    public String pdfPath(){ return "https://mambathlete.com/upload/pdf/"; }
    public String homeCategoryPath(){ return "https://mambathlete.com/upload/images/banner/"; }
    public String popularPath(){ return "https://mambathlete.com/upload/images/product/"; }
    public String imagePath(){
        return "https://mambathlete.com/upload/images/product/";
    }


}
