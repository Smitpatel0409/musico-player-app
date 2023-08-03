package com.smytappstudio.musicoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

//import com.example.musicoplayer.R;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    String textcontent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    Files file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_play_song );
        textView = findViewById( R.id.textView );
        play = findViewById( R.id.play );
        previous = findViewById( R.id.previous);
        next = findViewById( R.id.next );
        seekBar = findViewById( R.id.seekBar );

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList( "songList" );
        textContent = intent.getStringExtra("currentSong");
        textView.setText( textContent );
        textView.setSelected( true );
        position = intent.getIntExtra( "position",0 );
        Uri uri = Uri.parse(songs.get( position ).toString());
        mediaPlayer = MediaPlayer.create( this, uri );
        mediaPlayer.start();
        seekBar.setMax( mediaPlayer.getDuration());


        seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mediaPlayer.seekTo( seekBar.getProgress() );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo( seekBar.getProgress() );
            }
        } );
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition< mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress( currentPosition );
                        sleep( 800 );

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource( R.drawable.play );
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource( R.drawable.pause );
                    mediaPlayer.start();
                }
            }
        } );

        previous.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                mediaPlayer.release();
                if (position!=0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get( position ).toString());
                mediaPlayer = MediaPlayer.create( getApplicationContext(), uri );
                mediaPlayer.start();
                play.setImageResource( R.drawable.pause );
                seekBar.setMax( mediaPlayer.getDuration());
//                seekBar.setProgress( mediaPlayer.getCurrentPosition() );
                textContent = songs.get(position).getName().toString();
                textView.setText( textContent );
            }
        } );

        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                mediaPlayer.release();
                if (position!=songs.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get( position ).toString());
                mediaPlayer = MediaPlayer.create( getApplicationContext(), uri );
                mediaPlayer.start();
                play.setImageResource( R.drawable.pause );
                seekBar.setMax( mediaPlayer.getDuration());
//                seekBar.setProgress( mediaPlayer.getCurrentPosition() );
                textContent = songs.get( position ).getName().toString();
                textView.setText( textContent );
            }
        } );
    }
}