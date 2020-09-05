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

public class ReviewFragment extends Fragment
{

    private View myRootView;
    private CET4Entity review;
    //用来显示单词和音标的
    private TextView chineseText, wordText, englishText;

    //复习的时候单词是一个个小卡片显示出来的，本质上这些是一个个fragment
    public static ReviewFragment newInstance(CET4Entity info)
    {
        ReviewFragment fragment=new ReviewFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("review", (Serializable) info);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 把那些卡片显示出来，并且绑定布局文件
     * 有可能是空的，毕竟单词都复习完了或者没有不会的
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        myRootView = inflater.inflate(R.layout.review_fragment,container,false);
        return myRootView;
    }

    //这些小卡片包含中英文以及对应的音标，在这里初始化好
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //汉语绑定id
        chineseText = myRootView.findViewById(R.id.china_text);
        //音标绑定id
        englishText = myRootView.findViewById(R.id.english_text);
        //单词绑定id
        wordText = myRootView.findViewById(R.id.word_text);
        review = (CET4Entity) getArguments().getSerializable("review");
        setData();
    }


    /**
     * 设置错题
     * 害，我都懒得打了，看前面CardFragment那里的吧，基本一样照抄过来
     * */
    private void setData()
    {
        //如果数据库不为空
        if (review != null)
        {
            //分别将list里面的数据取出数据设置单词音标以及汉语
            wordText.setText(review.getWord());
            englishText.setText(review.getEnglish());
            chineseText.setText(review.getchina());

        }
        else
        {
            /**
             * 如果数据库为空
             * 隐藏“我会了”按钮
             * 剩下的瞎jb乱写的，不要在意这些细节
             * */
            wordText.setText("好厉害");
            englishText.setText("[今天的单词]");
            chineseText.setText("都复习完啦");
        }

    }
}
