package com.example.myapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtil {
    private static final String COVER_IMAGE_DIR = "novel_covers";
    
    /**
     * 保存图片到应用私有目录
     */
    public static String saveCoverImage(Context context, Bitmap bitmap) {
        try {
            // 创建文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "novel_cover_" + timeStamp + ".jpg";
            
            // 获取应用私有目录
            File dir = new File(context.getFilesDir(), COVER_IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从URI加载图片
     */
    public static Bitmap loadBitmapFromUri(Context context, Uri uri) {
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从文件路径加载图片
     */
    public static Bitmap loadBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }
}