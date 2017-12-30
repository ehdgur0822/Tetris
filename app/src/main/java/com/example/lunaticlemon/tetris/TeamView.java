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

public class TeamView extends View {

    PieceList piece;
    EmoticonList emoticon;
    static TeamTetris t;
    Paint pnt_text, pnt_text1;
    Paint pnt_prev;
    Paint pnt_border;
    static String user1, user1_rank, user1_id, user1_cate, user2, user2_rank, user2_id, user2_cate, user3, user3_rank, user3_id, user3_cate, user4, user4_rank, user4_id, user4_cate;
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
    static boolean isDelete = false;
    static boolean isDeleteAck = false;
    static boolean user1_good, user2_good, user3_good, user4_good;
    static boolean user1_angry, user2_angry, user3_angry, user4_angry;
    static boolean user1_sad, user2_sad, user3_sad, user4_sad;
    int user1_good_count, user2_good_count, user3_good_count, user4_good_count;
    int user1_angry_count, user2_angry_count, user3_angry_count, user4_angry_count;
    int user1_sad_count, user2_sad_count, user3_sad_count, user4_sad_count;
    int emoticon_count;
    static boolean user1_disconnect = false;
    static boolean user2_disconnect = false;
    static boolean user3_disconnect = false;
    static boolean user4_disconnect = false;

    public TeamView(Context context) {
        super(context);
        init(context);
    }

