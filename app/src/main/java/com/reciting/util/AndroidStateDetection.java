package com.reciting.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


//本来没准备这个东西的，但是为了加入一些网络功能，这些还是需要的
//说到这又想骂两句了，本来这些东西，iOS里是自动实现的，根本都不要我们去写，害，安卓就是个**
public class AndroidStateDetection
{
    //判断网络连接是否可用
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo [] networkInfos=connectivityManager.getAllNetworkInfo();

            if(networkInfos!=null&&networkInfos.length>0)
            {
                for(int i=0;i<networkInfos.length;i++)
                {
                    if(networkInfos[i].getState()==NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    //判断WIFI是否打开
    public static  boolean isWifi(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.getType()==ConnectivityManager.TYPE_WIFI)
        {
            return true;
        }
        return false;
    }


    //判断移动数据是否打开
    public static boolean isMobile(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.getType()==ConnectivityManager.TYPE_MOBILE)
        {
            return  true;
        }
        return false;
    }
}
