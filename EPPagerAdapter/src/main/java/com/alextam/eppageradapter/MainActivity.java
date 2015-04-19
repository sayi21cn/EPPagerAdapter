package com.alextam.eppageradapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.alextam.eppageradapter.common.Advertisement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Alex Tam
 *
 */
public class MainActivity extends Activity {
    private ViewPager mViewPager;
    private EpPagerAdapter epPagerAdapter;

    private List<Advertisement> advertisements = new ArrayList<Advertisement>();

    private AtomicInteger TimeChange = new AtomicInteger(4000);

    private boolean isContinue = true;


    private final Handler viewpagerHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(mViewPager != null)
            {
                mViewPager.setCurrentItem(msg.what);
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

    }


    private void initWidgets()
    {
        mViewPager = (ViewPager)findViewById(R.id.viewpager);

//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=2627339065,1560702196&fm=21&gp=0.jpg"));
//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img1.imgtn.bdimg.com/it/u=3305511864,2395774481&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=2140357188,1080361013&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img0.imgtn.bdimg.com/it/u=2064294465,1841769489&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img4.imgtn.bdimg.com/it/u=2302852567,1735733481&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img3.imgtn.bdimg.com/it/u=469505965,1743632042&fm=21&gp=0.jpg"));

//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.duitang.com/uploads/item/201410/13/20141013065617_Ncamk.jpeg"));
//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=55634296,2316889453&fm=21&gp=0.jpg"));

        epPagerAdapter = new EpPagerAdapter(MainActivity.this, advertisements);

        mViewPager.setAdapter(epPagerAdapter);
        mViewPager.setCurrentItem(4000);
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
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
        });

        new Thread() {
            public void run() {
                while (true) {
                    if (isContinue) {
                        try {
                            sleep(3000);	//控制焦点图切换时间间隔
                            TimeChange.incrementAndGet();
                            viewpagerHandler.sendEmptyMessage(TimeChange.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

    private final class MyPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            // 计算当前显示的itemview
            TimeChange.getAndSet(position);
            viewpagerHandler.sendEmptyMessage(position);

            int index = position % advertisements.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
