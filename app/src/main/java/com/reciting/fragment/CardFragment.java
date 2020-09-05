package com.reciting.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reciting.greendao.entity.greendao.CET4Entity;
import com.reciting.sockword.R;

import java.io.Serializable;

public class CardFragment extends Fragment
{
    private View myRootView;
    private CET4Entity wrong;
    //用来显示单词和音标的
    private TextView chineseText, wordText, englishText;


    //产生一个新的复习卡片的fragment
    public static CardFragment newInstance(CET4Entity info)
    {
        CardFragment fragment=new CardFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("info",(Serializable) info);
        fragment.setArguments(bundle);
        return fragment;
    }

    //显示出来，并绑定布局文件
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        myRootView = inflater.inflate(R.layout.wrong_fragment,container,false);
        return myRootView;
    }


    //跟上面的作用差不多
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //汉语绑定id
        chineseText = myRootView.findViewById(R.id.china_text);
        //音标绑定id
        englishText = myRootView.findViewById(R.id.english_text);
        //单词绑定id
        wordText = myRootView.findViewById(R.id.word_text);
        wrong = (CET4Entity) getArguments().getSerializable("info");
        setData();
    }


    //设置错题
    private void setData()
    {
        //判断如果数据库不为空
        if (wrong != null)
        {
            /**
             * 分别将list里面的数据取出数据
             * 设置单词音标以及汉语
             * */
            wordText.setText(wrong.getWord());
            englishText.setText(wrong.getEnglish());
            chineseText.setText(wrong.getchina());

        }
        else
        {
            /**
             * 如果数据库为空
             * 说明没有错题了
             * 隐藏“我会了”按钮
             * 这些文本瞎写的（逃
             * */
            wordText.setText("好厉害");
            englishText.setText("[没有]");
            chineseText.setText("不会的单词了");
        }
    }
}
