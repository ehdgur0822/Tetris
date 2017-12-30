package com.example.lunaticlemon.tetris;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.STROKE;

/**
 * Created by lemon on 2017-06-04.
 */

public class SingleView extends View{

    PieceList piece;
    Tetris t;
    Paint pnt_text;
    Paint pnt_prev;
    Paint pnt_border;
    public Handler handler;
    public Handler keyHandler;
    static int score;
    static boolean isEnd = false;
    static boolean isLeft = false;
    static boolean isRight = false;
    static boolean isDown = false;
    static boolean isRotate = false;
    static boolean isHold = false;
    static boolean isEcho = false;
    int cur_destroy;

    public SingleView(Context context)
    {
        super(context);
        init(context);
    }

    public SingleView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SingleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context)
    {
        isEnd = false;
        isLeft = false;
        isRight = false;
        isDown = false;
        isRotate = false;
        isHold = false;
        isEcho = false;

        piece = new PieceList(context);
        t = new Tetris();
        cur_destroy = t.total_destroy;

        pnt_text = new Paint();
        pnt_text.setAntiAlias(true);
        pnt_text.setColor(Color.WHITE);
        pnt_text.setTextSize(50);

        pnt_prev = new Paint();
        pnt_prev.setAlpha(70);

        pnt_border = new Paint();
        pnt_border.setColor(Color.YELLOW);
        pnt_border.setStyle(STROKE);
        pnt_border.setStrokeWidth(2.0f);
        pnt_border.setStrokeCap(ROUND);

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                t.actionDown();
                invalidate();
                if(cur_destroy != t.total_destroy)
                {
                    isEcho = true;
                    cur_destroy = t.total_destroy;
                }
                this.sendEmptyMessageDelayed(0, 1010 - t.getLevel()*10);
            }
        };

        keyHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(isLeft)
                {
                    isLeft = false;
                    t.actionLeft();
                    invalidate();
                }
                if(isRight)
                {
                    isRight = false;
                    t.actionRight();
                    invalidate();
                }
                if(isDown)
                {
                    isDown = false;
                    t.actionDown();
                    invalidate();
                }
                if(isRotate)
                {
                    isRotate = false;
                    t.actionRotate();
                    invalidate();
                }
                if(isHold)
                {
                    isHold = false;
                    t.HoldBlock();
                    invalidate();
                }

                this.sendEmptyMessage(0);
            }
        };

        handler.sendEmptyMessage(0);
        keyHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 측정된 폭과 높이를 출력해 보자
        int width, height;
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        // 패딩값을 측정값의 10%를 주어 뺀다.
        int paddingWidth = width / 5;
        int paddingHeight = height / 40;

        setMeasuredDimension(width-paddingWidth, height-paddingHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("Score : " + String.valueOf(t.getScore()), 50, 40, pnt_text);
        canvas.drawText("Level : " + String.valueOf(t.getLevel()), 550, 40, pnt_text);

        // main board
        canvas.drawRect(piece.getWidth() * (1) - 5, piece.getWidth() * (3) - 5, piece.getWidth() * (11) + 5, piece.getWidth() * (23) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getBoard(j, i)), piece.getWidth() * (i + 1), piece.getWidth() * (j + 3), null);
            }
        }


        // next board
        canvas.drawText("Next", piece.getWidth() * (15), piece.getWidth() * (4), pnt_text);
        canvas.drawRect(piece.getWidth() * (14) - 5, piece.getWidth() * (5) - 5, piece.getWidth() * (19) + 5, piece.getWidth() * (10) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                    canvas.drawBitmap(piece.getPiece(t.getNext(j, i)), piece.getWidth() * (i + 14), piece.getWidth() * (j + 5), null);
            }
        }


        // hold board
        canvas.drawText("Hold", piece.getWidth() * (15), piece.getWidth() * (16), pnt_text);
        canvas.drawRect(piece.getWidth() * (14) - 5, piece.getWidth() * (17) - 5, piece.getWidth() * (19) + 5, piece.getWidth() * (22) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                canvas.drawBitmap(piece.getPiece(t.getHold(j, i)), piece.getWidth() * (i + 14), piece.getWidth() * (j + 17), null);
            }
        }
        canvas.drawText(Integer.toString(t.getHoldCount()) + " / 5", piece.getWidth() * (15), piece.getWidth() * (23)+10, pnt_text);

        /*
        // test board
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getfBoard(j, i)), piece.getWidth() * (i + 12), piece.getWidth() * (j + 5), null);
            }
        }
        */
        // end check
        if(t.isEnd) {
            isEnd = true;
            piece.recyclePiece();
            handler.removeMessages(0);
            keyHandler.removeMessages(0);
        }

        if(isEnd == true)
        {
            piece.recyclePiece();
            handler.removeMessages(0);
            keyHandler.removeMessages(0);
        }

        score = t.getScore();
    }
}
