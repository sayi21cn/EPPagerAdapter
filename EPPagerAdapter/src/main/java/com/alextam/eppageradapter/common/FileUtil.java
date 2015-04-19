package com.alextam.eppageradapter.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alex Tam on 2015/4/17.
 */
public class FileUtil {
    public final static String SAVE_ROOT_FULL_PATH =  FileUtil.SAVE_ROOT_PATH +
            FileUtil.SAVE_ROOT_PATH_ADS + "/";

    public final static String SAVE_ROOT_PATH = "/EpPagerAdapter";

    public final static String SAVE_ROOT_PATH_ADS = "/Ads";

    /**
     * 获取根存储路径
     * @return
     */
    public static String getSavePath(){
        String savePath = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else{
            File[] files = new File("/mnt/").listFiles();
            if(files.length > 0){
                String filePath = null;
                for(int p=0;p<files.length;p++){
                    if(files[p].isDirectory()){
                        if(files[p].getPath().indexOf("sdcard") >= 0){
                            StatFs st = new StatFs(files[p].getPath());
                            int blocksize = st.getBlockSize();
                            int blockcount = st.getBlockCount();
                            if((blocksize * blockcount) > 0){
                                filePath = files[p].getPath();
                            }
                        }

                    }
                }
                if(filePath != null){
                    savePath = filePath;
                }else{
                    savePath = null;
                }
            }
        }
        return savePath;
    }

    /**
     * 保存焦点图到本地
     * @param bm
     * @param name
     * @throws IOException
     */
    public static String saveAdvPicture(Context context,Bitmap bm,String name) throws IOException {
        String fileName  =  "/"+ name + ".jpg";
        String ALBUM_PATH = getSavePath() + FileUtil.SAVE_ROOT_PATH;

        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){  //若不存在则创建
            dirFile.mkdir();
        }

        String ALBUM_PATH2 = dirFile + FileUtil.SAVE_ROOT_PATH_ADS;
        File dirFile2 = new File(ALBUM_PATH2);
        if(!dirFile2.exists()){
            dirFile2.mkdir();
        }
        File myCaptureFile = new File(dirFile2 + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        MediaScannerConnection.scanFile(context, new String[]{dirFile2 + fileName}, null, null);

        bos.flush();
        bos.close();
        return myCaptureFile.getPath();
    }

    /**
     * 创建保存的文件夹
     */
    public static boolean initFolderSystem()
    {
        boolean ifHaveDownFolder = false;
        String ALBUM_PATH = getSavePath() + FileUtil.SAVE_ROOT_PATH;

        if(ALBUM_PATH != null)
        {
            File dirFile = new File(ALBUM_PATH);
            if (!dirFile.exists()) { // 若不存在则创建
                dirFile.mkdir();
            }

            // 存放首页轮播的图片
            String ALBUM_PATH1 = dirFile + FileUtil.SAVE_ROOT_PATH_ADS;
            File dirFile1 = new File(ALBUM_PATH1);
            if (!dirFile1.exists()) {
                dirFile1.mkdir();
            }
            ifHaveDownFolder = true;
        }
        return ifHaveDownFolder;
    }

    /**
     * InputStream转byte[]
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

}
