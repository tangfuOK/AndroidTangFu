package com.talon.screen.quick.network;


import com.talon.screen.quick.util.LogWrapper;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by Talon, Date on 2020-04-13.
 * note: websocket 服务端
 */
public class MWebSocketServer extends WebSocketServer {

    private final String TAG = "MWebSocketServer";

    private boolean mIsStarted = false;
    private CallBack mCallBack;

    private List<WebSocket> mWebSocketList;

    public MWebSocketServer(int port, CallBack callBack) {
        super(new InetSocketAddress(port));
        this.mCallBack = callBack;
        setReuseAddr(true);
        setConnectionLostTimeout(5 * 1000);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake handshake) {
        LogWrapper.d(TAG, "有用户链接");
        if (mWebSocketList == null)
            mWebSocketList = new ArrayList<>();
        mWebSocketList.add(webSocket);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogWrapper.d(TAG, "有用户离开");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogWrapper.e(TAG, "接收到消息：" + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogWrapper.e(TAG, "发生error:" + ex.toString());
    }

    @Override
    public void onStart() {
        updateServerStatus(true);
    }

    /**
     * 停止服务器
     */
    public void socketStop() {
        try {
            super.stop(100);
            updateServerStatus(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送二进制
     *
     * @param bytes
     */
    public void sendBytes(byte[] bytes) {
        if (mWebSocketList == null) return;
        for (WebSocket socket : mWebSocketList)
            socket.send(bytes);
    }

    private void updateServerStatus(boolean isStarted) {
        mIsStarted = isStarted;
        LogWrapper.e(TAG, "mIsStarted:" + mIsStarted);
        // 回调
        if (mCallBack != null)
            mCallBack.onServerStatus(isStarted);
    }

    public boolean isStarted() {
        LogWrapper.e(TAG, "mIsStarted:" + mIsStarted);
        return mIsStarted;
    }

    public interface CallBack {
        void onServerStatus(boolean isStarted);
    }

}
