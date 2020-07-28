package com.talon.screen.quick.network;

import android.graphics.Bitmap;

import com.talon.screen.quick.util.BitmapUtils;
import com.talon.screen.quick.util.LogWrapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.SocketException;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author by Talon, Date on 2020-04-13.
 * note: websocket 客户端
 */
public class MWebSocketClient extends WebSocketClient {

    private final String TAG = "MWebSocketClient";

    private boolean mIsConnected = false;
    private CallBack mCallBack;

    public MWebSocketClient(URI serverUri, CallBack callBack) {
        super(serverUri);
        this.mCallBack = callBack;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        LogWrapper.e(TAG, "onOpen");
        updateClientStatus(true);

        try {
            getSocket().setReceiveBufferSize(5 * 1024 * 1024);
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (mCallBack != null)
            mCallBack.onBitmapReceived(BitmapUtils.decodeImg(buf));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        updateClientStatus(false);
    }

    @Override
    public void onError(Exception ex) {
        updateClientStatus(false);
    }

    private void updateClientStatus(boolean isConnected) {

        mIsConnected = isConnected;
        LogWrapper.d(TAG, "mIsConnected:" + mIsConnected);
        // 回调
        if (mCallBack != null)
            mCallBack.onClientStatus(isConnected);
    }

    public boolean isConnected() {
        LogWrapper.d(TAG, "mIsConnected:" + mIsConnected);
        return mIsConnected;
    }

    public interface CallBack {
        void onClientStatus(boolean isConnected);

        void onBitmapReceived(Bitmap bitmap);
    }


}
