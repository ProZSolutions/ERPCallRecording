package in.proz.prozcallrecorder;


import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.proz.prozcallrecorder.ADapter.RecordingAdapter;
import in.proz.prozcallrecorder.InternalStorage.CallRecordingHelper;

public class RecordingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);

        ListView listView = findViewById(R.id.recordingList);
        List<String> recordings = getRecordings();
      //  ArrayList<String> recordingsnew = CallRecordingHelper.getCallRecordingPaths();


        String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IncomingCallRecord";
        RecordingAdapter adapter = new RecordingAdapter(this, recordings, directoryPath);
        listView.setAdapter(adapter);
    }

    private List<String> getRecordings() {
        File dir = new File(Environment.getExternalStorageDirectory(), "IncomingCallRecord");
        List<String> files = new ArrayList<>();
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    files.add(file.getName());
                }
            }
        }
        return files;
    }
}
