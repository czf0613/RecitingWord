package com.reciting.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.reciting.fragment.ReviewFragment;
import com.reciting.greendao.entity.greendao.CET4Entity;

import java.util.List;

public class ReviewFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    //要复习的单词，存进列表里
    private List<CET4Entity> reviewData;

    //构造函数
    public ReviewFragmentPagerAdapter(FragmentManager fm, List<CET4Entity> list)
    {
        super(fm);
        this.reviewData = list;
    }

    //下面的我就不说了吧……是个人都看得懂了
    @Override
    public Fragment getItem(int position)
    {
        return ReviewFragment.newInstance(reviewData.get(position));
    }

    @Override
    public int getCount()
    {
        return this.reviewData.size();
    }

}
