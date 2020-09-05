package com.reciting.transform;


import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

//这个是转场动画，需要系统版本是7.0以上，7.0的代号就是LOLLIPOP，那个棒棒糖
public class CardTransformer implements ViewPager.PageTransformer
{
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transformPage(View page, float position)
    {
        if(position < 0) {
            page.setTranslationX(-position*page.getWidth());
            page.setTranslationZ(position);
            //缩放比例
            float scale = (page.getWidth()+40*position)/page.getWidth();
            page.setScaleY(scale);
            page.setScaleX(scale);
            page.setTranslationY(-position*40);
        }
    }
}