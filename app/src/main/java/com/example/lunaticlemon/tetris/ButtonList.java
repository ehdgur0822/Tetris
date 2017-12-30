package com.example.lunaticlemon.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by lemon on 2017-06-06.
 */

public class ButtonList {

    private int width;
    private Bitmap[] button = new Bitmap[6];

    public ButtonList(Context con)
    {
        Resources res = con.getResources();
        button[0] = BitmapFactory.decodeResource(res, R.drawable.left_unclicked);
        button[1] = BitmapFactory.decodeResource(res, R.drawable.left_clicked);
        button[2] = BitmapFactory.decodeResource(res, R.drawable.right_unclicked);
        button[3] = BitmapFactory.decodeResource(res, R.drawable.right_clicked);
        button[4] = BitmapFactory.decodeResource(res, R.drawable.rotate_unclicked);
        button[5] = BitmapFactory.decodeResource(res, R.drawable.rotate_clicked);

        width = button[0].getWidth();
    }

    public int getWidth() {
        return width;
    }

    public Bitmap getButton(int buttonnum)
    {
        return button[buttonnum];
    }

    public void recycleButton()
    {
        for(int i=0;i<6;i++)
        {
            button[i].recycle();
        }
    }
}
