package com.talon.screen.quick.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class BitmapUtils {

    /**
     * 压缩图片 （压缩后不代表实际大小，有差异）
     *
     * @param bitmap    被压缩的图片
     * @param sizeLimit 大小限制  单位 k
     * @return 压缩后的图片
     */
    public static Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        // 循环判断压缩后图片是否超过限制大小
        while (baos.toByteArray().length / 1024 > sizeLimit) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }
        return BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
    }


    /**
     * Bitmap 转二进制
     * @param bitmap
     * @return
     */
    public static byte[] getByteBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] datas = baos.toByteArray();
        return datas;
    }

    /**
     * 二进制数组 转 Bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap decodeImg(byte[] data) {
        Bitmap bitmap = null;

        byte[] imgByte = null;
        InputStream input = null;
        try {
            if (data == null)
                return null;
            imgByte = data;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            input = new ByteArrayInputStream(imgByte);
            SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
            bitmap = (Bitmap) softRef.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imgByte != null) {
                imgByte = null;
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

}
