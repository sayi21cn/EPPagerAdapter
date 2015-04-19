package com.alextam.eppageradapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alextam.eppageradapter.common.AdvFilePathInfo;
import com.alextam.eppageradapter.common.Advertisement;
import com.alextam.eppageradapter.common.BitmapUtil;
import com.alextam.eppageradapter.common.FileUtil;
import com.alextam.eppageradapter.common.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alex Tam on 2015/4/19.
 *<br>
 * 1>This EpPagerAdapter,subclass of PagerAdapter,can download image file via internet automatically
 * 		when you create it and send List <{@link Advertisement}> list in it.
 * 	 当在用户创建EpPagerAdapter的时候,传递List <{@link Advertisement}> list 参数进来,这个作为PagerAdapter的子类的
 * 	EpPagerAdapter对象会自动下载(焦点)图文件.
 * <br>
 * 2>And those image-files which had been downloaded would be saved in storage thus being used next time.
 * 	 下载完成的图片文件会被保存在本地种,(方便)下次重新复用.
 * <br>
 * 3>If one of those image-files is deleted when we need it in PagerAdapter,it could be downloaded and saved again.
 * 	 当PagerAdapter需要使用某个图片文件时,如果它已经被删除,它将再次被下载并保存在本地.
 * <br>
 * 4>It is intended to read a image file which exists in storage.If fail,it would go to download it.
 *  当某个图片文件在存储路径已经存在,则优先获取本地的图片,如果不存在,则下载.
 */
public class EpPagerAdapter extends PagerAdapter {
    private Context mContext;
    private MyApplication myApplication;

    /** 焦点图的图片队列 **/
    private List<ImageView> imageViewList = new ArrayList<ImageView>();

    /** 焦点图实体队列 **/
    private List<Advertisement> advertisementList = new ArrayList<Advertisement>();

    /**焦点图文件信息队列**/
    private List<AdvFilePathInfo> advPathInfoList = new ArrayList<AdvFilePathInfo>();

    /**焦点图文件信息散列表**/
    private Map<String, AdvFilePathInfo> advPathInfoMap = new HashMap<String, AdvFilePathInfo>();

    /** 拼接焦点图文件信息(用于保存) **/
    private StringBuilder advPathInfoSBuilder;

    /** 监听图片就绪数量 **/
    private int keyNumber = 0;

    /** 是否完成所有图片的就绪 **/
    private boolean ifFinishAllImages = false;

    private Set<DownLoadTask> downLoadTasks = new HashSet<DownLoadTask>();;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");




    public EpPagerAdapter(Context context,List<Advertisement> advertisementList)
    {
        this.mContext = context;
        this.advertisementList = advertisementList;
        initImageViewList();
        if(FileUtil.initFolderSystem())
        {
            catchAdvImages();
        }
        else
        {	//由于空间不足或者无法建立存储空间,则不允许下载
            Log.e("非法下载", "空间不足,或无法获取保存路径");
        }
    }

    /**
     * 设置默认的
     */
    private void initImageViewList()
    {
        //清除可能的旧缓存
        if(imageViewList != null && imageViewList.size() > 0)
        {
            for(int count = imageViewList.size()-1; count >=0; count --)
            {
                imageViewList.get(count).setImageBitmap(null);
            }
            imageViewList.clear();
        }

        if(advertisementList != null && advertisementList.size() > 0)
        {
            for(int i=0; i<advertisementList.size(); i++)
            {
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(R.drawable.pic_default);
                imageViewList.add(imageView);
            }
        }
    }


