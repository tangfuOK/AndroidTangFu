package com.talon.screen.quick.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import com.talon.screen.quick.Config;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * 截屏的封装类，在onActivityResult中调用
 */
public class ScreenShotHelper {

    private final String TAG = "ScreenShotHelper";

    private int mImageWidth;
    private int mImageHeight;
    private int mScreenDensity;

    public interface OnScreenShotListener {
        void onShotFinish(Bitmap bitmap);
    }

    public static ImageReader mImageReader; // 比较特殊，需要其它类持有这个对象，才不会被回收。
    private OnScreenShotListener mOnScreenShotListener;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private final SoftReference<Context> mRefContext;

    public ScreenShotHelper(Context context, int resultCode, Intent data, OnScreenShotListener onScreenShotListener) {
        this.mOnScreenShotListener = onScreenShotListener;
        this.mRefContext = new SoftReference<Context>(context);
        getScreenBaseInfo();
        mMediaProjection = getMediaProjectionManager().getMediaProjection(resultCode, data);
//        mImageReader = ImageReader.newInstance(getScreenWidth(), getScreenHeight(), PixelFormat.RGBA_8888, 1);
        mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, 0x01, 2);
    }

    /**
     * 获取屏幕相关数据
     */
    private void getScreenBaseInfo() {
        mImageWidth = (int) (ScreenUtils.getScreenWidth(getContext()) * Config.IMAGE_SCALE);
        mImageHeight = (int) (ScreenUtils.getScreenHeight(getContext()) * Config.IMAGE_SCALE);
        mScreenDensity = ScreenUtils.getScreenDensityDpi(getContext());
    }

    /**
     * 开始截屏
     */
    public void startScreenShot() {
        createVirtualDisplay();
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);
    }


    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private void createVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mImageWidth,
                mImageHeight,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null
        );
    }

    private Context getContext() {
        return mRefContext.get();
    }


    /**
     * 屏幕发生变化时截取
     */
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            try (Image image = reader.acquireLatestImage()) {
                if (image != null) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;
                    // Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_4444);
                    bitmap.copyPixelsFromBuffer(buffer);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

                    // todo 可以在这里处理Bitmap
                    bitmap = BitmapUtils.compressBitmap(bitmap, 100);

                    if (mOnScreenShotListener != null) {
                        if (bitmap != null)
                            mOnScreenShotListener.onShotFinish(bitmap);
                    }
                    image.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}