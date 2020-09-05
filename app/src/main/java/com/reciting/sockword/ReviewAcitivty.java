package com.reciting.sockword;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.reciting.adapter.ReviewFragmentPagerAdapter;
import com.reciting.greendao.entity.greendao.CET4Entity;
import com.reciting.greendao.entity.greendao.CET4EntityDao;
import com.reciting.greendao.entity.greendao.DaoMaster;
import com.reciting.greendao.entity.greendao.DaoSession;
import com.reciting.transform.CardTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReviewAcitivty extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener
{
    //此处忽略一万字
    public static final String TAG = "ReviewAcitivty";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ViewPager viewpager;
    private ReviewFragmentPagerAdapter myAdapter;

    private ImageView back;
    //记住了，没记住按钮
    private Button review_yes,review_no;
    //当前页码和页码总数
    private TextView currentItem,allItem,dispatch;

    //前面说过了……不重复提了
    private SQLiteDatabase db,db2;
    private DaoMaster mDaoMaster,dbMaster,rMaster;
    private DaoSession mDaoSession,dbSession,rSession;
    private CET4EntityDao questionDao,dbDao,rDao;

    List<Integer> list;
    List<CET4Entity> datas;
    List<CET4Entity> reviewData;
    int reviewNum = 0;
    //错题数目
    int rnum = 0;
    //把错题id（即k）存进来
    List<Integer> wrong = new ArrayList<Integer>();


    //这里不用我再解释了吧
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        allItem = (TextView)findViewById(R.id.allItem);
        currentItem = (TextView)findViewById(R.id.currentItem);
        dispatch = (TextView)findViewById(R.id.dispatch);

        back = (ImageView)findViewById(R.id.review_back_btn);
        back.setOnClickListener(this);
        review_yes = (Button) findViewById(R.id.review_yes);
        review_no = (Button) findViewById(R.id.review_no);
        review_yes.setOnClickListener(this);
        review_no.setOnClickListener(this);

        viewpager.setOnPageChangeListener(this);

        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //初始化list  判断题的数目
        list=new ArrayList<Integer>();

        /**
         * 添加十个20以内的随机数
         */
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

        //具体参见MainActivity里面说的注释，我不想再打一遍了
        AssetsDatabaseManager.initManager(this);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("word.db");
        mDaoMaster=new DaoMaster(db1);
        mDaoSession=mDaoMaster.newSession();
        questionDao=mDaoSession.getCET4EntityDao();
        datas=questionDao.queryBuilder().list();
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"wrong.db",null);

        //初始化数据库
        //第一步获得一个Dao类：
        db=helper.getWritableDatabase();
        dbMaster=new DaoMaster(db);
        dbSession=dbMaster.newSession();
        dbDao=dbSession.getCET4EntityDao();
        DaoMaster.DevOpenHelper helper1=new DaoMaster.DevOpenHelper(this,"review.db",null);

        //再干一遍
        db2=helper1.getWritableDatabase();
        rMaster=new DaoMaster(db2);
        rSession=rMaster.newSession();
        rDao=rSession.getCET4EntityDao();


        if(rDao.queryBuilder().list().size()==0)
        {
            copyData();
        }

        if(wrong.size()!=dbDao.queryBuilder().list().size())
        {
            copyWrongId();
        }

        //设置复习题
        setData(reviewNum);
        Log.d(TAG, "初始复习题数："+String.valueOf(rnum));


    }


    //将复习题复制到数据库
    private void copyData()
    {
        int a = Integer.parseInt(sharedPreferences.getString("reviewNum","20"));
        for(int i=0;i<a;i++)
        {
            String word=datas.get(i).getWord();
            String english=datas.get(i).getEnglish();
            String china=datas.get(i).getchina();
            String sign=datas.get(i).getSign();
            CET4Entity data=new CET4Entity(Long.valueOf(i+1),word,english,china,sign);
            rDao.insert(data);
        }
    }

    //将错题ID复制
    private void copyWrongId()
    {
        for(int i=0;i<dbDao.queryBuilder().list().size();i++)
        {
            wrong.add(dbDao.queryBuilder().list().get(i).getId().intValue());
        }
    }


    //将没记住的存到数据库
    private  void saveWrongData(int k)
    {
        String word=datas.get(k-1).getWord();
        String english=datas.get(k-1).getEnglish();
        String china=datas.get(k-1).getchina();
        String sign=datas.get(k-1).getSign();
        //创建一个数据项
        CET4Entity data=new CET4Entity(Long.valueOf(dbDao.count()+1),word,english,china,sign);
        //保存数据项
        dbDao.insert(data);
        Toast.makeText(ReviewAcitivty.this,"已加入生词本",Toast.LENGTH_SHORT).show();
    }


    //设置错题
    private void setData(int j)
    {
        //初始化list
        reviewData = new ArrayList<>();
        //判断如果数据库不为空
        if (rDao.queryBuilder().list() != null && rDao.queryBuilder().list().size() > 0 && j <= rDao.queryBuilder().list().size() && j >= 0)
        {
            for (int i = 0; i < rDao.queryBuilder().list().size(); i++)
            {
                //把数据循环加到list里面
                reviewData.add(i, rDao.queryBuilder().list().get(i));
            }
        }
        else
        {
            reviewData.add(null);
            review_yes.setVisibility(View.GONE);
            review_no.setVisibility(View.GONE);
            currentItem.setVisibility(View.GONE);
            allItem.setVisibility(View.GONE);
            dispatch.setVisibility(View.GONE);
        }
        //错题数量
        rnum = rDao.queryBuilder().list().size();
        Log.d(TAG, "复习题数量为："+rnum);

        //转换成String类型，偷懒这么干了
        allItem.setText(rnum+"");

        myAdapter = new ReviewFragmentPagerAdapter(getSupportFragmentManager(),reviewData);
        viewpager.setAdapter(myAdapter);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setCurrentItem(reviewData.size()-1);
        viewpager.setPageTransformer(true,new CardTransformer());
    }


    //按钮的监听
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //记住了  按钮的点击操作
            case R.id.review_yes:
                //从数据库删除该条数据
                rDao.deleteByKey(rDao.queryBuilder().list().get(reviewNum).getId());
                Log.d(TAG, "删除的是第几个："+String.valueOf(reviewNum));
                //刷新数据
                setData(reviewNum);
                break;
            //没记住  按钮的点击操作
            case R.id.review_no:
                //加入生词本
                if(!wrong.contains(rDao.queryBuilder().list().get(reviewNum).getId().intValue()))
                {
                    wrong.add(rDao.queryBuilder().list().get(reviewNum).getId().intValue());
                    saveWrongData(rDao.queryBuilder().list().get(reviewNum).getId().intValue());
                }
                //从数据库删除该条数据
                rDao.deleteByKey(rDao.queryBuilder().list().get(reviewNum).getId());
                Log.d(TAG, "删除的是第几个："+String.valueOf(reviewNum));
                setData(reviewNum);
                break;
            //返回按钮
            case R.id.review_back_btn:
                finish();
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        reviewNum = position;
        currentItem.setText(reviewNum+1+"");
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