    /**
     * 更新队列中的ImageView
     * @param position	显示的ImageView
     * @param bmp 下载后的图片
     */
    public void setImageView(int position,Bitmap bmp)
    {
        imageViewList.get(position).setImageBitmap(bmp);
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    @Override
    public void destroyItem(View arg0,int position,Object arg2)
    {
        ((ViewPager) arg0).removeView(imageViewList.get(position % imageViewList.size()));
    }

    @Override
    public Object instantiateItem(View view, int position)
    {
        if(imageViewList != null && imageViewList.size() <= 3)
        {
            if (imageViewList.get(position % imageViewList.size()).getParent() != null) {
                //由于用于填充的view的数量只有3个,而ViewPager会预加载前后各一页,第二个会重复添加在同一个parent,
                //因为定义了pager是无限的大小,因此当滑动到第二页的时候,第一页和第三页会添加同一个child到parent中,这样会报错
                ((ViewPager) imageViewList.get(position % imageViewList.size())
                        .getParent()).removeView(imageViewList.get(position
                        % imageViewList.size()));
            }
        }
        //计算当前显示的itemview
        int index = position % imageViewList.size();
        ((ViewPager) view).addView(imageViewList.get(index), 0);
        return imageViewList.get(index);
    }


    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1)
    {

    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

    @Override
    public void startUpdate(View arg0)
    {
    }

    /**
     * 先去本地查找该图片,如果不存在就开启线程下载焦点图
     */
    private void catchAdvImages()
    {
        StringBuffer advStr = new StringBuffer();
        Advertisement entity;
        for(int i=0; i<advertisementList.size(); i++){
            entity = advertisementList.get(i);
            //这里仅提供一种思路,简单的实现方式. 具体格式和保存方式可根据需求来实现.
            //格式: 焦点图名@@跳转链接地址@@焦点图下载地址&&&焦点图名@@跳转链接地址@@焦点图下载地址...
            if(i == 0){
                advStr.append(entity.getName()+"@@"+entity.getUrl()+"@@"+entity.getImageUrl());
            }else{
                advStr.append("&&&" + entity.getName()+"@@"+entity.getUrl()+"@@"+entity.getImageUrl());
            }
        }

        myApplication = (MyApplication)mContext.getApplicationContext();

        String advPathInfoStr = myApplication.getAdvPathInfo();
        //将本地存储的文件信息提取出来
        if(advPathInfoStr != null && !advPathInfoStr.trim().equals(""))
        {
            clearList(advPathInfoList);
            String[] pathArr = advPathInfoStr.split("&&&");
            String[] pathInfoArr;
            for(int p=0;p<pathArr.length;p++){
                AdvFilePathInfo pathEntity = new AdvFilePathInfo();
                pathInfoArr = pathArr[p].split("@@");

                try {
                    if(pathInfoArr.length == 3){
                        pathEntity.setFileName(pathInfoArr[1]);
                    }else{
                        pathEntity.setFileName("null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pathEntity.setFileName("null");
                }
                pathEntity.setFilePath(pathInfoArr[0]);
                pathEntity.setFileDownUrl(pathInfoArr[2]);

                advPathInfoMap.put(pathInfoArr[2], pathEntity);
                advPathInfoList.add(pathEntity);
            }
        }

        if(advStr.toString().equals(myApplication.getAdvertiseStr()))
        {	//数据和本地存储的数据完全相同,直接从本地获取图片
            if(advertisementList.size() == advPathInfoList.size())
            {
                updateListByDefault(advPathInfoList);
            }
            else
            {
                Log.e("EpPagerAdapter error", "advPathInfoList.size() can not match advertisementList.size().");
            }
        }
        else
        {
            //更新本地的焦点图信息
            myApplication.setAdvertiseStr(advStr.toString());
            //置空本地的焦点图文件信息
            myApplication.setAdvPathInfo("");
            updateListByDownLoad();
        }

    }


    public final void clearList(List list)
    {
        if(list != null && list.size() > 0)
            list.clear();
    }

    /**
     * 当数据无变化,调用该方法更新
     * @param list
     */
    private void updateListByDefault(List<AdvFilePathInfo> list)
    {
        if(list.size() == imageViewList.size())
        {
            advPathInfoSBuilder = new StringBuilder();
            String key;
            for(int i=0; i<list.size(); i++)
            {
                try {
                    if(advPathInfoMap != null && advPathInfoMap.containsKey(advertisementList.get(i).getImageUrl()))
                    {
                        key = advertisementList.get(i).getImageUrl();
                        if(!advPathInfoMap.get(key).getFilePath().equals("null")
                                && new File(advPathInfoMap.get(key).getFilePath()).exists())
                        {
                            Bitmap bitmap = BitmapFactory.decodeFile(advPathInfoMap.get(key).getFilePath());
                            imageViewList.get(i).setImageBitmap(bitmap);
                            //重新保存数据
                            appendAdvPathInfoSBuilder(advPathInfoSBuilder, advPathInfoMap.get(key).getFilePath(),
                                    advPathInfoMap.get(key).getFileName(), advPathInfoMap.get(key).getFileDownUrl());
                            setAdapterState();
                        }
                        else
                        {	//原先存储的文件已经不存在,则重新下载
                            imageViewList.get(i).setImageResource(R.drawable.pic_default);

                            DownLoadTask task = new DownLoadTask(imageViewList.get(i), key,i);
                            downLoadTasks.add(task);
                            task.execute(key);
                        }
                    }
                    else
                    {
                        imageViewList.get(i).setImageResource(R.drawable.pic_default);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    imageViewList.get(i).setImageResource(R.drawable.pic_default);
                }
            }
        }
        else
        {
            //图片本地数据和服务器的冲突,导致下载失败
            Log.e("EpPagerAdapter error", "the data used to download can not match server's data.");
        }
    }

    /**
     * 通过下载更新图片
     */
    private void updateListByDownLoad()
    {
        //存放已有的焦点图链接
        advPathInfoSBuilder = new StringBuilder();

        String imageDownUrl;
        for(int i=0; i< advertisementList.size(); i++)
        {
            imageDownUrl = advertisementList.get(i).getImageUrl();
            //检索本地的焦点图文件信息中是否包含已有的焦点图,这样就不重复下载
            if(advPathInfoMap != null && advPathInfoMap.containsKey(advertisementList.get(i).getImageUrl()))
            {
                //尝试set入图片
                try {
                    if(!advPathInfoMap.get(imageDownUrl).getFilePath().equals("null")
                            && new File(advPathInfoMap.get(imageDownUrl).getFilePath()).exists())
                    {	//先尝试读取本地图片,否则下载
                        Bitmap bitmap =
                                BitmapFactory.decodeFile(advPathInfoMap.get(imageDownUrl).getFilePath());
                        imageViewList.get(i).setImageBitmap(bitmap);

                        appendAdvPathInfoSBuilder(advPathInfoSBuilder, advPathInfoMap.get(imageDownUrl).getFilePath(),
                                advPathInfoMap.get(imageDownUrl).getFileName(), advPathInfoMap.get(imageDownUrl).getFileDownUrl());
//						if(advPathInfoSBuilder == null || advPathInfoSBuilder.length() < 1)
//						{
//							advPathInfoSBuilder.append(advPathInfoMap.get(imageDownUrl).getFilePath() +"@@"
//									+ advPathInfoMap.get(imageDownUrl).getFileName() + "@@"+ advPathInfoMap.get(imageDownUrl).getFileDownUrl());
//						}
//						else
//						{
//							advPathInfoSBuilder.append("&&&" + advPathInfoMap.get(imageDownUrl).getFilePath() +"@@"
//									+ advPathInfoMap.get(imageDownUrl).getFileName() + "@@"+ advPathInfoMap.get(imageDownUrl).getFileDownUrl());
//						}
                        setAdapterState();
                    }
                    else
                    {
                        imageViewList.get(i).setImageResource(R.drawable.pic_default);
                        DownLoadTask task = new DownLoadTask(imageViewList.get(i), imageDownUrl,i);
                        downLoadTasks.add(task);
                        task.execute(imageDownUrl);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    imageViewList.get(i).setImageResource(R.drawable.pic_default);
                }
            }
            else
            {
                DownLoadTask task = new DownLoadTask(imageViewList.get(i), imageDownUrl,i);
                downLoadTasks.add(task);
                task.execute(imageDownUrl);
            }
        }
    }

    /**
     * 下载器
     * @author Alex Tam
     *
     */
    private class DownLoadTask extends AsyncTask<String, Void, Bitmap>
    {
        private ImageView imageView;
        private String imageDownUrl;
        private int position;

        public DownLoadTask(ImageView imageView,String imageDownUrl,int position )
        {
            this.imageView = imageView;
            this.imageDownUrl = imageDownUrl;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            String downloadUrl = (String)params[0];

            try {
                //下载
                byte[] datas = getBytesOfBitMap(downloadUrl);
                if(datas != null)
                {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(datas, 0, datas.length, opts);
                    opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, 500 * 500);
                    opts.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length, opts);

                    return bitmap;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            String fileName = dateFormat.format(new Date(System.currentTimeMillis())) +"_" + position;
            if(bitmap != null)
            {
                imageView.setImageBitmap(bitmap);

                //保存至本地
                try {
                    String filePath = FileUtil.saveAdvPicture(mContext, bitmap, fileName);

                    appendAdvPathInfoSBuilder(advPathInfoSBuilder, filePath, fileName, imageDownUrl);
                    setAdapterState();

                }
                catch (IOException e) {
                    e.printStackTrace();
                    appendAdvPathInfoSBuilder(advPathInfoSBuilder, "null-filepath", fileName, imageDownUrl);
                    setAdapterState();
                }

            }
            else
            {
                //下载失败则显示默认图片
                imageView.setImageResource(R.drawable.pic_default);
                appendAdvPathInfoSBuilder(advPathInfoSBuilder, "null-filepath", fileName, imageDownUrl);
                setAdapterState();
            }
        }

    }

    /**
     * 获取下载图片并转为byte[]
     * @param imgUrl
     * @return
     */
    private byte[] getBytesOfBitMap(String imgUrl){
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);  //10s
            conn.setReadTimeout(20 * 1000);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream in = conn.getInputStream();
            return FileUtil.readStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 监听图片就绪状态
     */
    private void setAdapterState()
    {
        keyNumber ++;
        if(keyNumber >= imageViewList.size())
        {	//所有图片都就绪
            ifFinishAllImages = true;
            Log.i("EpPagerAdapter Success", "All pictures were successfully saved in storage.");
            myApplication.setAdvPathInfo(advPathInfoSBuilder.toString());
            deleteCacheADS();
        }
    }


    /**
     * 停止所有下载任务
     */
    public void cancelAllTask()
    {
        if(downLoadTasks != null){
            for(DownLoadTask task : downLoadTasks)
            {
                task.cancel(false);
            }
        }
    }

    /**
     * 该方法获取所有焦点图是否到位就绪
     * @return
     */
    public boolean getAdapterState()
    {
        return ifFinishAllImages;
    }

    /**
     * 当Activity跳转时,想停止后台下载的任务,可调用该方法
     */
    public void stop()
    {
        cancelAllTask();
    }

    /**
     * 可调用该方法检测当前焦点图是否全部就绪,如果没有,则将还没有下载的图片进行下载
     */
    public void start()
    {
        catchAdvImages();
    }

    private void appendAdvPathInfoSBuilder(StringBuilder advPathInfoSBuilder,String filePath,
                                           String fileName, String fileDownUrl)
    {
        if(advPathInfoSBuilder == null || advPathInfoSBuilder.length() < 1)
        {
            advPathInfoSBuilder.append(filePath +"@@" + fileName + "@@"+ fileDownUrl);
        }
        else
        {
            advPathInfoSBuilder.append("&&&" + filePath +"@@" + fileName + "@@"+ fileDownUrl);
        }
    }

    /**
     * 清理多余的图片文件
     */
    public void deleteCacheADS()
    {
        try {
            File rootFile = new File(FileUtil.getSavePath() + FileUtil.SAVE_ROOT_FULL_PATH);
            if(rootFile.exists())
            {
                File[] filelists = rootFile.listFiles();
                if(filelists != null && filelists.length > 0)
                {
                    if(myApplication != null)
                    {
                        List<String> realFilePaths = new ArrayList<String>();
                        String advPathInfoStr = myApplication.getAdvPathInfo();
                        if(advPathInfoStr != null && !advPathInfoStr.trim().equals(""))
                        {
                            String[] pathArr = advPathInfoStr.split("&&&");
                            String[] pathInfoArr;
                            for(int p=0;p<pathArr.length;p++){
                                pathInfoArr = pathArr[p].split("@@");
                                realFilePaths.add(pathInfoArr[0]);
                                Log.i("打印读取map数据", " filePath = "+pathInfoArr[0] + "  fileName = "+pathInfoArr[1] + "  imageDownUrl = "+pathInfoArr[2]);
                            }

                            if(realFilePaths != null && realFilePaths.size() > 0)
                            {
                                for(int i=0;i<filelists.length;i++)
                                {
                                    if(realFilePaths.indexOf(filelists[i].getPath()) < 0)
                                    {
                                        if(new File(filelists[i].getPath()).exists())
                                        {
                                            new File(filelists[i].getPath()).delete();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
