package com.example.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends ArrayAdapter<Music>{

    private static final String LOG_TAG = MusicAdapter.class.getSimpleName();
    private final Context context;

    public MusicAdapter(Context context, ArrayList<Music> arrayList) {
        super(context,0,arrayList);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View ListItemView=convertView;
        if(ListItemView==null)
        {
            ListItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.music, parent, false);
        }
        Music currentMusic = getItem(position);
        TextView textView = (TextView) ListItemView.findViewById(R.id.music_name);
        textView.setText(currentMusic.getTitle());

        TextView textView1 = (TextView) ListItemView.findViewById(R.id.music_artist);
        textView1.setText(currentMusic.getArtist());

        ImageView albumArt= (ImageView) ListItemView.findViewById(R.id.music_image);



             byte[] art=getAlbumArt(currentMusic.getPath());
             if(art!=null)
             {

                 Glide.with(context).asBitmap().load(art).into(albumArt);
             }else
             {

                 Glide.with(context).asBitmap().load(R.drawable.icon).into(albumArt);
             }





        return ListItemView;
    }
   public byte[] getAlbumArt(String uri)
  {
      MediaMetadataRetriever retriever= new MediaMetadataRetriever();
      retriever.setDataSource(uri);
     byte[] art=retriever.getEmbeddedPicture();

      retriever.release();
       return  art;
  }

};
