package com.example.lunaticlemon.tetris;

/**
 * Created by lemon on 2017-06-23.
 */

public class MessageProtocol{
    public static final byte READY = 0;
    public static final byte BOARD_CHANGE = 1;
    public static final byte GAME_END = 2;
    public static final byte RECONNECT = 3;
    public static final byte DISCONNECT = 4;
    public static final byte ACK = 5;
    public static final byte OBSTACLE = 6;
    public static final byte EMOTICON = 7;
}