    public TeamView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TeamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TeamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context)
    {
        piece = new PieceList(context);
        emoticon = new EmoticonList(context);
        t = new TeamTetris();

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
        isDelete = false;
        isDeleteAck = false;
        user1_good = false;
        user2_good = false;
        user3_good = false;
        user4_good  = false;
        user1_angry = false;
        user2_angry = false;
        user3_angry = false;
        user4_angry = false;
        user1_sad = false;
        user2_sad = false;
        user3_sad = false;
        user4_sad = false;
        user1_good_count = 0;
        user2_good_count = 0;
        user3_good_count = 0;
        user4_good_count = 0;
        user1_angry_count = 0;
        user2_angry_count = 0;
        user3_angry_count = 0;
        user4_angry_count = 0;
        user1_sad_count = 0;
        user2_sad_count = 0;
        user3_sad_count = 0;
        user4_sad_count = 0;
        emoticon_count = 13;
        user1_disconnect = false;
        user2_disconnect = false;
        user3_disconnect = false;
        user4_disconnect = false;

        pnt_text = new Paint();
        pnt_text.setAntiAlias(true);
        pnt_text.setColor(Color.WHITE);
        pnt_text.setTextSize(50);

        pnt_text1 = new Paint();
        pnt_text1.setAntiAlias(true);
        pnt_text1.setColor(Color.WHITE);
        pnt_text1.setTextSize(25);

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
                if(TeamGameActivity.teamclient.allSet) {
                    t.actionDown();
                    invalidate();
                    isSet = true;
                }
                this.sendEmptyMessageDelayed(0, 1000);
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
                    if(!TeamView.user1_disconnect) {
                        t.actionLeft();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isRight)
                {
                    isRight = false;
                    if(!TeamView.user1_disconnect) {
                        t.actionRight();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isDown)
                {
                    isDown = false;
                    if(!TeamView.user1_disconnect) {
                        t.actionDown();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isRotate)
                {
                    isRotate = false;
                    if(!TeamView.user1_disconnect) {
                        t.actionRotate();
                        invalidate();
                        isSet = true;
                    }
                }
                if(isHold)
                {
                    isHold = false;
                    if(!TeamView.user1_disconnect) {
                        t.HoldBlock();
                        invalidate();
                    }
                }
                if(isChange)
                {
                    isChange = false;
                    if(!isDelete)
                        TeamGameActivity.isDeleteSend = t.canDelete();
                    invalidate();
                }
                if(isObstacle)
                {
                    isObstacle = false;
                    t.isObstacle = true;
                }
                if(TeamView.isDeleteAck)
                {
                    TeamView.isDeleteAck = false;
                    t.deleteRow();
                    invalidate();
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
                if(user3_good)
                {
                    if(user3_good_count > emoticon_count)
                    {
                        user3_good = false;
                        user3_good_count = 0;
                    }
                    else
                    {
                        user3_good_count++;
                    }
                    invalidate();
                }
                if(user4_good)
                {
                    if(user4_good_count > emoticon_count)
                    {
                        user4_good = false;
                        user4_good_count = 0;
                    }
                    else
                    {
                        user4_good_count++;
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
                if(user3_sad)
                {
                    if(user3_sad_count > emoticon_count)
                    {
                        user3_sad = false;
                        user3_sad_count = 0;
                    }
                    else
                    {
                        user3_sad_count++;
                    }
                    invalidate();
                }
                if(user4_sad)
                {
                    if(user4_sad_count > emoticon_count)
                    {
                        user4_sad = false;
                        user4_sad_count = 0;
                    }
                    else
                    {
                        user4_sad_count++;
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
                if(user3_angry)
                {
                    if(user3_angry_count > emoticon_count)
                    {
                        user3_angry = false;
                        user3_angry_count = 0;
                    }
                    else
                    {
                        user3_angry_count++;
                    }
                    invalidate();
                }
                if(user4_angry)
                {
                    if(user4_angry_count > emoticon_count)
                    {
                        user4_angry = false;
                        user4_angry_count = 0;
                    }
                    else
                    {
                        user4_angry_count++;
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

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("Nick : " + user3, 280, 200, pnt_text1);
        canvas.drawText("Rank : " + user3_rank, 280, 220, pnt_text1);

        // enemy1 board , user3
        canvas.drawRect(piece.getResizedWidth() * (22), piece.getResizedWidth() * (3) - 5, piece.getResizedWidth() * (32) + 10, piece.getResizedWidth() * (23) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getResizePiece(t.getEnemy1Board(j, i)), piece.getResizedWidth() * (i + 22), piece.getResizedWidth() * (j + 3), null);
            }
        }
        if(TeamView.user3_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getResizedWidth() * (24), piece.getResizedWidth() * (14), pnt_text1);
        }
        if(user3_good)
            canvas.drawBitmap(emoticon.getReducedEmoticon("good"), 430, 100, null);
        if(user3_sad)
            canvas.drawBitmap(emoticon.getReducedEmoticon("sad"), 430, 100, null);
        if(user3_angry)
            canvas.drawBitmap(emoticon.getReducedEmoticon("angry"), 430, 100, null);

        canvas.drawText("Nick : " + user4, 800, 200, pnt_text1);
        canvas.drawText("Rank : " + user4_rank, 800, 220, pnt_text1);

        // enemy2 board, user4
        canvas.drawRect(piece.getResizedWidth() * (33) - 5, piece.getResizedWidth() * (3) - 5, piece.getResizedWidth() * (43) + 5, piece.getResizedWidth() * (23) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getResizePiece(t.getEnemy2Board(j, i)), piece.getResizedWidth() * (i + 33), piece.getResizedWidth() * (j + 3), null);
            }
        }
        if(TeamView.user4_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getResizedWidth() * (35), piece.getResizedWidth() * (14), pnt_text1);
        }
        if(user4_good)
            canvas.drawBitmap(emoticon.getReducedEmoticon("good"), 630, 100, null);
        if(user4_sad)
            canvas.drawBitmap(emoticon.getReducedEmoticon("sad"), 630, 100, null);
        if(user4_angry)
            canvas.drawBitmap(emoticon.getReducedEmoticon("angry"), 630, 100, null);

        canvas.drawText("Nick : " + user1, 350, 480, pnt_text);
        canvas.drawText("Rank : " + user1_rank, 350, 530, pnt_text);
        canvas.drawText("Nick : " + user2, 800, 480, pnt_text);
        canvas.drawText("Rank : " + user2_rank, 800, 530, pnt_text);

        // main board, user1
        canvas.drawRect(piece.getWidth() * (7) - 5, piece.getWidth() * (14) - 5, piece.getWidth() * (17) + 5, piece.getWidth() *  (34) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getBoard(j, i)), piece.getWidth() * (i + 7), piece.getWidth() * (j + 14), null);
            }
        }
        if(TeamView.user1_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getWidth() * (9), piece.getWidth() * (25), pnt_text);
        }
        if(user1_good)
            canvas.drawBitmap(emoticon.getEmoticon("good"), 400, 650, null);
        if(user1_sad)
            canvas.drawBitmap(emoticon.getEmoticon("sad"), 400, 650, null);
        if(user1_angry)
            canvas.drawBitmap(emoticon.getEmoticon("angry"), 400, 650, null);

        // next board
        canvas.drawText("Next", piece.getWidth() * (2), piece.getWidth() * (15), pnt_text);
        canvas.drawRect(piece.getWidth() * (1) - 20, piece.getWidth() * (16) - 5, piece.getWidth() * (6) - 10, piece.getWidth() * (21) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                canvas.drawBitmap(piece.getPiece(t.getNext(j, i)), piece.getWidth() * (i + 1) - 15, piece.getWidth() * (j + 16), null);
            }
        }

        // hold board
        canvas.drawText("Hold", piece.getWidth() * (2), piece.getWidth() * (27), pnt_text);
        canvas.drawRect(piece.getWidth() * (1) - 20, piece.getWidth() * (28) - 5, piece.getWidth() * (6) - 10, piece.getWidth() * (33) + 5, pnt_border);
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                canvas.drawBitmap(piece.getPiece(t.getHold(j, i)), piece.getWidth() * (i + 1) - 15, piece.getWidth() * (j + 28), null);
            }
        }
        canvas.drawText(Integer.toString(t.getHoldCount()) + " / 5", piece.getWidth() * (2), piece.getWidth() * (34)+10, pnt_text);

        // team board, user2
        canvas.drawRect(piece.getWidth() * (17) + 5, piece.getWidth() * (14) - 5, piece.getWidth() * (27) + 15, piece.getWidth() * (34) + 5, pnt_border);
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                canvas.drawBitmap(piece.getPiece(t.getTeamBoard(j, i)), piece.getWidth() * (i + 17) + 10, piece.getWidth() * (j + 14), null);
            }
        }
        if(TeamView.user2_disconnect)
        {
            canvas.drawText("DISCONNECT", piece.getWidth() * (19), piece.getWidth() * (25), pnt_text);
        }
        if(user2_good)
            canvas.drawBitmap(emoticon.getEmoticon("good"), 800, 650, null);
        if(user2_sad)
            canvas.drawBitmap(emoticon.getEmoticon("sad"), 800, 650, null);
        if(user2_angry)
            canvas.drawBitmap(emoticon.getEmoticon("angry"), 800, 650, null);

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