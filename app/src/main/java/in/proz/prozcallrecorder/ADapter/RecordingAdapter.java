package in.proz.prozcallrecorder.ADapter;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.util.List;

import in.proz.prozcallrecorder.R;

public class RecordingAdapter extends BaseAdapter {
    private Context context;
    private List<String> recordings;
    private String directoryPath;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;

    public RecordingAdapter(Context context, List<String> recordings, String directoryPath) {
        this.context = context;
        this.recordings = recordings;
        this.directoryPath = directoryPath;
        File dir = new File(Environment.getExternalStorageDirectory(), "IncomingCallRecord");
        directoryPath = dir.getAbsolutePath();
    }

    @Override
    public int getCount() {
        return recordings.size();
    }

    @Override
    public Object getItem(int position) {
        return recordings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.recording_item, parent, false);
        }

        TextView tvFileName = convertView.findViewById(R.id.tvFileName);
        TextView btnPlayPause = convertView.findViewById(R.id.btnPlayPause);
        TextView btnRewind = convertView.findViewById(R.id.btnRewind);
        TextView btnForward = convertView.findViewById(R.id.btnForward);
        TextView btnShare = convertView.findViewById(R.id.btnShare);
        LinearLayout layout = convertView.findViewById(R.id.layout);

        tvFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout.getVisibility()==View.VISIBLE){
                    layout.setVisibility(View.GONE);
                }else{
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });


        String fileName = recordings.get(position);

        String filePath = directoryPath + "/" + fileName;

        tvFileName.setText(fileName);

        btnPlayPause.setOnClickListener(v -> playAudio(filePath, position, btnPlayPause));
        btnRewind.setOnClickListener(v -> rewindAudio());
        btnForward.setOnClickListener(v -> forwardAudio());
        btnShare.setOnClickListener(v -> shareAudio(filePath));

        return convertView;
    }


    private void playAudio(String filePath, int position, Button btnPlayPause) {
        // Check if file exists
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        stopCurrentAudio(); // Stop and reset any existing MediaPlayer instance

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playingPosition = position;
                btnPlayPause.setText("⏸ Pause");
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayPause.setText("▶ Play");
                playingPosition = -1;
            });

            mediaPlayer.prepareAsync(); // Use prepareAsync() instead of prepare()

        } catch (IOException e) {
            Toast.makeText(context, "Error playing file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void playAudio(String filePath, int position, TextView btnPlayPause) {
        stopCurrentAudio();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playingPosition = position;
            btnPlayPause.setText("⏸ Pause");

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayPause.setText("▶ Play");
                playingPosition = -1;
            });

        } catch (IOException e) {
            Toast.makeText(context, "Error playing file", Toast.LENGTH_SHORT).show();
        }
    }

    private void rewindAudio() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(Math.max(currentPosition - 10000, 0));
        }
    }

    private void forwardAudio() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            mediaPlayer.seekTo(Math.min(currentPosition + 10000, duration));
        }
    }

    private void stopCurrentAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            playingPosition = -1;
        }
    }

    private void shareAudio(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(context, "in.proz.prozcallrecorder.provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setPackage("com.whatsapp"); // Only WhatsApp
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(shareIntent);
        } catch (Exception e) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }
}
