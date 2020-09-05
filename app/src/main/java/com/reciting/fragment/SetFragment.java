package com.reciting.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.reciting.sockword.R;
import com.reciting.util.SwitchButton;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

//设置界面的逻辑
public class SetFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SetFragment";
    //加入了选择图片当背景的功能，接下来的三个东西都是为这个服务的
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;

    //定义轻量级数据库，老生常谈了……不用再说是啥了吧
    private SharedPreferences sharedPreferences;
    //“锁屏界面”开关按钮
    private SwitchButton switchButton;
    //定义选择难度的下拉框
    private Spinner spinnerDifficulty;
    //定义选择解锁题目的下拉框
    private Spinner spinnerALLNum;
    //定义新题目的下拉框
    private Spinner spinnerNewNum;
    //定义复习题的下拉框
    private Spinner spinnerReviewNum;
    //锁屏壁纸
    private Spinner spinnerLockBackground;
    //定义下拉框的适配器
    private ArrayAdapter<String> adapterDifficulty,adapterAllNum,adapterNewNum,adapterReviewNum,adapterLockBackground;


    /**
     * 所有下拉框的内容在此
     * 选择难度的下拉框内容
     * 选择解锁题目的下拉框
     * 新题目下拉框的选项内容
     * 复习题的下拉框
     * 锁屏壁纸选择下拉框内容
     */
    String [] difficulty =new String[]{"小学","初中","高中","四级","六级"};
    String [] allNum=new String[]{"2","4","6","8"};
    String [] newNum=new String []{"10","30","50","100"};
    String [] reviewNum=new String[] {"5","10","15","20"};
    String [] LockBackground= new String[] {"默认壁纸","从手机相册选择"};

    //shared preference的修改器
    SharedPreferences.Editor editor=null;


    //获取本地缓存里存储的设置，并且进行显示
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.set_fragment_layout,null);
        init(view);
        return view;
    }

    private  void init(View view)
    {
        //初始化数据库，这里是存设置的东西
        sharedPreferences=getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        //绑定一堆东西
        switchButton=(SwitchButton) view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);

        spinnerDifficulty=(Spinner) view.findViewById(R.id.spinner_difficulty);
        spinnerALLNum=(Spinner) view.findViewById(R.id.spinner_all_number);
        spinnerNewNum=(Spinner) view.findViewById(R.id.spinner_new_number);
        spinnerReviewNum=(Spinner) view.findViewById(R.id.spinner_revise_number);
        spinnerLockBackground=(Spinner) view.findViewById(R.id.spinnerLockBackground);

        picture = (ImageView) view.findViewById(R.id.picture);
