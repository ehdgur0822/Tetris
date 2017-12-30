package com.example.lunaticlemon.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by lemon on 2017-07-13.
 */

public class EmoticonList {

    private int width, reduced_width;
    private Bitmap[] emoticon = new Bitmap[3];
    private Bitmap[] emoticon_reduced = new Bitmap[3];
    public EmoticonList(Context con)
    {
        Resources res = con.getResources();
        width = 150;
        reduced_width = 100;
        emoticon[0] = BitmapFactory.decodeResource(res, R.drawable.emoticon_good);
        emoticon_reduced[0] = Bitmap.createScaledBitmap(emoticon[0], reduced_width, reduced_width, false);
        emoticon[0] = Bitmap.createScaledBitmap(emoticon[0], width, width, false);
        emoticon[1] = BitmapFactory.decodeResource(res, R.drawable.emoticon_sad);
        emoticon_reduced[1] = Bitmap.createScaledBitmap(emoticon[1], reduced_width, reduced_width, false);
        emoticon[1] = Bitmap.createScaledBitmap(emoticon[1], width, width, false);
        emoticon[2] = BitmapFactory.decodeResource(res, R.drawable.emoticon_angry);
        emoticon_reduced[2] = Bitmap.createScaledBitmap(emoticon[2], reduced_width, reduced_width, false);
        emoticon[2] = Bitmap.createScaledBitmap(emoticon[2], width, width, false);
    }

    public int getWidth() {
        return width;
    }

    public Bitmap getEmoticon(String type)
    {
        switch(type)
        {
            case "good":
                return emoticon[0];
            case "sad":
                return emoticon[1];
            case "angry":
                return emoticon[2];
        }

        return null;
    }

    public Bitmap getReducedEmoticon(String type)
    {
        switch (type) {
            case "good":
                return emoticon_reduced[0];
            case "sad":
                return emoticon_reduced[1];
            case "angry":
                return emoticon_reduced[2];
        }

        return null;
    }


    public void recycleEmoticon()
    {
        for(int i=0;i<3;i++)
        {
            emoticon[i].recycle();
            emoticon_reduced[i].recycle();
        }
    }
}
