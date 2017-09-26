package codes.by.vijaykumar.interviewtask;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.TimedText;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class VideoActivity extends AppCompatActivity implements OnTimedTextListener {

    private static final String TAG = VideoActivity.class.getSimpleName();


    private TextView txtDisplay, txtCurrentTime, txtTotalTime;
    private static Handler handler = new Handler();
    private VideoView videoView;
    private MediaPlayer player;
    private double startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = (VideoView) findViewById(R.id.videoView);
        txtDisplay = (TextView) findViewById(R.id.txtDisplay);
        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);

        String filePath = "android.resource://" + getPackageName() + "/" + R.raw.bootanimation_nexus;
        player = MediaPlayer.create(this, R.raw.bootanimation_nexus);
        playVideo(filePath);
        playSubtitle();

    }

    private void playSubtitle() {
        try {
            player.addTimedTextSource(getSubtitleFile(R.raw.subtitle), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            int textTrackIndex = findTrackIndexFor(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, player.getTrackInfo());
            if (textTrackIndex >= 0) {
                player.selectTrack(textTrackIndex);
            } else {
                Log.w(TAG, "Cannot find text track!");
            }
            player.setOnTimedTextListener(this);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void playVideo(String path) {
        try {

            videoView.setVideoURI(Uri.parse(path));
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    videoView.start();
                    playSubtitle();
                }
            });

            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
            // Log.e(TAG, "error: " + e.getMessage(), e);
        }


        double finalTime = player.getDuration();
        startTime = player.getCurrentPosition();

        txtTotalTime.setText(String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) finalTime)))
        );


        handler.postDelayed(UpdateSongTime, 100);

    }


    private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    private String getSubtitleFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File subtitleFile = getFileStreamPath(fileName);
        if (subtitleFile.exists()) {
            Log.d(TAG, "Subtitle already exists");
            return subtitleFile.getAbsolutePath();
        }

        // Copy the file from the res/raw folder to your app folder on the
        // device
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(resId);
            outputStream = new FileOutputStream(subtitleFile, false);
            copyFile(inputStream, outputStream);
            return subtitleFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, outputStream);
        }
        return "";
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    // A handy method I use to close all the streams
    private void closeStreams(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable stream : closeables) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onTimedText(final MediaPlayer mp, final TimedText text) {
        if (text != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    txtDisplay.setText(text.getText());


                }
            });
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = player.getCurrentPosition();
            txtCurrentTime.setText(String.format("%d : %d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );


            handler.postDelayed(this, 1000);


        }
    };
}
