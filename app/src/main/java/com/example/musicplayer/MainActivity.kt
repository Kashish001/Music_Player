@file:Suppress("DEPRECATION")

package com.example.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mtechviral.mplaylib.MusicFinder
import com.mtechviral.mplaylib.MusicFinder.Song
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast


class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var totalTime: Int = 0;
    lateinit var runnable: Runnable;
    private var handler = Handler();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),0);
        } else {
            createPlayer();
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            createPlayer();
        } else {
            longToast("Permission Denied");
            finish()
        }
    }

   private fun createPlayer() {
       val songFinder = MusicFinder(contentResolver)
       songFinder.prepare()
       songFinder.allSongs
       val songs: List<Song> = songFinder.allSongs
       val sizeOflist = songs.size;
       if(sizeOflist == 0) {
           longToast("No Songs Available");
           finish()
       }
       val musicPlay: Button = findViewById(R.id.play);
       val next: Button = findViewById(R.id.next);
       val prev: Button = findViewById(R.id.previous);
       var songNum: Int = 0;
       val song = songs[songNum];
       val tittle: TextView = findViewById(R.id.titlee);
       val albmart: com.mikhaellopez.circularimageview.CircularImageView =
           findViewById(R.id.albmPic);
       val songArtistt: TextView = findViewById(R.id.artistt);


       musicPlay.setOnClickListener {
           val songPlaying: Boolean? = mediaPlayer?.isPlaying;
           if (songPlaying == true) {
               mediaPlayer?.pause()
               musicPlay.setBackgroundResource(R.drawable.play);
           } else {
               totalTime = song.duration.toInt();
               playSong(song = songs[songNum]);
               tittle.text = songs[songNum].title;
               songArtistt.text = songs[songNum].artist;
               albmart.setImageURI(songs[songNum].albumArt);
               if(albmart.drawable == null) albmart.setImageResource(R.drawable.music);
               musicPlay.setBackgroundResource(R.drawable.pause);
           }
       }
       musicPlay.callOnClick();
       //Next Song -----------------------------------
       next.setOnClickListener {
           songNum++;
           songNum %= sizeOflist;
           if (mediaPlayer?.isPlaying == true) {
               songPosition.progress = 0;
               playSong(song = songs[songNum]);
               tittle.text = songs[songNum].title;
               albmart.setImageURI(songs[songNum].albumArt);
               if(albmart.drawable == null) albmart.setImageResource(R.drawable.music);
               songArtistt.text = songs[songNum].artist;
           } else {
               songPosition.progress = 0;
               playSong(song = songs[songNum]);
               tittle.text = songs[songNum].title;
               albmart.setImageURI(songs[songNum].albumArt);
               if(albmart.drawable == null) albmart.setImageResource(R.drawable.music);
               songArtistt.text = songs[songNum].artist;
               musicPlay.setBackgroundResource(R.drawable.pause);
           }
       }

       //Previous Song -------------------------------

       prev.setOnClickListener {
           songNum--;
           if (songNum < 0) songNum = sizeOflist - 1;
           if (mediaPlayer?.isPlaying == true) {
               songPosition.progress = 0;
               playSong(song = songs[songNum]);
               tittle.text = songs[songNum].title;
               albmart.setImageURI(songs[songNum].albumArt);
               if(albmart.drawable == null) albmart.setImageResource(R.drawable.music);
               songArtistt.text = songs[songNum].artist;
           }
               else {
               songPosition.progress = 0;
                   playSong(song = songs[songNum]);
                   tittle.text = songs[songNum].title;
                   albmart.setImageURI(songs[songNum].albumArt);
               if(albmart.drawable == null) albmart.setImageResource(R.drawable.music);
                   songArtistt.text = songs[songNum].artist;
                   musicPlay.setBackgroundResource(R.drawable.pause);
               }
           }

           //-------------------------------------------
       songPosition.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
           override fun onProgressChanged(
               seekBar: SeekBar?,
               progress: Int,
               fromUser: Boolean
           ) {
               if(fromUser) {
                   mediaPlayer?.seekTo(progress);
               }
           }

           override fun onStartTrackingTouch(seekBar: SeekBar?) {

           }

           override fun onStopTrackingTouch(seekBar: SeekBar?) {

           }
       })
       val elapse: TextView? = findViewById(R.id.elapTime);
       val remT: TextView? = findViewById(R.id.remTime);
       runnable = Runnable {
               songPosition.progress = mediaPlayer?.currentPosition!!;
               handler.postDelayed(runnable, 1000)
               var temp = mediaPlayer?.currentPosition;
               var min = (temp?.div(1000) ?.div(60));
               var sec = (temp?.div(1000) ?.rem(60));
               var timeL = "";
               timeL += "$min";
               timeL += ":";
               if (sec != null) {
                   if(sec < 10) {
                       timeL += "0";
                   }
               }
               timeL += "$sec";
               elapse?.text = timeL;
               temp = totalTime - mediaPlayer?.currentPosition!!;
               min = (temp?.div(1000) ?.div(60));
               sec = (temp?.div(1000) ?.rem(60));
               timeL = "";
               timeL += "$min";
               timeL += ":";
               if (sec != null) {
                   if(sec < 10) {
                       timeL += "0";
                   }
               }
               timeL += "$sec";
               remT?.text = timeL;
               if(timeL == "0:00" && mediaPlayer?.isPlaying == true) {
                   next.callOnClick();
               }
           }
           handler.postDelayed(runnable, 1000)
   }

    private fun playSong(song: Song) {
        elapTime.text = "0:00";
        songPosition.max = song.duration.toInt();
        totalTime = song.duration.toInt();
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(this,song.uri);
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}