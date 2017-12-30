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
 * Created by lemon on 2017-06-23.
 */

public class RankView extends View {

    PieceList piece;
    EmoticonList emoticon;
    static Tetris t;
    Paint pnt_text;
    Paint pnt_prev;
    Paint pnt_border;
    static String user1, user1_rank, user1_id, user1_cate, user2, user2_rank, user2_id, user2_cate;
    public Handler handler;
    public Handler keyHandler;
    static boolean isEnd = false;
    static boolean isLeft = false;
    static boolean isRight = false;
    static boolean isDown = false;
    static boolean isRotate = false;
    static boolean isHold = false;
    static boolean isChange = false;
    static boolean isSet = false;
    static boolean isDestroy = false;
    static boolean isObstacle = false;
    static boolean user1_good, user2_good;
    static boolean user1_angry, user2_angry;
    static boolean user1_sad, user2_sad;
    int user1_good_count, user2_good_count;
    int user1_angry_count, user2_angry_count;
    int user1_sad_count, user2_sad_count;
    int emoticon_count;
    static boolean user1_disconnect = false;
    static boolean user2_disconnect = false;
    public RankView(Context context) {
        super(context);
        init(context);
    }

    public RankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context)
    {
        piece = new PieceList(context);
        emoticon = new EmoticonList(context);
        t = new Tetris();

        isEnd = false;
        isLeft = false;
        isRight = false;
        isDown = false;
        isRotate = false;
        isHold = false;
        isChange = false;
        isSet = false;
        isDestroy = false;
        isObstacle = false;
        user1_good = false;
        user2_good = false;
        user1_angry = false;
        user2_angry = false;
        user1_sad = false;
        user2_sad = false;
        user1_good_count = 0;
        user2_good_count = 0;
        user1_angry_count = 0;
        user2_angry_count = 0;
        user1_sad_count = 0;
        user2_sad_count = 0;
        emoticon_count = 50;
        user1_disconnect = false;
        user2_disconnect = false;

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

        handler = null;
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(RankGameActivity.client.allSet) {
                    t.actionDown();
                    invalidate();
                    isSet = true;
                }
                if(t.getLevel() > 60)
                    this.sendEmptyMessageDelayed(0, 100);
                else
                    this.sendEmptyMessageDelayed(0, 700-t.getLevel()*10);
            }
        };

        keyHandler = null;
        keyHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                if(isLeft)
                {
                    isLeft = false;
                    if(!user1_disconnect) {
                        t.actionLeft();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isRight)
                {
                    isRight = false;
                    if(!user1_disconnect) {
                        t.actionRight();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isDown)
                {
                    isDown = false;
                    if(!user1_disconnect) {
                        t.actionDown();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isRotate)
                {
                    isRotate = false;
                    if(!user1_disconnect) {
                        t.actionRotate();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isHold)
                {
                    isHold = false;
                    if(!user1_disconnect) {
                        t.HoldBlock();
                        invalidate();
                    }
                }
                if(isChange)
                {
                    isChange = false;
                    invalidate();
                }
                if(isObstacle)
                {
                    isObstacle = false;
                    t.isObstacle = true;
                }
                if(t.isDestroy)
                {
                    t.isDestroy = false;
                    isDestroy = true;
                }
                if(user1_good)
                {
                    if(user1_good_count > emoticon_count)
                    {
                        user1_good = false;
                        user1_good_count = 0;
                    }
                    else
                    {
                        user1_good_count++;
                    }
                    invalidate();
                }

                if(user2_good)
                {
                    if(user2_good_count > emoticon_count)
                    {
                        user2_good = false;
                        user2_good_count = 0;
                    }
                    else
                    {
                        user2_good_count++;
                    }
                    invalidate();
                }

                if(user1_sad)
                {
                    if(user1_sad_count > emoticon_count)
                    {
                        user1_sad = false;
                        user1_sad_count = 0;
                    }
                    else
                    {
                        user1_sad_count++;
                    }
                    invalidate();
                }
                if(user2_sad)
                {
                    if(user2_sad_count > emoticon_count)
                    {
                        user2_sad = false;
                        user2_sad_count = 0;
                    }
                    else
                    {
                        user2_sad_count++;
                    }
                    invalidate();
                }
                if(user1_angry)
                {
                    if(user1_angry_count > emoticon_count)
                    {
                        user1_angry = false;
                        user1_angry_count = 0;
                    }
                    else
                    {
                        user1_angry_count++;
                    }
                    invalidate();
                }
                if(user2_angry)
                {
                    if(user2_angry_count > emoticon_count)
                    {
                        user2_angry = false;
                        user2_angry_count = 0;
                    }
                    else
                    {
                        user2_angry_count++;
                    }
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

        setMeasuredDimension(width, height-paddingHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("Nick : " + user1, 150, 40, pnt_text);
        canvas.drawText("Rank : " + user1_rank, 150, 90, pnt_text);
        canvas.drawText("Nick : " + user2, 750, 40, pnt_text);
        canvas.drawText("Rank : " + user2_rank, 750, 90, pnt_text);

        // main board
        canvas.drawRect(piece.getWidth() * (1) - 5, piece.getWidth() * (3) - 5, piece.getWidth() * (11) + 5, piece.getWidth() * (23) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getBoard(j, i)), piece.getWidth() * (i + 1), piece.getWidth() * (j + 3), null);
            }
        }
        if(user1_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getWidth() * (3), piece.getWidth() * (13), pnt_text);
        }
        if(user1_good)
            canvas.drawBitmap(emoticon.getEmoticon("good"), 150, 150, null);
        if(user1_sad)
            canvas.drawBitmap(emoticon.getEmoticon("sad"), 150, 150, null);
        if(user1_angry)
            canvas.drawBitmap(emoticon.getEmoticon("angry"), 150, 150, null);

        // next board
        canvas.drawText("Next", piece.getWidth() * (13), piece.getWidth() * (4), pnt_text);

        canvas.drawRect(piece.getWidth() * (12) - 20, piece.getWidth() * (5) - 5, piece.getWidth() * (17) - 10, piece.getWidth() * (10) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                canvas.drawBitmap(piece.getPiece(t.getNext(j, i)), piece.getWidth() * (i + 12) - 15, piece.getWidth() * (j + 5), null);
            }
        }

        // hold board
        canvas.drawText("Hold", piece.getWidth() * (13), piece.getWidth() * (16), pnt_text);
        canvas.drawRect(piece.getWidth() * (12) - 20, piece.getWidth() * (17) - 5, piece.getWidth() * (17) - 10, piece.getWidth() * (22) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                canvas.drawBitmap(piece.getPiece(t.getHold(j, i)), piece.getWidth() * (i + 12) - 15, piece.getWidth() * (j + 17), null);
            }
        }
        canvas.drawText(Integer.toString(t.getHoldCount()) + " / 5", piece.getWidth() * (13), piece.getWidth() * (23)+10, pnt_text);

        // match user board
        canvas.drawRect(piece.getWidth() * (17) + 5, piece.getWidth() * (3) - 5, piece.getWidth() * (27) + 15, piece.getWidth() * (23) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getEnemyBoard(j, i)), piece.getWidth() * (i + 17) + 10, piece.getWidth() * (j + 3), null);
            }
        }
        if(user2_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getWidth() * (20), piece.getWidth() * (13), pnt_text);
        }
        if(user2_good)
            canvas.drawBitmap(emoticon.getEmoticon("good"), 800, 150, null);
        if(user2_sad)
            canvas.drawBitmap(emoticon.getEmoticon("sad"), 750, 150, null);
        if(user2_angry)
            canvas.drawBitmap(emoticon.getEmoticon("angry"), 750, 150, null);

        // end check
        if(t.isEnd) {
            isEnd = true;
            piece.recyclePiece();
            emoticon.recycleEmoticon();
            handler.removeMessages(0);
            keyHandler.removeMessages(0);
        }

        if(isEnd == true)
        {
            piece.recyclePiece();
            emoticon.recycleEmoticon();
            handler.removeMessages(0);
            keyHandler.removeMessages(0);
        }
    }
}