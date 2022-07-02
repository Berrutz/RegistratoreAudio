package Async;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

import Wave.WavIO;
import interfaces.IRecordingDone;

public class Recorder {

    private final String TAG = "Recorder";
    private int RecordingLenght;
    private int Fs;   // freq campionamento in Hz
    private int nSamples;
    private short[] AudioData; // valori da -2^32 a 2^32 | campioni audio PCM a 16 bit (2 BYTES,short)

    private AudioRecord audioRecord = null;
    private Activity activity = null;
    private IRecordingDone iRecordingDone =  null;

    public Recorder(Activity activity,IRecordingDone iRecordingDone,int recordingLenght, int fs) {
        this.RecordingLenght = recordingLenght;
        this.Fs = fs;
        this.nSamples = this.RecordingLenght * this.Fs;
        this.AudioData = new short[this.nSamples];
        this.activity=activity;
        this.iRecordingDone=iRecordingDone;


        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, this.Fs, AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT, 2 * this.nSamples);
    }

    public  void go(String path, String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // do in background
                DoRecording(path,fileName);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // On post Execute
                        try {
                            iRecordingDone.onRecordingDone("Stop",AudioData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void DoRecording(String path,String fileName){
        String _StoreDir = Environment.getExternalStorageDirectory() + "/" + path; // percorso path
        File f = new File(_StoreDir);
        if(!f.exists())
            if(!f.mkdir())
                Log.i(TAG,"Creazione cartella "+ path+" non riuscita!");

            audioRecord.startRecording();
            audioRecord.read(AudioData,0,this.nSamples);

            String _fulldir = _StoreDir + "/" + fileName;

            byte[] databyte = ConvertArray2ByteArray(AudioData);

            Log.i(TAG, "dIR:" + _fulldir);
            WavIO wavIO = new WavIO(_fulldir,16,1,1,
                    Fs,2,16,databyte);
            wavIO.save();

    }

    private byte[] ConvertArray2ByteArray(short[] array){
        byte[] databyte= new byte[2*this.nSamples];

        for (int i=0;i<array.length;i++){
            //databyte[2*i]= (byte)(array[i] & 0x00ff);
            //databyte[2*i +1]= (byte)((array[i] >> 8) & 0xff00);
            databyte[2*i] = (byte)(array[i] & 0xff);
            databyte[2*i +1 ] = (byte)((array[i] >> 8) & 0xff);  // Works converte in modo adatto!!
        }
        return databyte;
    }
}
