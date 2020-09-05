package com.reciting.sockword;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.reciting.greendao.entity.greendao.CET4Entity;
import com.reciting.greendao.entity.greendao.CET4EntityDao;
import com.reciting.greendao.entity.greendao.DaoMaster;
import com.reciting.greendao.entity.greendao.DaoSession;
import com.reciting.util.BaseApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

//这个是锁屏界面看到的那个页面，巨复杂
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
    //这里我实在不想在浪费时间打注释了
    public static final String TAG = "MainActivity";
    //用来显示单词和音标
    private TextView timeText,dateText,wordText,englishText;
    //用来显示时间
    private String myMonth,myDay,myweek,myHour,myMinute,mySecond;
    //键盘锁的对象
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock k1;
    //加载3个单词选项
    private RadioGroup radioGroup;
    private RadioButton radioOne,radioTwo,radioThree;
    LinearLayout lockback;
    //定义轻量级数据库
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor=null;
    //用于记录答了几道题
    int j=0;
    //判断题的数目
    List<Integer> list;
    //把错题id（即k）存进来
    List<Integer> wrong = new ArrayList<Integer>();
    //用于从数据库读取相应的词库
    List<CET4Entity> datas;
    //错题的id
    int k;

    /**
     * 手指按下时位置坐标为（x1,y1）
     * 手指离开屏幕时坐标为(x2,y2)
     */
    float x1=0;
    float y1=0;
    float x2=0;
    float y2=0;

    //创建数据库
    private SQLiteDatabase db;
    //管理者
    private DaoMaster mDaoMaster,dbMaster;
    //和数据库进行对话
    private DaoSession mDaoSession,dbSession;
    //对应的表,由java代码生成的，对数据库内相应的表操作使用此对象
    private CET4EntityDao questionDao,dbDao;


    //显示所有的东西……哇前期准备真的打的吐血，超级多东西
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //将锁屏界面的内容显示在手机屏幕的最上层(将Activity显示在锁屏界面上)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //绑定布局文件
        setContentView(R.layout.activity_main);
        init();
    }

    public  void init(){
        /**
         * 初始化轻量级数据库
         * 指定该文件的名称
         * 指定文件的操作模式
         */
        sharedPreferences=getSharedPreferences("share", Context.MODE_PRIVATE);


        //初始化轻量级数据库编辑器
        editor=sharedPreferences.edit();
        //初始化list  判断题的数目
        list=new ArrayList<Integer>();


         //添加十个20以内的随机数，用来出题
        Random r=new Random();
        int i;
        while (list.size()<10)
        {
            i=r.nextInt(20);
            if(!list.contains(i))
            {
                list.add(i);
            }
        }


        //得到键盘锁管理对象
        km =(KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        k1 = km.newKeyguardLock("unLock");


        //初始化只需要调用一次
        AssetsDatabaseManager.initManager(this);
        //获取管理对象,因为数据库需要通过管理对象才能获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        //通过管理对象获取数据库
        SQLiteDatabase db1 = mg.getDatabase("word.db");
        //对数据库进行操作
        mDaoMaster=new DaoMaster(db1);
        mDaoSession=mDaoMaster.newSession();
        questionDao=mDaoSession.getCET4EntityDao();


        /**
         * 第一个参数为Context
         * 第二个参数为数据库名字
         * 第三参数为CursorFactory
         */
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"wrong.db",null);


        //初始化数据库
        //第一步获得一个Dao类：
        db=helper.getWritableDatabase();
        dbMaster=new DaoMaster(db);
        dbSession=dbMaster.newSession();
        dbDao=dbSession.getCET4EntityDao();

        //又tm是绑定一堆组件……真的打的吐血了，好累啊
        lockback=(LinearLayout)findViewById(R.id.lockback);

        timeText=(TextView) findViewById(R.id.time_text);
        dateText=(TextView) findViewById(R.id.date_text);
        wordText=(TextView) findViewById(R.id.word_text);
        englishText=(TextView) findViewById(R.id.english_text);

        radioGroup=(RadioGroup) findViewById(R.id.choose_group);
        radioOne=(RadioButton) findViewById(R.id.choose_btn_one);
        radioTwo=(RadioButton) findViewById(R.id.choose_btn_two);
        radioThree=(RadioButton) findViewById(R.id.choose_btn_three);
        radioGroup.setOnCheckedChangeListener(this);

        //设置锁屏背景，具体的实现在后面那个设置页面有提到
        lockback.setBackgroundDrawable(Drawable.createFromPath(sharedPreferences.getString("lockback","/storage/emulated/0/Tencent/QQfile_recv/background.png")));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        /**
         * 获取系统时间，并设置将其显示出来
         * 拿到Calendar对象即可获取系统当前时间
         */
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        //月份是0-11所以获取当前月必须加1
        myMonth =String.valueOf(calendar.get(Calendar.MONTH)+1);
        myDay =String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        //星期是1234567，待会后面还要处理一下
        myweek =String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        mySecond = String.valueOf(calendar.get(Calendar.SECOND));


        //如果小时是个位数则在前面加一个0
        if(calendar.get(Calendar.HOUR_OF_DAY)<10)
            myHour ="0"+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        else
            myHour =String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));

        //如果分钟是个位数则在前面加一个0
        if(calendar.get(Calendar.MINUTE)<10)
            myMinute ="0"+String.valueOf(calendar.get(Calendar.MINUTE));
        else
            myMinute =String.valueOf(calendar.get(Calendar.MINUTE));


        /**
         * 获取星期，并进行处理
         * Java的String是真的坑，一开始不小心打了==，全部完蛋
         */
        if("1".equals(myweek))
            myweek ="天";
        else if("2".equals(myweek))
            myweek ="一";
        else if("3".equals(myweek))
            myweek ="二";
        else if("4".equals(myweek))
            myweek ="三";
        else if("5".equals(myweek))
            myweek ="四";
        else if("6".equals(myweek))
            myweek ="五";
        else if("7".equals(myweek))
            myweek ="六";


        //显示出来
        timeText.setText(myHour +":"+ myMinute);
        dateText.setText(myMonth +"月"+ myDay +"日"+"  "+"星期"+ myweek);

        //把mainActivity添加到销毁集合里
        BaseApplication.addDestoryActivity(this,"mainActivity");
        //随机获取锁屏界面单词
        getDBData();
    }


    /**
     * 判断是否是同一天
     * 是同一天返回false
     * 因为我们要记录同一天做了哪些题，所以为了防止每天23:59发生跨天的事情
     * 这里需要我们处理一下，具体怎么用看到最后面的unlock
     */
    private boolean isToday()
    {
        int oldminute = sharedPreferences.getInt("minute", -1);
        int oldmonth = sharedPreferences.getInt("month", -1);
        int olddate = sharedPreferences.getInt("date", -1);
        int oldsecond = sharedPreferences.getInt("second",-1);
        //写进去缓存里
        editor.putInt("second", Integer.parseInt(mySecond));
        editor.putInt("month", Integer.parseInt(myMonth));
        editor.putInt("date", Integer.parseInt(myDay));
        editor.putInt("minute", Integer.parseInt(myMinute));
        editor.commit();

        //一点一点比较……其实那么多if加在一起是很消耗性能的
        if (oldminute == -1 || oldmonth == -1 || olddate == -1|| oldsecond == -1)
            return true;


        if (oldmonth < Integer.parseInt(myMonth))
            return true;
        else if (olddate < Integer.parseInt(myDay))
            return true;
        else if (oldminute < Integer.parseInt(myMinute))
            return true;
        else if (oldsecond < Integer.parseInt(mySecond))
            return true;
        else
            return false;
    }


     //将错题存到数据库
    private  void saveWrongData()
    {
        String word=datas.get(k).getWord();
        String english=datas.get(k).getEnglish();
        String china=datas.get(k).getchina();
        String sign=datas.get(k).getSign();
        CET4Entity data=new CET4Entity(Long.valueOf(dbDao.count()+1),word,english,china,sign);
        dbDao.insert(data);
    }

    private void btnGetText(String msg,RadioButton btn)
    {
        /**
         * 答对设置绿色
         * 答错设置红色
         */
        if(msg.equals(datas.get(k).getchina()))
        {
            wordText.setTextColor(Color.GREEN);
            englishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
            Log.d(TAG, "回答正确: "+ k );
        }
        else
        {
            wordText.setTextColor(Color.RED);
            englishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);
            Log.d(TAG, "错题id k为: "+ k );

            if(!wrong.contains(k))
            {
                Log.d(TAG, "不重复错题id k为: "+ k );
                wrong.add(k);
                saveWrongData();
                //保存到数据库
                int wrong=sharedPreferences.getInt("wrong",0);
                //修改错题数
                editor.putInt("wrong",wrong+1);
                //将错题Id存入sharedPreferences中
                editor.putString("wrongId",","+datas.get(j).getId());
                //提交修改
                editor.commit();
            }
        }
    }


    //设置选项(A B C）
    private void setChinese(List<CET4Entity>datas, int j)
    {
        /**
         * 随机产生几个随机数，是用于生成解锁单词
         * 因为数据库中只录入20个单词，所以产生的随机数都是20以内的
         */
        Random r=new Random();
        List<Integer>list=new ArrayList<Integer>();
        int i;
        while (list.size()<4)
        {
            i=r.nextInt(20);
            if(!list.contains(i))
            {
                list.add(i);
            }
        }


        /**
         *设置单词中文意思的错误选项
         * 设置为正确，当前单词的前一个，后一个
         * 其实……我也想改变一下规则，但是毕竟是20之内的随机数，太容易pseudo random
         */
        if(list.get(0)<7){
            radioOne.setText("A: "+datas.get(k).getchina());
            if(k-1>=0){
                radioTwo.setText("B: "+datas.get(k-1).getchina());
            }else{
                radioTwo.setText("B: "+datas.get(k+2).getchina());
            }

            if(k+1<20){
                radioThree.setText("C: "+datas.get(k+1).getchina());
            }else{
                radioThree.setText("C: "+datas.get(k-2).getchina());
            }
        }else if(list.get(0)<14){
            radioTwo.setText("B: "+datas.get(k).getchina());
            if(k-1>=0){
                radioOne.setText("A: "+datas.get(k-1).getchina());
            }else{
                radioOne.setText("A: "+datas.get(k+2).getchina());
            }

            if(k+1<20){
                radioThree.setText("C: "+datas.get(k+1).getchina());
            }else{
                radioThree.setText("C: "+datas.get(k-2).getchina());
            }
        }else{
            radioThree.setText("C: "+datas.get(k).getchina());
            if(k-1>=0){
                radioTwo.setText("B: "+datas.get(k-1).getchina());
            }else{
                radioTwo.setText("B: "+datas.get(k+2).getchina());
            }

            if(k+1<20){
                radioOne.setText("A: "+datas.get(k+1).getchina());
            }else{
                radioOne.setText("A: "+datas.get(k-2).getchina());
            }
        }
    }



    //获取数据库数据
    private void getDBData()
    {
        //获取数据表中所有数据
        datas=questionDao.queryBuilder().list();
        //获取数据项ID,list中保存了10个20以内的数据项id，且不重复
        k=list.get(j);
        Log.d(TAG, "初始获取k为: "+k);
        wordText.setText(datas.get(k).getWord());
        englishText.setText(datas.get(k).getEnglish());
        //设置单词的三个词义选项
        setChinese(datas,k);
    }


    //手指滑动的事件
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //当手指按下时坐标（x,y）
                x1=event.getX();
                y1=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开时坐标(x,y)
                x2=event.getX();
                y2=event.getY();


                //上划直接解锁
                if(y1-y2>200)
                    unlocked();
                //下滑标记为已掌握
                else if(y2-y1>200)
                {
                    //已掌握单词数量加1
                    int num=sharedPreferences.getInt("alreadyMastered",0)+1;
                    editor.putInt("alreadyMastered",num);
                    editor.commit();
                    Toast.makeText(MainActivity.this,"已掌握",Toast.LENGTH_SHORT).show();
                    getNextData();
                }
                //右划下一题
                else if(x2-x1>200)
                    //获取下一题
                    getNextData();
                break;
        }
        return  super.onTouchEvent(event);
    }



    //获取下一题
    private void getNextData()
    {
        //当前已做题的数目
        j++;
        //解锁题目数，默认为2道，要读设置数据才知道
        int i=sharedPreferences.getInt("allNum",2);
        if(i>j)
        {
            //获取数据
            getDBData();
            //设置颜色，因为刚刚答对了会变绿，答错了会变红，要初始化全白
            setTextColor();
        }
        else
        {
            //答题数满足设置的数量即解锁屏幕
            unlocked();
        }
    }


    //三个选项的选择
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
       switch (i)
       {
           case R.id.choose_btn_one:
               String msg=radioOne.getText().toString().substring(3);
               btnGetText(msg,radioOne);
               break;
           case R.id.choose_btn_two:
               String msg1=radioTwo.getText().toString().substring(3);
               btnGetText(msg1,radioTwo);
               break;
           case R.id.choose_btn_three:
               String msg2=radioThree.getText().toString().substring(3);
               btnGetText(msg2,radioThree);
               break;
       }
    }


    //还原单词与选项选项
    private  void setTextColor()
    {
        //3个单选按钮默认不被选中
        radioOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);

        //将选项按钮和单词音标设置为白色
        radioOne.setTextColor(Color.parseColor("#FFFFFF"));
        radioTwo.setTextColor(Color.parseColor("#FFFFFF"));
        radioThree.setTextColor(Color.parseColor("#FFFFFF"));
        wordText.setTextColor(Color.parseColor("#FFFFFF"));
        englishText.setTextColor(Color.parseColor("#FFFFFF"));
    }



    //解锁
    private void unlocked()
    {
        //隐式启动
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        //解锁
        k1.disableKeyguard();
        /**
         * 呼应前文
         * 这个判断同一天主要是记录今天学了多少题
         * 为了防止0:00这种尴尬事情的发生
         */
        if(isToday())
        {
            int locknum=sharedPreferences.getInt("alreadyStudy",0)+1;
            editor.putInt("alreadyStudy",locknum);
            editor.commit();
        }
        else
        {
            int locknum=1;
            editor.putInt("alreadyStudy",locknum);
            editor.commit();
        }
        finish();
    }
}
