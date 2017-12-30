package com.example.lunaticlemon.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by lemon on 2017-06-05.
 */

public class PieceList {

    private int width;
    private Bitmap[] piece = new Bitmap[9];

    public PieceList(Context con)
    {
        Resources res = con.getResources();
        piece[0] = BitmapFactory.decodeResource(res, R.drawable.piece_00);
        piece[1] = BitmapFactory.decodeResource(res, R.drawable.piece_01);
        piece[2] = BitmapFactory.decodeResource(res, R.drawable.piece_02);
        piece[3] = BitmapFactory.decodeResource(res, R.drawable.piece_03);
        piece[4] = BitmapFactory.decodeResource(res, R.drawable.piece_04);
        piece[5] = BitmapFactory.decodeResource(res, R.drawable.piece_05);
        piece[6] = BitmapFactory.decodeResource(res, R.drawable.piece_06);
        piece[7] = BitmapFactory.decodeResource(res, R.drawable.piece_07);
        piece[8] = BitmapFactory.decodeResource(res, R.drawable.piece_08);

        width = piece[0].getWidth();
    }

    public int getWidth() {
        return width;
    }

    public int getResizedWidth() { return 18;}

    public Bitmap getPiece(int piecenum)
    {
        return piece[piecenum];
    }

    public Bitmap getResizePiece(int piecenum) { return Bitmap.createScaledBitmap(piece[piecenum], 18, 18, false); }

    public void recyclePiece()
    {
        for(int i=0;i<9;i++)
        {
            piece[i].recycle();
        }
    }
}
