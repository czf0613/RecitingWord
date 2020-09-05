package com.reciting.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.reciting.greendao.entity.greendao.DaoMaster;
import com.reciting.greendao.entity.greendao.DaoSession;
import com.reciting.greendao.entity.greendao.WisdomEntity;
import com.reciting.greendao.entity.greendao.WisdomEntityDao;
import com.reciting.sockword.R;
import com.reciting.sockword.ReviewAcitivty;

import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StudyFragment extends Fragment implements View.OnClickListener
{
    /**
     * 名言(英语)
     * 名言(汉语)
     * 学习难度
     * 总共学习
     * 掌握单词
     * 答错题数
     */
    private TextView wisdomEnglish, wisdomChina, difficultyTv, alreadyStudyText, alreadyMasteredText, wrongText;

    //开始复习按钮
    private Button review_btn;
    //定义轻量级数据库
    private SharedPreferences sharedPreferences;
    //数据库管理者
    private DaoMaster myDaoMaster;
    //与数据库进行会话
    private DaoSession myDaoSession;
    private WisdomEntityDao questionDao;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //绑定布局文件
        View view = inflater.inflate(R.layout.study_fragment, null);
        //初始化数据库
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        //我都懒得打注释了……太无聊了
        wisdomEnglish = (TextView) view.findViewById(R.id.wisdom_english);
        wisdomChina = (TextView) view.findViewById(R.id.wisdom_china);
        difficultyTv = (TextView) view.findViewById(R.id.difficulty_text);
        alreadyStudyText = (TextView) view.findViewById(R.id.already_study);
        alreadyMasteredText = (TextView) view.findViewById(R.id.already_mastered);
        wrongText = (TextView) view.findViewById(R.id.wrong_text);
        review_btn = (Button)view.findViewById(R.id.review_btn);
        review_btn.setOnClickListener(this);

        //初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getActivity());
        //获取管理对象，因为数据库需要通过管理对象才能获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");
        myDaoMaster = new DaoMaster(db1);
        myDaoSession = myDaoMaster.newSession();
        //获取数据
        questionDao = myDaoSession.getWisdomEntityDao();
        return view;
    }



    @Override
    public void onStart()
    {
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty","四级")+"英语");
        List<WisdomEntity> datas=questionDao.queryBuilder().list();
        Random random=new Random();
        int i=random.nextInt(10);
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();
    }


    /**
     * 设置十字内的各个单词书(从轻量级数据库中获取数据)
     */
    private void setText()
    {
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }


    /**
     * 点击事件
     * 那上面有一个开始复习的按钮
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.review_btn:
                Intent intent=new Intent(getActivity(),ReviewAcitivty.class);
                startActivity(intent);
                break;
        }
    }
}
