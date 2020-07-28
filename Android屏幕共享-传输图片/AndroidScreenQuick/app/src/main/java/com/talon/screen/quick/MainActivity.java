package com.talon.screen.quick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.talon.screen.quick.network.MWebSocketServer;
import com.talon.screen.quick.util.BitmapUtils;
import com.talon.screen.quick.util.IPUtils;
import com.talon.screen.quick.util.LogWrapper;
import com.talon.screen.quick.util.ScreenShotHelper;
import com.talon.screen.quick.util.ThreadPool;

/**
 * @author by talon, Date on 2020/6/20.
 * note: 主界面
 */
public class MainActivity extends AppCompatActivity implements ScreenShotHelper.OnScreenShotListener {

    private final String TAG = "btn_quick";
    private static final int REQUEST_MEDIA_PROJECTION = 100;

    private TextView tv_ip;

    private MWebSocketServer webSocketServer;
    private boolean socketIsStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_ip = findViewById(R.id.tv_ip);

        webSocketServer = new MWebSocketServer(Config.ANDROID_SERVER_PORT, new MWebSocketServer.CallBack() {
            @Override
            public void onServerStatus(boolean isStarted) {
                socketIsStarted = isStarted;
            }
        });
        webSocketServer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_ip.setText(String.format("当前IP：%s", IPUtils.getIpAddressString()));
    }

    /**
     * 推送端：1. 开启服务  2. 申请截图权限  3. 传输数据
     *
     * @param view
     */
    public void StartQuick(View view) {
        if (!socketIsStarted)
            Toast.makeText(this, "socket 服务启动异常！", Toast.LENGTH_SHORT).show();
        else
            tryStartScreenShot();
    }

    /**
     * 播放端：1. 输入IP  2. 接收到数据  3. 展示
     *
     * @param view
     */
    public void Join(View view) {
        showEditDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && data != null) {
            if (resultCode == RESULT_OK) {
                // 截屏的回调
                ScreenShotHelper screenShotHelper = new ScreenShotHelper(this, resultCode, data, this);
                screenShotHelper.startScreenShot();
            } else if (resultCode == RESULT_CANCELED) {
                LogWrapper.d(TAG, "用户取消");
            }
        }
    }

    /**
     * 申请截屏权限
     */
    private void tryStartScreenShot() {
        MediaProjectionManager mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mProjectionManager != null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onShotFinish(Bitmap bitmap) {
        LogWrapper.d(TAG, "bitmap:" + bitmap.getWidth());
        webSocketServer.sendBytes(BitmapUtils.getByteBitmap(bitmap));
    }

    private void showEditDialog() {
        final EditText editText = new EditText(this);
        editText.setText("192.168.2.112");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Server").setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String host = editText.getText().toString();
                if (!TextUtils.isEmpty(host)) {
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    intent.putExtra("host", host);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