//        picture.setBackgroundDrawable(Drawable.createFromPath(sharedPreferences.getString("lockback",String.valueOf(R.mipmap.background))));
        picture.setBackgroundDrawable(Drawable.createFromPath(sharedPreferences.getString("lockback","/storage/emulated/0/Tencent/QQfile_recv/background.png")));



        //以下的是选择单词难度的实现
        //初始化选择难度下拉框的适配器
        adapterDifficulty=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,difficulty);
        //给下拉框设置适配器
        spinnerDifficulty.setAdapter(adapterDifficulty);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        //设置选择难度下拉框的监听事件
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            //获取选择的内容
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String msg=adapterView.getItemAtPosition(i).toString();
                //记得要写进去缓存，不然就渣都没有了
                editor.putString("difficulty",msg);
                editor.commit();
            }

            //没选就啥都不干吧
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * 解锁题个数的选项框，同上面的选择难度的选项框的原理一样，后面的都是基本上复制黏贴的，没啥好说的
         */

        //初始化解锁题目下拉框的适配器
        adapterAllNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,allNum);
        //给下拉框设置适配器
        spinnerALLNum.setAdapter(adapterAllNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerALLNum,sharedPreferences.getInt("allNum",2)+"道");

        //设置选择难度下拉框的监听事件
        this.spinnerALLNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg = adapterView.getItemAtPosition(i).toString();
                int ii=Integer.parseInt(msg);
                editor.putInt("allNum",ii);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /**
         * 每日新题个数的选项框，同上面的选择难度的选项框的原理一样，实在懒得再打了……
         * 那个注释里写的什么“难度下拉框”意会一下就好
         * 复制黏贴的，大家懂的
         */

        //初始化每日新题个数下拉框的适配器
        adapterNewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,newNum);
        //给下拉框设置适配器
        spinnerNewNum.setAdapter(adapterNewNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerNewNum,sharedPreferences.getString("newNum","10"));
        //设置选择难度下拉框的监听事件
        this.spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("newNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /**
         *略……
         */

        //初始化选择难度下拉框的适配器
        adapterReviewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item, reviewNum);
        //给下拉框设置适配器
        spinnerReviewNum.setAdapter(adapterReviewNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerReviewNum,sharedPreferences.getString("reviewNum","20"));
        //设置选择难度下拉框的监听事件
        this.spinnerReviewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("reviewNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /**
         *  选择锁屏壁纸
         */

        //初始化选择难度下拉框的适配器
        adapterLockBackground=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,LockBackground);
        //给下拉框设置适配器
        spinnerLockBackground.setAdapter(adapterLockBackground);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerLockBackground,sharedPreferences.getString("LockBackground","默认壁纸"));
        //设置选择难度下拉框的监听事件
        this.spinnerLockBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("LockBackground",msg);
                editor.commit();
                /**
                 * 想了好久默认壁纸该咋存……但是真的想不到该咋办，因为不好就为了这个单独还配一个图片uri
                 * 那这样子我们就还要考虑在一个特定的区域里存一张图，有点蛋疼
                 * 既然大部分人的手机里都有QQ，那就借QQ的背景一用
                 * 这是个鸡贼的做法
                 * 如果有的人手机里实在木有QQ，背景就会变成白的，此时建议自行设置背景图片
                 */
                if(msg.equals("默认壁纸"))
                {
                    //QQ的背景图片地址，借来py交易一波
                    editor.putString("lockback","/storage/emulated/0/Tencent/QQfile_recv/background.png");
                    editor.commit();

                    //设置背景图片
                    Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Tencent/QQfile_recv/background.png");
                    picture.setImageBitmap(bitmap);

                }
                else if(msg.equals("从手机相册选择"))
                {
                    //动态申请打开相册的权限
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                    else
                        openAlbum();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    /**
     * 每次进入设置页面时下拉框显示最新选择的数据
     * 读取本地缓存并且显示出来，当成是默认的选择项
     * @param spinner
     * @param value
     */
    public  void setSpinnerItemSelectedByValue(Spinner spinner, String value)
    {
        SpinnerAdapter spinnerAdapter=spinner.getAdapter();
        int k=spinnerAdapter.getCount();
        for (int i=0;i<k;i++)
        {
            if(spinnerAdapter.getItem(i).toString().equals(value))
            {
                //默认选中项
                spinner.setSelection(i,true);
            }
        }
    }



    /**
     * 给“锁屏界面”开关设置了点击事件
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.switch_btn:
                if(switchButton.isSwitchOpen())
                {
                    //这里有个小动画
                    switchButton.closeSwitch();
                    //记得给缓存写进去
                    editor.putBoolean("btnTf",false);
                }
                else
                {
                    switchButton.openSwitch();
                    editor.putBoolean("btnTf",true);
                }
                //千万记得写缓存啊，不然就GG了
                editor.commit();
                break;
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();
        /**
         * 从数据获取开关按钮的状态
         * 其实就是显示“设置”而已，因为这个不是spinner，所以得单独写一个
         * 正好这个可以在onStart时间里写，索性就重写这个方法咯
         * 如果返回为true则将解锁开关打开,否则关闭
         */
        if(sharedPreferences.getBoolean("btnTf",false))
            switchButton.openSwitch();
        else
            switchButton.closeSwitch();
    }



    /**
     * 打开手机相册
     * 这里要用一下Intent事件
     */
    private void openAlbum()
    {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }


    //前面说的申请权限的事情，在这里放一个监听
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                //同意就打开，不同意就显示一个消息说不同意
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                else
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
    }


    /**
     * 这里要来一个监听，处理选择了图片之后的事情
     * 如果选择了的话，返回的是Context的RESULT_OK，如果没有选的话……我也不知道会返回啥
     * 然而这里遇到了一点坑，系统版本有点坑爹
     * 判断版本，然后对于不同的版本要不同的处理
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK)
                {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19)
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    else
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }


    //首先说下面这些东西是网上抄的，这超出我的知识范围了……
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data)
    {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d(TAG, "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(getActivity(), uri))
        {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority()))
            {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority()))
            {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }

        editor.putString("lockback",imagePath);
        editor.commit();
        Log.d(TAG, "选择图片路径为: "+imagePath);
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data)
    {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }



    //获取文件的路径（行了我的脑子终于在线了）
    private String getImagePath(Uri uri, String selection)
    {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径，就像查数据库一样
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    //利用图片的路径，进行显示
    private void displayImage(String imagePath)
    {
        if (imagePath != null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }
        else
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
    }

}
