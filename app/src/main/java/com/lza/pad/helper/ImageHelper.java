package com.lza.pad.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/16/15.
 */
public class ImageHelper {

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap compressBitmap(Bitmap bmp, int targetWidth, int targetHeight) {

        if (targetWidth == 0 || targetHeight == 0) return bmp;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }

        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, targetWidth * targetHeight);
        //这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inTempStorage = new byte[30 * 1024];
        try {
            is = new ByteArrayInputStream(baos.toByteArray());
            bm = BitmapFactory.decodeStream(is, null, opts);
            return bm;
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * 压缩图片
     *
     * @param tempFile
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap compressBitmap(String tempFile, int targetWidth, int targetHeight) {
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;
        bfOptions.inPurgeable = true;
        bfOptions.inTempStorage = new byte[30 * 1024];
        bfOptions.inJustDecodeBounds = true;

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fs = null;
        }
        Bitmap bmp = null;
        if (fs != null) {
            try {
                bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                bfOptions.inSampleSize = computeSampleSize(bfOptions, -1, targetWidth * targetHeight);
                bfOptions.inJustDecodeBounds = false;
                return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bmp;
    }

}
