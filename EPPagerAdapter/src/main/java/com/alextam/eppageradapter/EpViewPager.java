package com.alextam.eppageradapter;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.alextam.eppageradapter.common.Advertisement;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EpViewPager could switch pictures automatically.
 * <br>EpViewPager能自动切换焦点图.
 * @author Alex Tam
 *
 */
public class EpViewPager extends ViewPager implements View.OnTouchListener {
    private Context mContext;

    /** 计算item的位置 **/
    private AtomicInteger POSITION_SWITCH = new AtomicInteger(4000);

    private List<Advertisement> advertisementList;

    private boolean isContinue = true;

    private EpViewPagerSwitchListener listener;

    private int currentIndex = 0;

    /** item的切换时长 **/
    private int TIME_SWITCH = 4000;

    
    /**
     * EpViewPagerSwitchListener would be used in onPageSelected(int position) of {@link MyPageChangeListener}.
     * <br>EpViewPagerSwitchListener接口会在MyPageChangeListener的onPageSelected(int position)方法中被回调.
     * @author Alex Tam
     *
     */
    public interface EpViewPagerSwitchListener
    {
        void onEpViewPagerSwitch(int position, List<Advertisement> advertisementList);
    }

    private final Handler viewpagerHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(EpViewPager.this != null)
            {
                setCurrentItem(msg.what);
            }
            super.handleMessage(msg);
        }
    };

    public EpViewPager(Context context) {
        this(context,null);
        this.mContext = context;
    }

    public EpViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = getContext();

        setOnPageChangeListener(new MyPageChangeListener());
        setOnTouchListener(this);
        startToSwitch();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isContinue = false;
                break;
            case MotionEvent.ACTION_UP:
                isContinue = true;
                break;
            default:
                isContinue = true;
                break;
        }
        return false;
    }

    private final class MyPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            POSITION_SWITCH.getAndSet(position);
            viewpagerHandler.sendEmptyMessage(position);

            if(advertisementList != null && advertisementList.size() > 0)
            {
                int index = position % advertisementList.size();
                currentIndex = index;
                if(listener != null) listener.onEpViewPagerSwitch(index,advertisementList);
            }
        }
    }

    /**
     * Subclass of {@link Thread}.It could switch the pictures of EpViewPager.
     * 切换线程
     */
    private class SwitchThread extends Thread
    {
        @Override
        public void run()
        {
            while (true) {
                if (isContinue) {
                    try {
                        //控制焦点图切换时间间隔
                        sleep(TIME_SWITCH);
                        POSITION_SWITCH.incrementAndGet();
                        viewpagerHandler.sendEmptyMessage(POSITION_SWITCH.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Set the data-resource of EpViewPager.
     * <br>设置数据源
     * @param advertisementList
     */
    public void setAdvertisementList(List<Advertisement> advertisementList)
    {
        this.advertisementList = advertisementList;
    }

    /**
     * Set the time EpViewPager cost in switching pictures.
     * <br>设置EpViewPager切换图片的时间
     * @param timeValue
     */
    public void setViewpagerChangeTime(int timeValue)
    {
        if(timeValue >= 3000)
        {
            TIME_SWITCH = timeValue;
        }
    }

    /**
     * Start to switch pictures automatically.
     * <br>开始自动切换.
     */
    public void startToSwitch()
    {
        SwitchThread switchThread = new SwitchThread();
        switchThread.start();
    }

    /**
     * Set {@link EpViewPagerSwitchListener} that the method onHandleSwitch() could be used inside EpViewPager.
     * <br>设置EpViewPagerSwitchListener接口,使onHandleSwitch()方法能被调用
     * @param listener
     */
    public void setEpViewPagerSwitchListener(EpViewPagerSwitchListener listener)
    {
        this.listener = listener;
    }




}
