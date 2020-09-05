package com.reciting.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import androidx.annotation.RequiresApi;

/**
 * 设置屏幕的监听状态
 * 因为这个软件的运行需要依靠屏幕开启的信号
 */
public class ScreenListener
{
    private Context context;           //必不可少的东西
    private ScreenBroadcastReceiver myScreenReceiver;    //定义一个广播
    private ScreenStateListener myScreenStateListener;    //定义内部接口


    /**
     *初始化
     */

    public ScreenListener(Context context)
    {
        this.context=context;
        myScreenReceiver =new ScreenBroadcastReceiver();   //初始化广播
    }

    /**
     * 自定义接口
     */

    public interface ScreenStateListener
    {
        void onScreenOn();              //手机屏幕亮起时
        void onScreenOff();             //手机屏幕关闭
        void onUserPresent();           //手机屏幕解锁
    }

    /**
     * 获取screen的状态
     */

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR_MR1)
    private void getScreenState()
    {
        //初始化powerManager
        PowerManager manager= (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        //如果屏幕亮起，屏幕监听器调用onScreenOn()
        if(manager.isScreenOn())
        {
            myScreenStateListener.onScreenOn();
        }
        else
        {
            myScreenStateListener.onScreenOff();
        }
    }


    /**
     *
     * 一个内部广播，用于监听屏幕亮起时，屏幕关闭时，解锁时的状态
     *
     */

    private class ScreenBroadcastReceiver extends BroadcastReceiver
    {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            action = intent.getAction();
            //屏幕亮时操作
            if (Intent.ACTION_SCREEN_ON.equals(action))
                myScreenStateListener.onScreenOn();
                //屏幕关闭时的操作
            else if (Intent.ACTION_SCREEN_OFF.equals(action))
                myScreenStateListener.onScreenOff();
                //屏幕解锁
            else if (Intent.ACTION_USER_PRESENT.equals(action))
                myScreenStateListener.onUserPresent();
        }
    }


        /**
         * 开始监听广播状态
         * @param listener
         */

        public void begin(ScreenStateListener listener)
        {
            myScreenStateListener =listener;
            registerListener();
            getScreenState();
        }


        /**
         * 注册广播接收器
         */
        public void registerListener()
        {
            IntentFilter filter=new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);                    //屏幕亮起时开启的广博
            filter.addAction(Intent.ACTION_SCREEN_OFF);                   //屏幕关闭时开启的广播
            filter.addAction(Intent.ACTION_USER_PRESENT);                 //屏幕解锁时开启的广播
            context.registerReceiver(myScreenReceiver,filter);             //发送广播
        }


    /**
     * 解除注册广播接收器
     */
        public void unregisterListener()
        {
            context.unregisterReceiver(myScreenReceiver);                  //注销广播
        }
    }

