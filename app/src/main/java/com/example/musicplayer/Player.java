package com.example.musicplayer;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayer.MainActivity.arrayList;

public class Player extends AppCompatActivity {
    TextView song_name,artist_name,duration_start,duration_end;
    ImageView album_art,next_btn,previous_btn,back_btn,shuffle_btn,repeat_btn,play_pause,heart;
    ArcSeekBar Seek_bar;
    Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    int position=-1;
    Runnable runnable;
    Thread playThread,prevThread,nextThread;
    boolean shuffleboolean=false,repeatboolean=false;
    static ArrayList<Music> songlist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

        song_name = (TextView) findViewById(R.id.name);
        artist_name = (TextView) findViewById(R.id.artist);
        duration_start = (TextView) findViewById(R.id.duration_start);
        duration_end = (TextView) findViewById(R.id.duration_end);
        album_art = (ImageView) findViewById(R.id.album_art);
        next_btn = (ImageView) findViewById(R.id.next_btn);
        previous_btn = (ImageView) findViewById(R.id.previous_btn);
        shuffle_btn = (ImageView) findViewById(R.id.shuffle_btn);
        repeat_btn = (ImageView) findViewById(R.id.repeat_btn);
        play_pause = (ImageView) findViewById(R.id.play_pause);
        heart = (ImageView) findViewById(R.id.heart);
        Seek_bar = (ArcSeekBar) findViewById(R.id.arc_seek_bar);

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heart.setImageResource(R.drawable.heart1);
            }
        });

        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleboolean)
                {
                    shuffleboolean=false;
                    shuffle_btn.setImageResource(R.drawable.shuffle);
                }else
                {
                    shuffleboolean=true;
                    shuffle_btn.setImageResource(R.drawable.shuffle_on);
                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatboolean)
                {
                    repeatboolean=false;
                    repeat_btn.setImageResource(R.drawable.repeat);
                }else
                {
                    repeatboolean=true;
                    repeat_btn.setImageResource(R.drawable.repeat_on);
                }
            }
        });

        getIntentMethod();
        song_name.setText(songlist.get(position).getTitle());
        artist_name.setText(songlist.get(position).getArtist());

        Seek_bar.setOnProgressChangedListener(new ProgressListener() {
            @Override
            public void invoke(int progress) {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.seekTo(progress);;
                }}}
                );

            Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 if(mediaPlayer!=null)
                 {
                    int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    duration_start.setText(formattedTime(CurrentPosition));
//                   Seek_bar.setProgress(mediaPlayer.getCurrentPosition());

                 }handler.postDelayed(this, 500); }
                 }); handler.postDelayed(runnable,500);


    }


    public String formattedTime(int CurrentPosition)
       {
        String totalout = "";
        String totalnew = "";
        String seconds  = String.valueOf(CurrentPosition % 60);
        String minutes  = String.valueOf(CurrentPosition/60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;
       if(seconds.length()==1)
       {
         return totalnew;
      }else
      {
          return totalout;
      }
     }
    public void getIntentMethod()
    {
        position= getIntent().getIntExtra("position",-1);
        songlist=arrayList;
        if(songlist!=null )
        {
            play_pause.setImageResource(R.drawable.pause1);
            uri=Uri.parse(songlist.get(position).getPath());
        }
        if(mediaPlayer!=null)
        {
        mediaPlayer.release();
        mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
        }});
        }
        else
            {
                mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
            }

        Seek_bar.setMaxProgress(mediaPlayer.getDuration());
        metaData(uri);
    }
    public void metaData(Uri uri)//for album art
    {
        final MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(songlist.get(position).getDuration())/1000;
        duration_end.setText(formattedTime(durationTotal));

                byte[] art=retriever.getEmbeddedPicture();

                if(art!=null)
                {

                    Glide.with(getApplicationContext()).asBitmap().load(art).into(album_art);
                }else
                {

                    Glide.with(getApplicationContext()).asBitmap().load(R.drawable.icon).into(album_art);
                }


    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                previous_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       prevBtnClicked();
                    }


                });
            }
        };prevThread.start();
    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatboolean)
            {
                position=getRandom(songlist.size()-1);
            }else if(!shuffleboolean && !repeatboolean) {
                position=((position-1)<0 ? (songlist.size()-1) : (position-1));
            }
            uri=uri.parse(songlist.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(songlist.get(position).getTitle());
            artist_name.setText(songlist.get(position).getArtist());

            Seek_bar.setMaxProgress(mediaPlayer.getDuration());
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                        Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);
            play_pause.setImageResource(R.drawable.pause1);
            mediaPlayer.start();

        }else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatboolean)
            {
                position=getRandom(songlist.size()-1);
            }else if(!shuffleboolean && !repeatboolean) {
            position=((position-1)<0 ? (songlist.size()-1) : (position-1));
        }
            uri=uri.parse(songlist.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(songlist.get(position).getTitle());
            artist_name.setText(songlist.get(position).getArtist());

            Seek_bar.setMaxProgress(mediaPlayer.getDuration());
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                        Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);
            play_pause.setImageResource(R.drawable.play1);
        }

    }

    private void nextThreadBtn() {
        nextThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }


                });
            }
        };nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatboolean)
            {
                position=getRandom(songlist.size()-1);
            }else if(!shuffleboolean && !repeatboolean) {
                position = ((position + 1) % songlist.size());
            }
            uri=uri.parse(songlist.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(songlist.get(position).getTitle());
            artist_name.setText(songlist.get(position).getArtist());

            Seek_bar.setMaxProgress(mediaPlayer.getDuration());
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                        Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);
            play_pause.setImageResource(R.drawable.pause1);
            mediaPlayer.start();

        }else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatboolean)
            {
                position=getRandom(songlist.size()-1);
            }else if(!shuffleboolean && !repeatboolean) {
                position = ((position + 1) % songlist.size());
            }
            uri=uri.parse(songlist.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(songlist.get(position).getTitle());
            artist_name.setText(songlist.get(position).getArtist());

            Seek_bar.setMaxProgress(mediaPlayer.getDuration());
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                        Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);
            play_pause.setImageResource(R.drawable.play1);
        }

    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playpauseBtnClicked();
                    }

                    
                });
            }
        };playThread.start();
    }

    private void playpauseBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            play_pause.setImageResource(R.drawable.play1);
            mediaPlayer.pause();
//          Seek_bar.setMaxProgress(mediaPlayer.getDuration()/1000);
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                        Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);

        }else
        {
            play_pause.setImageResource(R.drawable.pause1);
            mediaPlayer.start();
//           Seek_bar.setMaxProgress(mediaPlayer.getDuration()/1000);
//            Player.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mediaPlayer!=null)
//                    {
//                        int CurrentPosition=mediaPlayer.getCurrentPosition()/1000;
//                       Seek_bar.setProgress(mediaPlayer.getCurrentPosition());
//
//                    }handler.postDelayed(this, 500); }
//            }); handler.postDelayed(runnable,500);

        }
    }

    @Override
    public void finish()//animation of screen
    {
        super.finish();
        overridePendingTransition(R.anim.no_animation,R.anim.slide_down);
    }


}