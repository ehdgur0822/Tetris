package com.example.lunaticlemon.tetris;

import java.util.Random;

/**
 * Created by lemon on 2017-06-05.
 */

public class Tetris {
    Random random;
    private int[][][] piece1 = {{{0,1,0,0},{1,1,1,0},{0,0,0,0},{0,0,0,0}}, {{0,1,0,0},{0,1,1,0},{0,1,0,0},{0,0,0,0}}, {{0,0,0,0},{1,1,1,0},{0,1,0,0},{0,0,0,0}}, {{0,1,0,0},{1,1,0,0},{0,1,0,0},{0,0,0,0}}};
    private int[][][] piece2 = {{{0,0,0,0},{2,2,0,0},{0,2,2,0},{0,0,0,0}}, {{0,2,0,0},{2,2,0,0},{2,0,0,0},{0,0,0,0}}};
    private int[][][] piece3 = {{{0,0,0,0},{0,3,3,0},{3,3,0,0},{0,0,0,0}}, {{0,3,0,0},{0,3,3,0},{0,0,3,0},{0,0,0,0}}};
    private int[][][] piece4 = {{{4,0,0,0},{4,4,4,0},{0,0,0,0},{0,0,0,0}}, {{0,4,4,0},{0,4,0,0},{0,4,0,0},{0,0,0,0}}, {{0,0,0,0},{4,4,4,0},{0,0,4,0},{0,0,0,0}}, {{0,4,0,0},{0,4,0,0},{4,4,0,0},{0,0,0,0}}};
    private int[][][] piece5 = {{{0,0,5,0},{5,5,5,0},{0,0,0,0},{0,0,0,0}}, {{0,5,0,0},{0,5,0,0},{0,5,5,0},{0,0,0,0}}, {{0,0,0,0},{5,5,5,0},{5,0,0,0},{0,0,0,0}}, {{5,5,0,0},{0,5,0,0},{0,5,0,0},{0,0,0,0}}};
    private int[][][] piece6 = {{{0,0,0,0},{0,6,6,0},{0,6,6,0},{0,0,0,0}}};
    private int[][][] piece7 = {{{0,0,0,0},{0,0,0,0},{7,7,7,7},{0,0,0,0}}, {{0,0,7,0},{0,0,7,0},{0,0,7,0},{0,0,7,0}}};
    private int[][] board = new int[20][10];
    private int[][] fixed_board = new int[20][10];
    private int[][] enemy_board = new int[20][10];
    private int[][] next = new int[5][5];
    private int[][] hold = new int[5][5];
    private int holdCount;
    private int score;
    private int level;
    private int cur_piece, cur_ori, cur_row, cur_col, next_piece, next_ori, hold_piece, hold_ori;
    private boolean isDownLock;
    public boolean isEnd;
    public boolean isDestroy;
    public boolean isObstacle;
    public int total_destroy;

    public Tetris()
    {
        random = new Random();

        for(int i=0;i<board[0].length;i++)
        {
            for(int j=0;j<board.length;j++)
            {
                board[j][i] = 0;
                fixed_board[j][i] = 0;
                enemy_board[j][i] = 0;
            }
        }

        for(int i=0;i<next[0].length;i++)
        {
            for(int j=0;j<next.length;j++)
            {
                next[j][i] = 0;
                hold[j][i] = 0;
            }
        }

        score = 0;
        level = 1;
        holdCount = 5;

        isDownLock = false;
        isEnd = false;
        isDestroy = false;
        isObstacle = false;
        total_destroy = 0;

        next_piece = -1;
        hold_piece = -1;
        makeNew();
    }

    private void makeNext()
    {
        int mask=0;

        next_piece = random.nextInt(100) % 7 + 1;
        next_ori = 0;

        for(int j=0;j<4;j++)
        {
            for(int i=0;i<4;i++)
            {
                switch(next_piece)
                {
                    case 1:
                        mask = piece1[next_ori][j][i];
                        break;
                    case 2:
                        mask = piece2[next_ori][j][i];
                        break;
                    case 3:
                        mask = piece3[next_ori][j][i];
                        break;
                    case 4:
                        mask = piece4[next_ori][j][i];
                        break;
                    case 5:
                        mask = piece5[next_ori][j][i];
                        break;
                    case 6:
                        mask = piece6[next_ori][j][i];
                        break;
                    case 7:
                        mask = piece7[next_ori][j][i];
                        break;
                }

                next[j+1][i+1] = mask;
            }
        }

    }

