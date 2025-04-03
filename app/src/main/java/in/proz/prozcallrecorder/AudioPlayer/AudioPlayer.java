package in.proz.prozcallrecorder.AudioPlayer;


import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.proz.prozcallrecorder.R;


public class AudioPlayer extends DialogFragment {

    private MediaPlayer mediaPlayer;
    private ImageView btnPlayPause, btnForward, btnRewind;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime;
    private Handler handler = new Handler();
    private Runnable updater;

    private static final String ARG_AUDIO_URL = "audio_url";
    private String audioUrl;

    private boolean isPlaying = false;

    public static AudioPlayer newInstance(String audioUrl) {
        AudioPlayer fragment = new AudioPlayer();
        Bundle args = new Bundle();
        args.putString(ARG_AUDIO_URL, audioUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_player, container, false);
        setCancelable(true);

        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnForward = view.findViewById(R.id.btnForward);
        btnRewind = view.findViewById(R.id.btnRewind);
        seekBar = view.findViewById(R.id.seekBar);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);

        audioUrl = getArguments().getString(ARG_AUDIO_URL);

        mediaPlayer = new MediaPlayer();
        prepareMediaPlayer();

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                pauseAudio();
            } else {
                playAudio();
            }
        });

        btnForward.setOnClickListener(v -> forwardAudio());
        btnRewind.setOnClickListener(v -> rewindAudio());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(convertMillisToTime(mediaPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        releaseMediaPlayer(); // Release on dismiss
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaPlayer(); // Ensure release on view destroy
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updater); // Stop seekbar update
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // Set dialog width to match parent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                tvTotalTime.setText("Duration :"+convertMillisToTime(mediaPlayer.getDuration()));
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayPause.setImageResource(R.drawable.play);
                isPlaying = false;
            });

        } catch (IOException e) {
            Log.e("AudioPlayerDialog", "Error preparing MediaPlayer", e);
        }
    }

    private void playAudio() {
        mediaPlayer.start();
        btnPlayPause.setImageResource(R.drawable.pause);
        isPlaying = true;
        updateSeekBar();
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        btnPlayPause.setImageResource(R.drawable.play);
        isPlaying = false;
        handler.removeCallbacks(updater);
    }

    private void forwardAudio() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        mediaPlayer.seekTo(Math.min(currentPosition + 10000, duration));
    }

    private void rewindAudio() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(Math.max(currentPosition - 10000, 0));
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        tvCurrentTime.setText(convertMillisToTime(mediaPlayer.getCurrentPosition()));
        if (mediaPlayer.isPlaying()) {
            updater = this::updateSeekBar;
            handler.postDelayed(updater, 1000);
        }
    }

    private String convertMillisToTime(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
