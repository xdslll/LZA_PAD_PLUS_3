package com.lza.pad.support.file;

import android.os.Environment;
import android.text.TextUtils;

import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 该类提供android系统文件相关的工具类
 *
 * @author Sam
 * @Date 2014-8-16
 */
public class FileTools implements Consts {

    /**
     * 判断文件是否为空，如果为空，则删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteIfEmpty(File file) {
        if(file != null && file.exists() && file.length() == 0) {
            return file.delete();
        }
        return false;
    }

    /**
     * 通过缓存文件路径获取缓存文件对象
     *
     * @param tmpFilePath 缓存文件路径，必须包含文件名
     * @return 缓存文件对象
     */
    public static File getCacheFile(String tmpFilePath) {
        //如果是文件夹则直接返回null
        if(tmpFilePath.endsWith("/"))
            return null;
        //如果包含文件，则提取文件名和目录
        int fileNameIndex = tmpFilePath.lastIndexOf("/");
        String dirName = tmpFilePath.substring(0, fileNameIndex + 1);
        String fileName = tmpFilePath.substring(fileNameIndex + 1);
        //Log.v("TAG", "dirName=" + dirName + ",fileName=" + fileName);
        if(sdCardAvailable()) {
            //获取sd卡目录
            File sdCardDir = Environment.getExternalStorageDirectory();
            //判断缓存目录是否存在，如不存在则创建
            File cacheDir = getDirObject(sdCardDir.getAbsolutePath() + dirName);
            //获取缓存文件对象
            return new File(cacheDir, fileName);
        }else {
            return null;
        }
    }

    public static File createCacheFile(String filePath) {
        if (sdCardAvailable()) {
            String cacheDir = CACHE_IMG_PATH;
            StringBuilder sb = new StringBuilder();
            String sdDir = getSDCardDir().getAbsolutePath();
            sdDir = appendDirSeparator(sdDir);
            cacheDir = appendDirSeparator(cacheDir);
            filePath = appendDirSeparator(filePath);
            sb.append(sdDir).append(cacheDir).append(filePath);
            return getDirObject(sb.toString());
        }
        return null;
    }

    /**
     * 根据指定的路径名称，依次创建所有的文件夹，并返回文件夹对象
     *
     * @param dirName 文件夹路径
     * @return
     */
    public static File getDirObject(String dirName) {
        File cacheDir = new File(dirName);
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 判断sd卡是否可用，包括：
     *  1. 是否连接
     *  2. 是否可以读写
     *
     * @return
     */
    public static boolean sdCardAvailable() {
        //判断sd卡是否连接
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //判断sd卡目录是否可读写
            File sdCardFile = Environment.getExternalStorageDirectory();
            if (sdCardFile.canRead() && sdCardFile.canWrite()) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 获取SD卡的路径
     *
     * @return
     */
    public static File getSDCardDir() {
        if (sdCardAvailable()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    private static boolean copyFile(File originalFile, File newFile) {
        try {
            FileInputStream fis = new FileInputStream(originalFile);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
            fis.close();
            fos.close();
            AppLogger.e("复制[" + originalFile.getAbsolutePath() + "]-->[" +
                    newFile.getAbsolutePath() + "]成功!");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFile(File originalFile, File newFile, boolean ifCover) {
        if (originalFile.exists()) {
            if (newFile.exists()) {
                if (ifCover) {
                    AppLogger.e("覆盖原文件复制!");
                    return copyFile(originalFile, newFile);
                } else {
                    AppLogger.e("复制的文件已经存在!");
                    return false;
                }
            } else {
                return copyFile(originalFile, newFile);
            }
        } else {
            AppLogger.e("原文件不存在!", new FileNotFoundException("原文件不存在!"));
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 在路径的最后加上分隔符
     *
     * @param path
     * @return
     */
    public static String appendDirSeparator(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.endsWith(File.separator)) {
                return path;
            } else {
                return path + File.separator;
            }
        }
        return path;
    }

    public static String appendDirSeparator(File path) {
        return appendDirSeparator(path.getAbsolutePath());
    }

    /**
     * 移除路径第一个分隔符
     *
     * @param path
     * @return
     */
    public static String removeFirstSeparator(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                return path.substring(1);
            }
        }
        return path;
    }

    public static String removeFirstSeparator(File path) {
        return removeFirstSeparator(path.getAbsolutePath());
    }

    /**
     * 将2个文件路径拼接起来
     *
     * @param frontFile
     * @param backFile
     * @return
     */
    public static File stickFile(File frontFile, File backFile) {
        String frontPath = appendDirSeparator(frontFile);
        String backPath = removeFirstSeparator(backFile);
        return new File(frontPath + backPath);
    }

    public static File stickFile(String frontFile, String backFile) {
        frontFile = appendDirSeparator(frontFile);
        backFile = removeFirstSeparator(backFile);
        return new File(frontFile + backFile);
    }

    /**
     * 创建文件夹
     *
     * @param file
     * @return
     */
    public static boolean mkDirs(File file) {
        boolean flag = false;
        if (file != null) {
            flag = file.mkdirs();
        }
        return flag;
    }

    /**
     * 清空指定目录下的所有文件,保留文件夹,慎用
     *
     * @param file
     * @return
     */
    public static boolean delteAllFiles(File file) {
        boolean flag = false;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                flag = file.delete();
            }else if (file.isDirectory()) {
                File[] cacheFiles = file.listFiles();
                for (File tmpFile : cacheFiles) {
                    delteAllFiles(tmpFile);
                }
            }
        }
        return flag;
    }

    /**
     * 清空指定目录下的所有文件和文件夹,慎用
     *
     * @param file
     * @return
     */
    public static boolean delteAllDirsAndFiles(File file) {
        boolean flag = false;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                flag = file.delete();
            }else if (file.isDirectory()) {
                File[] cacheFiles = file.listFiles();
                for (File tmpFile : cacheFiles) {
                    delteAllDirsAndFiles(tmpFile);
                }
                file.delete();
            }
        }
        return flag;
    }

    public static String getFileNameFromUrl(String url) {
        int fileNameIndex = url.lastIndexOf("/");
        return url.substring(fileNameIndex + 1);
    }
}