    private void makeNew()
    {
        int first_row=0,flag,mask=0;

        // set piece
        if(next_piece == -1)
        {
            cur_piece = random.nextInt(100)%7 + 1;
            cur_ori = 0;
            makeNext();
        }
        else
        {
            cur_piece = next_piece;
            cur_ori = next_ori;
            makeNext();
        }

        // find first non zero row
        flag = 0;
        for(int j=0;j<4;j++)
        {
            for (int i=0; i<4; i++)
            {
                switch (cur_piece)
                {
                    case 1:
                        if (piece1[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 2:
                        if (piece2[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 3:
                        if (piece3[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 4:
                        if (piece4[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 5:
                        if (piece5[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 6:
                        if (piece6[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                    case 7:
                        if (piece7[cur_ori][j][i] != 0)
                            flag = 1;
                        break;
                }

                if(flag == 1)
                    break;
            }
            if (flag == 1)
            {
                first_row = j;
                break;
            }
        }

        // set board
        for(int j=first_row,k=0;j<4;j++,k++)
        {
            for(int i=0;i<4;i++)
            {
                switch(cur_piece)
                {
                    case 1:
                        mask = piece1[cur_ori][j][i];
                        break;
                    case 2:
                        mask = piece2[cur_ori][j][i];
                        break;
                    case 3:
                        mask = piece3[cur_ori][j][i];
                        break;
                    case 4:
                        mask = piece4[cur_ori][j][i];
                        break;
                    case 5:
                        mask = piece5[cur_ori][j][i];
                        break;
                    case 6:
                        mask = piece6[cur_ori][j][i];
                        break;
                    case 7:
                        mask = piece7[cur_ori][j][i];
                        break;
                }

                if(mask != 0 && board[k][i+3] != 0)
                    isEnd = true;

                board[k][i+3] = mask;
            }
        }

        cur_row = first_row * -1;
        cur_col = 3;
    }

    private void setBoard()
    {
        for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
        {
            for(int k=cur_col,l=0;k<((cur_col+4)>board[0].length?board[0].length:(cur_col+4));k++,l++)
            {
                switch(cur_piece)
                {
                    case 1:
                        if(piece1[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece1[cur_ori][j][l];
                        }
                        break;
                    case 2:
                        if(piece2[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece2[cur_ori][j][l];
                        }
                        break;
                    case 3:
                        if(piece3[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece3[cur_ori][j][l];
                        }
                        break;
                    case 4:
                        if(piece4[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece4[cur_ori][j][l];
                        }
                        break;
                    case 5:
                        if(piece5[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece5[cur_ori][j][l];
                        }
                        break;
                    case 6:
                        if(piece6[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece6[cur_ori][j][l];
                        }
                        break;
                    case 7:
                        if(piece7[cur_ori][j][l] != 0)
                        {
                            fixed_board[i][k] = piece7[cur_ori][j][l];
                        }
                        break;
                }
            }
        }

        makeNew();
    }

    public void makeObstacle(int num)
    {
        int a=0, b=9;

        for(int i=0;i<board.length;i++)
        {
            if(i >= board.length - num) {
                    a = random.nextInt(4);
                    b = random.nextInt(4)+5;
            }
            for(int j=0;j<board[0].length;j++)
            {
                if(i < board.length - num)
                {
                    board[i][j] = fixed_board[i + num][j];
                    fixed_board[i][j] = fixed_board[i + num][j];
                }
                else
                {
                    if(j != a && j != b) {
                        board[i][j] = 8;
                        fixed_board[i][j] = 8;
                    }
                    else
                    {
                        board[i][j] = 0;
                        fixed_board[i][j] = 0;
                    }
                }
            }
        }

        for(int i=cur_row,k=0;i<cur_row+4;i++,k++)
        {
            for(int j=cur_col,l=0;j<cur_col+3;j++,l++)
            {
                    switch(cur_piece)
                    {
                        case 1:
                            if(piece1[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece1[cur_ori][k][l];
                            }
                            break;
                        case 2:
                            if(piece2[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece2[cur_ori][k][l];
                            }
                            break;
                        case 3:
                            if(piece3[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece3[cur_ori][k][l];
                            }
                            break;
                        case 4:
                            if(piece4[cur_ori][k][l]!= 0)
                            {
                                board[i][j] = piece4[cur_ori][k][l];
                            }
                            break;
                        case 5:
                            if(piece5[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece5[cur_ori][k][l];
                            }
                            break;
                        case 6:
                            if(piece6[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece6[cur_ori][k][l];
                            }
                            break;
                        case 7:
                            if(piece7[cur_ori][k][l] != 0)
                            {
                                board[i][j] = piece7[cur_ori][k][l];
                            }
                            break;
                    }
            }
        }
    }

    public void actionDown()
    {
        if(!isDownLock) {
            isDownLock = true;
            if (actionCheck(1, 0, 0) == true) {
                for (int i = ((cur_row + 4) > board.length ? (board.length - 1) : (cur_row + 3)), j = i - cur_row; i >= (cur_row > 0 ? cur_row : 0); i--, j--) {
                    for (int k = cur_col, l = 0; k < ((cur_col + 4) > board[0].length ? board[0].length : (cur_col + 4)); k++, l++) {
                        switch (cur_piece) {
                            case 1:
                                if (piece1[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece1[cur_ori][j][l];
                                }
                                break;
                            case 2:
                                if (piece2[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece2[cur_ori][j][l];
                                }
                                break;
                            case 3:
                                if (piece3[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece3[cur_ori][j][l];
                                }
                                break;
                            case 4:
                                if (piece4[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece4[cur_ori][j][l];
                                }
                                break;
                            case 5:
                                if (piece5[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece5[cur_ori][j][l];
                                }
                                break;
                            case 6:
                                if (piece6[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece6[cur_ori][j][l];
                                }
                                break;
                            case 7:
                                if (piece7[cur_ori][j][l] != 0) {
                                    board[i][k] = 0;
                                    board[i + 1][k] = piece7[cur_ori][j][l];
                                }
                                break;
                        }
                    }
                }
                cur_row++;
            } else {
                setBoard();

                int row_num = 0;
                while (isCalc() != 0) {
                    row_num++;
                }

                total_destroy += row_num;

                if (row_num != 0) {
                    score += row_num * 100 + (row_num - 1) * 10;
                    level = score/100 > 50 ? 50:score/100;
                    if(random.nextInt(100) < 10)
                    {
                        isDestroy = true;
                    }
                }
                else if(random.nextInt(1000) < level || isObstacle == true)
                {
                    isObstacle = false;
                    makeObstacle(1);
                }

            }
            isDownLock = false;
        }
    }

    public void actionLeft()
    {
        if(actionCheck(0,-1,0) == true)
        {
            for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
            {
                for(int k=cur_col,l=0;k<((cur_col+4)>board[0].length?board[0].length:(cur_col+4));k++,l++)
                {
                    switch(cur_piece)
                    {
                        case 1:
                            if(piece1[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece1[cur_ori][j][l];
                            }
                            break;
                        case 2:
                            if(piece2[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece2[cur_ori][j][l];
                            }
                            break;
                        case 3:
                            if(piece3[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece3[cur_ori][j][l];
                            }
                            break;
                        case 4:
                            if(piece4[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece4[cur_ori][j][l];
                            }
                            break;
                        case 5:
                            if(piece5[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece5[cur_ori][j][l];
                            }
                            break;
                        case 6:
                            if(piece6[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece6[cur_ori][j][l];
                            }
                            break;
                        case 7:
                            if(piece7[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k-1] = piece7[cur_ori][j][l];
                            }
                            break;
                    }
                }
            }
            cur_col--;
        }
    }

    public void actionRight()
    {
        if(actionCheck(0,1,0) == true)
        {
            for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
            {
                for(int k=((cur_col+4)>board[0].length?(board[0].length-1):(cur_col+3)),l=(board[0].length-cur_col-1)>3?3:(board[0].length-cur_col-1);k>=cur_col;k--,l--)
                {
                    switch(cur_piece)
                    {
                        case 1:
                            if(piece1[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece1[cur_ori][j][l];
                            }
                            break;
                        case 2:
                            if(piece2[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece2[cur_ori][j][l];
                            }
                            break;
                        case 3:
                            if(piece3[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece3[cur_ori][j][l];
                            }
                            break;
                        case 4:
                            if(piece4[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece4[cur_ori][j][l];
                            }
                            break;
                        case 5:
                            if(piece5[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece5[cur_ori][j][l];
                            }
                            break;
                        case 6:
                            if(piece6[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece6[cur_ori][j][l];
                            }
                            break;
                        case 7:
                            if(piece7[cur_ori][j][l] != 0)
                            {
                                board[i][k] = 0;
                                board[i][k+1] = piece7[cur_ori][j][l];
                            }
                            break;
                    }
                }
            }
            cur_col++;
        }
    }

    public void actionRotate()
    {
        if(actionCheck(0,0,1) == true)
        {
            for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
            {
                for(int k=cur_col,l=0;k<((cur_col+4)>board[0].length?board[0].length:(cur_col+4));k++,l++)
                {
                    board[i][k] = fixed_board[i][k];
                    switch(cur_piece)
                    {
                        case 1:
                            if(piece1[(cur_ori+1)%4][j][l] != 0)
                            {
                                board[i][k] = piece1[(cur_ori+1)%4][j][l];
                            }
                            break;
                        case 2:
                            if(piece2[(cur_ori+1)%2][j][l] != 0)
                            {
                                board[i][k] = piece2[(cur_ori+1)%2][j][l];
                            }
                            break;
                        case 3:
                            if(piece3[(cur_ori+1)%2][j][l] != 0)
                            {
                                board[i][k] = piece3[(cur_ori+1)%2][j][l];
                            }
                            break;
                        case 4:
                            if(piece4[(cur_ori+1)%4][j][l] != 0)
                            {
                                board[i][k] = piece4[(cur_ori+1)%4][j][l];
                            }
                            break;
                        case 5:
                            if(piece5[(cur_ori+1)%4][j][l] != 0)
                            {
                                board[i][k] = piece5[(cur_ori+1)%4][j][l];
                            }
                            break;
                        case 6:
                            if(piece6[(cur_ori+1)%1][j][l] != 0)
                            {
                                board[i][k] = piece6[(cur_ori+1)%1][j][l];
                            }
                            break;
                        case 7:
                            if(piece7[(cur_ori+1)%2][j][l] != 0)
                            {
                                board[i][k] = piece7[(cur_ori+1)%2][j][l];
                            }
                            break;
                    }
                }
            }

            switch(cur_piece)
            {
                case 1:
                    cur_ori = (cur_ori+1)%4;
                    break;
                case 2:
                    cur_ori = (cur_ori+1)%2;
                    break;
                case 3:
                    cur_ori = (cur_ori+1)%2;
                    break;
                case 4:
                    cur_ori = (cur_ori+1)%4;
                    break;
                case 5:
                    cur_ori = (cur_ori+1)%4;
                    break;
                case 6:
                    cur_ori = (cur_ori+1)%1;
                    break;
                case 7:
                    cur_ori = (cur_ori+1)%2;
                    break;
            }
        }
    }

    private boolean actionCheck(int row_change, int col_change, int ori_change)
    {
        int num = 0;

        for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
        {
            for(int k=cur_col,l=0;k<((cur_col+4)>board[0].length?board[0].length:(cur_col+4));k++,l++)
            {
                switch(cur_piece)
                {
                    case 1:
                        if(piece1[(cur_ori+ori_change)%4][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 2:
                        if(piece2[(cur_ori+ori_change)%2][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 3:
                        if(piece3[(cur_ori+ori_change)%2][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 4:
                        if(piece4[(cur_ori+ori_change)%4][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 5:
                        if(piece5[(cur_ori+ori_change)%4][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 6:
                        if(piece6[(cur_ori+ori_change)%1][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                    case 7:
                        if(piece7[(cur_ori+ori_change)%2][j][l] != 0)
                        {
                            if(checkRange(i+row_change, k+col_change))
                            {
                                if(fixed_board[i+row_change][k+col_change] == 0)
                                    num++;
                            }
                        }
                        break;
                }
            }
        }

        if(num == 4)
            return true;
        else
            return false;
    }

    private boolean checkRange(int row, int col)
    {
        if(row >= 0 && row < board.length)
        {
            if(col >= 0 && col < board[0].length)
                return true;
        }

        return false;
    }

    private boolean EndCheck()
    {
        for(int i=0;i<board[0].length;i++)
        {
            if(board[0][i] != 0)
                return false;
        }

        return true;
    }

    private int isCalc()
    {
        int find_row=-1;
        int flag;

        // find complete row
        for(int j=board.length-1;j>=0;j--)
        {
            flag = 1;
            for(int i=0;i<board[0].length;i++)
            {
                if(fixed_board[j][i] == 0)
                    flag = 0;
            }

            if(flag == 1)
            {
                find_row = j;
                break;
            }
        }

        // delete complete row
        for(int j=find_row;j>=1;j--)
        {
            for(int i=0;i<board[0].length;i++)
            {
                fixed_board[j][i] = fixed_board[j-1][i];
            }
        }

        // set board
        for(int j=0;j<board.length;j++)
        {
            for(int i=0;i<board[0].length;i++)
            {
                board[j][i] = fixed_board[j][i];
            }
        }
        for(int i=((cur_row+4)>board.length?(board.length-1):(cur_row+3)),j=i-cur_row;i>=(cur_row>0?cur_row:0);i--,j--)
        {
            for(int k=cur_col,l=0;k<((cur_col+4)>board[0].length?board[0].length:(cur_col+4));k++,l++)
            {
                switch(cur_piece)
                {
                    case 1:
                        if(piece1[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece1[cur_ori][j][l];
                        }
                        break;
                    case 2:
                        if(piece2[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece2[cur_ori][j][l];
                        }
                        break;
                    case 3:
                        if(piece3[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece3[cur_ori][j][l];
                        }
                        break;
                    case 4:
                        if(piece4[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece4[cur_ori][j][l];
                        }
                        break;
                    case 5:
                        if(piece5[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece5[cur_ori][j][l];
                        }
                        break;
                    case 6:
                        if(piece6[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece6[cur_ori][j][l];
                        }
                        break;
                    case 7:
                        if(piece7[cur_ori][j][l] != 0)
                        {
                            board[i][k] = piece7[cur_ori][j][l];
                        }
                        break;
                }
            }
        }

        if(find_row == -1)
            return 0;
        else
            return 1;
    }

    public void EnemyUpdate(int[][] a)
    {
        for(int i=0;i<board[0].length;i++)
        {
            for(int j=0;j<board.length;j++)
            {
                enemy_board[j][i] = a[j][i];
            }
        }
    }

    public void HoldBlock()
    {
        int mask = 0;

        if(holdCount > 0 && hold_piece == -1 )
        {
            holdCount--;

            hold_piece = next_piece;
            hold_ori = next_ori;

            for(int j=0;j<4;j++)
            {
                for(int i=0;i<4;i++)
                {
                    switch(hold_piece)
                    {
                        case 1:
                            mask = piece1[hold_ori][j][i];
                            break;
                        case 2:
                            mask = piece2[hold_ori][j][i];
                            break;
                        case 3:
                            mask = piece3[hold_ori][j][i];
                            break;
                        case 4:
                            mask = piece4[hold_ori][j][i];
                            break;
                        case 5:
                            mask = piece5[hold_ori][j][i];
                            break;
                        case 6:
                            mask = piece6[hold_ori][j][i];
                            break;
                        case 7:
                            mask = piece7[hold_ori][j][i];
                            break;
                    }

                    hold[j+1][i+1] = mask;
                }
            }
            makeNext();
        }
        else if(hold_piece != -1)
        {
            next_piece =  hold_piece;
            next_ori = hold_ori;
            hold_piece = -1;

            for(int j=0;j<4;j++)
            {
                for(int i=0;i<4;i++)
                {
                    switch(next_piece)
                    {
                        case 1:
                            mask = piece1[next_ori][j][i];
                            break;
                        case 2:
                            mask = piece2[next_ori][j][i];
                            break;
                        case 3:
                            mask = piece3[next_ori][j][i];
                            break;
                        case 4:
                            mask = piece4[next_ori][j][i];
                            break;
                        case 5:
                            mask = piece5[next_ori][j][i];
                            break;
                        case 6:
                            mask = piece6[next_ori][j][i];
                            break;
                        case 7:
                            mask = piece7[next_ori][j][i];
                            break;
                    }

                    next[j+1][i+1] = mask;
                    hold[j+1][i+1] = 0;
                }
            }
        }
    }

    public int[][] getEntireBoard() { return board; }

    public int getBoard(int i, int j) {
        return board[i][j];
    }

    public int getfBoard(int i, int j) {
        return fixed_board[i][j];
    }

    public int getEnemyBoard(int i, int j) { return enemy_board[i][j]; }

    public int getNext(int i, int j) {
        return next[i][j];
    }

    public int getHold(int i, int j) {
        return hold[i][j];
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getHoldCount() { return holdCount; }
}
