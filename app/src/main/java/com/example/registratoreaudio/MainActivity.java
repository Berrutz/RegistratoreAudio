package com.example.registratoreaudio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import Async.Recorder;
import interfaces.IRecordingDone;

public class MainActivity extends AppCompatActivity implements IRecordingDone {

    private  final String TAG="MainActivity";
    private Button bttRec=null;
    private TextView tvRecStatus=null;

    private  int RecordingLenght = 5; // Lunghezza registrazione 5 secondi
    private int Fs =  8000; // in Hz

    private final String PATH = "AMIF_Recording";
    private final String FILE_NAME = "MyRecording.wave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttRec=findViewById(R.id.bttRec);
        tvRecStatus=findViewById(R.id.tvRecStatus);

        Recorder recorder = new Recorder(this,this,RecordingLenght,Fs);

        bttRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    tvRecStatus.setText("Recording in Progress...");
                    Log.i(TAG,"Start Recording");
                    recorder.go(PATH,FILE_NAME);
            }
        });
    }

    @Override
    public void onRecordingDone(String message, short[] audioData){
        Log.i(TAG,"onRecordingDone");
        tvRecStatus.setText(message);

        String _StoreDir = Environment.getExternalStorageDirectory() + "/" + PATH; // percorso path
        String _fulldir = _StoreDir + "/" + FILE_NAME;
        Log.i(TAG, "dIR:" + _fulldir);
        Uri myUri = Uri.parse(_fulldir);; // initialize Uri here
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(getApplicationContext(),myUri);  // WORKS ma solo rumore continuo
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }
}