package com.reciting.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.reciting.fragment.CardFragment;
import com.reciting.greendao.entity.greendao.CET4Entity;

import java.util.List;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    //获取错误的单词，存入列表里
    private List<CET4Entity> wrongData;

    //构造函数
    public CardFragmentPagerAdapter(FragmentManager fm, List<CET4Entity> list)
    {
        super(fm);
        this.wrongData = list;
    }

    //相当于get函数，返回列表对应位置的元素
    @Override
    public Fragment getItem(int position)
    {
            return CardFragment.newInstance(wrongData.get(position));
    }

    //返回列表的大小，即单词的数量
    @Override
    public int getCount()
    {
        return this.wrongData.size();
    }

}
