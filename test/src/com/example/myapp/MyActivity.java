package com.example.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.*;

public class MyActivity extends Activity {
    private boolean isRecording = false ;
    private Object tmp = new Object() ;

    private AudioRecordFunc audioRecordFunc;


    private Button testButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        audioRecordFunc = AudioRecordFunc.getInstance();

        testButton= (Button)findViewById(R.id.test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }
        });
        Button start = (Button)findViewById(R.id.start_bt) ;
        start.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                Thread thread = new Thread(new Runnable() {
//                    public void run() {
//                        record();
//                    }
//                });
//                thread.start();
//                findViewById(R.id.start_bt).setEnabled(false) ;
//                findViewById(R.id.end_bt).setEnabled(true) ;

                audioRecordFunc.startRecordAndFile();
            }

        }) ;

        Button play = (Button)findViewById(R.id.play_bt) ;
        play.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                play();

            }

        }) ;

        Button stop = (Button)findViewById(R.id.end_bt) ;
//        stop.setEnabled(false) ;
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                isRecording = false ;
//                findViewById(R.id.start_bt).setEnabled(true) ;
//                findViewById(R.id.end_bt).setEnabled(false) ;
                audioRecordFunc.stopRecordAndFile();
            }

        }) ;

    }

    public void play() {
        // Get the file we want to playback.
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FinalAudio.wav");
        // Get the length of the audio stored in the file (16 bit so 2 bytes per short)
        // and create a short array to store the recorded audio.
        int musicLength = (int)(file.length()/2);
        short[] music = new short[musicLength];


        try {
            // Create a DataInputStream to read the audio data back from the saved file.
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

            // Read the file into the music array.
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }


            // Close the input streams.
            dis.close();


            // Create a new AudioTrack object using the same parameters as the AudioRecord
            // object used to create the file.
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    11025,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength*2,
                    AudioTrack.MODE_STREAM);
            // Start playback
            audioTrack.play();

            // Write the music buffer to the AudioTrack object
            audioTrack.write(music, 0, musicLength);

            audioTrack.stop() ;

        } catch (Throwable t) {
            Log.e("AudioTrack", "Playback Failed");
        }
    }

    public void record() {
        int frequency = 11025;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");

        // Delete any previous recording.
        if (file.exists())
            file.delete();


        // Create the new file.
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create " + file.toString());
        }

        try {
            // Create a DataOuputStream to write the audio data into the saved file.
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);

            // Create a new AudioRecord object to record the audio.
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    frequency, channelConfiguration,
                    audioEncoding, bufferSize);

            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();

            isRecording = true ;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++)
                    dos.writeShort(buffer[i]);
            }


            audioRecord.stop();
            dos.close();

        } catch (Throwable t) {
            Log.e("AudioRecord","Recording Failed");
        }
    }

    private void setDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.guide_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        Button cancelButton = (Button)view.findViewById(R.id.guide_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        Button registerButton = (Button)view.findViewById(R.id.guide_dialog_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyActivity.this,"注册按钮被点击了",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }
}
