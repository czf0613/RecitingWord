package com.reciting.sockword;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.reciting.fragment.SetFragment;
import com.reciting.fragment.StudyFragment;
import com.reciting.util.BaseApplication;
import com.reciting.util.ScreenListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


//我是真的不知道这里的注释写什么好了
public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    //绑定此页面与手机屏幕状态的监听
    private ScreenListener screenListener;
    //定义轻量级数据库
    private SharedPreferences sharedPreferences;
    //定义用于加载复习与设置的界面
    private FragmentTransaction transaction;
    //绑定复习界面
    private StudyFragment studyFragment;
    //绑定设置界面
    private SetFragment setFragment;
    //定义错词本按钮
    private Button wrongBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        init();
    }


    private void init()
    {
        //操作本地缓存，待会要读出来进行我们现在的应用
        sharedPreferences=getSharedPreferences("share",Context.MODE_PRIVATE);
        wrongBtn=(Button) findViewById(R.id.wrong_btn);
        wrongBtn.setOnClickListener(this);
        //注册一个editor，写上final主要是怕不小心被继承过去容易出事，因为好多类里用了同名的东西
        final  SharedPreferences.Editor edit=sharedPreferences.edit();

        //屏幕状态进行监听
        screenListener=new ScreenListener(this);

        screenListener.begin(new ScreenListener.ScreenStateListener()
        {
            @Override
            public void onScreenOn()
            {
                //判断是否在设置界面开启了锁屏按钮,如果开启则启动单词锁屏界面
                if (sharedPreferences.getBoolean("btnTf", false))
                {
                    //判断屏幕是否解锁
                    Log.d("HomeActivity","解锁了");
                    if (sharedPreferences.getBoolean("tf", false))
                    {
                        Log.d("HomeActivity","屏幕解锁了");
                        //打开另一个屏幕
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }


            /**
             *如果手机已经锁了
             * 就把数据库中的tf字段改成true
             */
            @Override
            public void onScreenOff()
            {
                //手机已锁屏操作
                Log.d("HomeActivity","手机锁屏了");
                edit.putBoolean("tf",true);
                edit.commit();
                //销毁锁屏界面
                BaseApplication.destroyActivity("mainActivity");
            }


            /**
             * 如果手机已经解锁了
             * 就把数据库中的tf字段改成false
             */
            @Override
            public void onUserPresent()
            {
                //一个道理我就不写注释了
                Log.d("HomeActivity","手机解锁了");
                edit.putBoolean("tf",false);
                edit.commit();

            }
        });

        //当此界面加载时，就显示复习界面的fragment
         studyFragment=new StudyFragment();
         setFragment(studyFragment);

    }

    public void setFragment(Fragment fragment)
    {
        //载入那么多的fragment
        transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();
    }


    /**
     * 接下来的两个是为了fragment服务的
     * 待会就会用到了
     */
    //单击进入复习界面
    public void study(View view)
    {
        if(studyFragment==null)
        {
            studyFragment=new StudyFragment();
        }
        setFragment(studyFragment);
    }

    //单击进入设置界面
    public void set(View view)
    {
        if(setFragment==null)
        {
            setFragment=new SetFragment();
        }
        setFragment(setFragment);
    }



    //如果按到了错题本的按钮，就进去另外一个界面
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.wrong_btn:
                Intent intent=new Intent(HomeActivity.this,WrongAcitivty.class);
                startActivity(intent);
                 break;
        }
    }


    //销毁activity
    @Override
    protected void onDestroy()
    {
        screenListener.unregisterListener();
        super.onDestroy();
    }
}
