package com.alextam.eppageradapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alextam.eppageradapter.common.Advertisement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Alex Tam
 *
 */
public class MainActivity extends Activity implements EpViewPager.EpViewPagerSwitchListener,EpPagerAdapter.EpPagerAdapterListener {
    private EpViewPager mViewPager;
    private EpPagerAdapter epPagerAdapter;

    private List<Advertisement> advertisements = new ArrayList<Advertisement>();

    private TextView tvSwitchTitle;

    /** 焦点图的小圆点数组 **/
    private ImageView[] dotImageViews = null;
    private ImageView dotImage;
    private LinearLayout dotGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

    }


    private void initWidgets()
    {
        dotGroup = (LinearLayout)findViewById(R.id.ll_title_main_frgm);
        tvSwitchTitle = (TextView)findViewById(R.id.tv_title_main_frgm);
        mViewPager = (EpViewPager)findViewById(R.id.viewpager);

        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=2627339065,1560702196&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img1.imgtn.bdimg.com/it/u=3305511864,2395774481&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name标题--1", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=2140357188,1080361013&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("name标题--2", "http://www.google.com.hk/", "http://img0.imgtn.bdimg.com/it/u=2064294465,1841769489&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("标题name--3", "http://www.google.com.hk/", "http://img4.imgtn.bdimg.com/it/u=2302852567,1735733481&fm=21&gp=0.jpg"));
        advertisements.add(new Advertisement("标题name--4", "http://www.google.com.hk/", "http://img3.imgtn.bdimg.com/it/u=469505965,1743632042&fm=21&gp=0.jpg"));

//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.duitang.com/uploads/item/201410/13/20141013065617_Ncamk.jpeg"));
//		advertisements.add(new Advertisement("name", "http://www.google.com.hk/", "http://img5.imgtn.bdimg.com/it/u=55634296,2316889453&fm=21&gp=0.jpg"));

        dotImageViews = new ImageView[advertisements.size()];
        setDotImageViews();

        epPagerAdapter = new EpPagerAdapter(MainActivity.this, advertisements);
        tvSwitchTitle.setText(advertisements.get(0).getName());
        epPagerAdapter.setEpPagerAdapterListener(this);
        mViewPager.setAdapter(epPagerAdapter);
        mViewPager.setCurrentItem(4000);
        mViewPager.setAdvertisementList(advertisements);
        mViewPager.setEpViewPagerSwitchListener(this);
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

    @Override
    public void onEpViewPagerSwitch(int position, List<Advertisement> advertisementList) {
        tvSwitchTitle.setText(advertisementList.get(position).getName());
        for (int i = 0; i < dotImageViews.length; i++) {
            dotImageViews[position].setBackgroundResource(R.drawable.dot_focused);
            if (position != i) {
                dotImageViews[i].setBackgroundResource(R.drawable.dot_normal);
            }
        }
    }

    /**
     * Set dot-image when EpViewPager switchs.
     */
    private void setDotImageViews()
    {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);  // , 1是可选写的
        lp.setMargins(2, 2, 5, 5);
        for (int i = 0; i < advertisements.size(); i++) {
            dotImage = new ImageView(MainActivity.this);
            dotImage.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            dotImage.setPadding(5, 5, 5, 5);
            dotImage.setLayoutParams(lp);
            dotImageViews[i] = dotImage;
            if (i == 0) {
                dotImageViews[i].setBackgroundResource(R.drawable.dot_focused);
            } else {
                dotImageViews[i].setBackgroundResource(R.drawable.dot_normal);
            }
            dotGroup.addView(dotImageViews[i]);
        }
    }

    @Override
    public void onEpPagerAdapterSuccess() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onEpPagerAdapterFail() {
        // TODO Auto-generated method stub

    }



    @Override
    public void onEpPagerAdapterClick(int position) {
        Toast.makeText(MainActivity.this, advertisements.get(position).getName(), Toast.LENGTH_SHORT).show();
    }




}
