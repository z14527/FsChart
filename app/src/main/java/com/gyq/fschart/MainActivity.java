package com.gyq.fschart;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rxpermisson.PermissionAppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

import static java.lang.Math.max;

public class MainActivity extends PermissionAppCompatActivity implements View.OnClickListener{
    private DrawingView mDrawingView;
    private static TextView mTextView = null;
    private TextView mTvReDraw = null;
    private TextView mTvToShui = null;
    private TextView mTvShanShuiSwitch = null;
    private static int COLOR_PANEL = 0;
    private static int BRUSH = 0;
    private ImageButton mColorPanel;
    private ImageButton mBrush;
    private ImageButton mUndo;
    private ImageButton mSave;
    public static Map<String,Integer> ShanMap=new HashMap<>();
    public static Map<String,Integer> ShuiMap=new HashMap<>();
    public static String shan ="庚申坤未丁午丙巳巽辰乙卯甲寅艮丑癸子壬亥乾戌辛酉";
    private static String lshan = null;
    public static String shui ="申坤未丁午丙巳巽辰乙卯甲寅艮丑癸子壬亥乾戌辛酉庚";
    private static String lshui = null;
    public static int bshan = 1;
    public static AlertDialog.Builder builder = null;
    public static Context context = null;

    public static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 3:
                    String mt2 = msg.getData().getString("send");
                    String[] mt3 = mt2.split(":");
                    if(mt3.length>=2){
                        int k = Integer.parseInt(mt3[0]);
                        int r = Integer.parseInt(mt3[1]);
                        if(k>=0 && k<24) {
                            if(bshan==1) {
                                String fs1 = shan.substring(k, k + 1);
                                if(r>=0){
                                    if (fs1.equals(lshan))
                                        ShanMap.put(fs1, max(ShanMap.get(fs1), r));
                                    else
                                        ShanMap.put(fs1, r);
                                    lshan = fs1;
                                    String info = "";
                                    for (int j = 0; j < 24; j++) {
                                        info = info + shan.substring(j, j + 1) + ": " + ShanMap.get(shan.substring(j, j + 1)) + "\t\t\t\t\t";
                                        if (j % 3 == 2)
                                            info = info + "\n";
                                    }
                                    mTextView.setText(info);
                                }else{
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("请输入 "+fs1+" 的山的数值(-100 - 100)：");    //设置对话框标题
                                    builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
                                    final EditText edit = new EditText(context);
                                    builder.setView(edit);
                                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String shan1 = edit.getText().toString();
                                            Toast.makeText(context,"输入的数值为："+shan1,Toast.LENGTH_LONG);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "你点了取消", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                                    AlertDialog dialog = builder.create();  //创建对话框
                                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                                    dialog.show();
                                }
                            }else{
                                k = k - 1;
                                if(k < 0 )
                                    k = k + 24;
                                String fs1 = shui.substring(k, k + 1);
                                if(r>=0) {
                                    if (fs1.equals(lshui))
                                        ShuiMap.put(fs1, max(ShuiMap.get(fs1), r));
                                    else
                                        ShuiMap.put(fs1, r);
                                    lshui = fs1;
                                    String info = "";
                                    for (int j = 0; j < 24; j++) {
                                        info = info + shui.substring(j, j + 1) + ": " + ShuiMap.get(shui.substring(j, j + 1)) + "\t\t\t\t\t";
                                        if (j % 3 == 2)
                                            info = info + "\n";
                                    }
                                    mTextView.setText(info);
                                }else{
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("请输入 "+fs1+" 的水的数值(-100 - 100)：");    //设置对话框标题
                                    builder.setIcon(android.R.drawable.btn_star);   //设置对话框标题前的图标
                                    final EditText edit = new EditText(context);
                                    builder.setView(edit);
                                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String shui1 = edit.getText().toString();
                                            Toast.makeText(context,"输入的数值为："+shui1,Toast.LENGTH_LONG);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "你点了取消", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                                    AlertDialog dialog = builder.create();  //创建对话框
                                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                                    dialog.show();
                                }
                            }
                        }
                    }
                    break;
                case 4:
                    String mt4 = msg.getData().getString("send");
                    mTextView.append(mt4+"\n");
                    break;
                case 5:
                    if(bshan==1) {
                        String info = "";
                        for (int j = 0; j < 24; j++) {
                            info = info + shan.substring(j, j + 1) + ": " + ShanMap.get(shan.substring(j, j + 1)) + "\t\t\t\t\t";
                            if (j % 3 == 2)
                                info = info + "\n";
                        }
                        mTextView.setText(info);
                    }else{
                        String info = "";
                        for (int j = 0; j < 24; j++) {
                            info = info + shui.substring(j, j + 1) + ": " + ShuiMap.get(shui.substring(j, j + 1)) + "\t\t\t\t\t";
                            if (j % 3 == 2)
                                info = info + "\n";
                        }
                        mTextView.setText(info);
                    }
                    break;
                default:

                    break;
            }
        }

        ;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(R.string.base_permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_CONTACTS,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE)
                .subscribe(new Subscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o.equals(true)){
                            //  Toast.makeText(MainActivity.this,"请求权限成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"请求权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                });
        initViews();
        initPaintMode();
        loadImage();
    }
    private void initViews() {
        mDrawingView = (DrawingView) findViewById(R.id.img_screenshot);
        mTextView = (TextView)findViewById(R.id.tvmessage);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvReDraw = (TextView)findViewById(R.id.tvredraw);
        mTvReDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDrawingView.ReDrawImage();
            }
        });
        mTvToShui = (TextView)findViewById(R.id.tv_to_shui);
        mTvToShui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDrawingView.setPenColor(getColor(R.color.blue));
                mDrawingView.ToShuiDrawImage();
            }
        });
        mTvShanShuiSwitch = (TextView)findViewById(R.id.tv_shan_shui_switch);
        mTvShanShuiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(bshan == 1) {
                    mDrawingView.setPenColor(getColor(R.color.blue));
                    bshan = 0;
                }
                else{
                    mDrawingView.setPenColor(getColor(R.color.red));
                    bshan = 1;
                }
                mDrawingView.ReDrawImage();
            }
        });
        mBrush = (ImageButton) findViewById(R.id.brush);
        mColorPanel = (ImageButton) findViewById(R.id.color_panel);
        mUndo = (ImageButton) findViewById(R.id.undo);
        mSave = (ImageButton) findViewById(R.id.save);

        mBrush.setOnClickListener(this);
        mColorPanel.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mSave.setOnClickListener(this);
        initPaintMode();
        for(int i=0;i<24;i++) {
            ShanMap.put(shan.substring(i, i + 1), 0);
            ShuiMap.put(shui.substring(i, i + 1), 0);
        }
    }

    private void initPaintMode() {
        mDrawingView.initializePen();
        mDrawingView.setPenSize(10);
        mDrawingView.setPenColor(getColor(R.color.red));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.brush:
                mBrush.setImageResource(BRUSH == 0 ? R.drawable.ic_brush : R.drawable.ic_pen);
                mDrawingView.setPenSize(BRUSH == 0 ? 40 : 10);
                BRUSH = 1 - BRUSH;
                break;
            case R.id.color_panel:
                mColorPanel.setImageResource(COLOR_PANEL == 0 ? R.drawable.ic_color_blue : R.drawable.ic_color_red);
                mDrawingView.setPenColor(COLOR_PANEL == 0 ? getColor(R.color.blue) : getColor(R.color.red));
                COLOR_PANEL = 1 - COLOR_PANEL;
                break;
            case R.id.undo:
                mDrawingView.undo();
                break;
            case R.id.save:
                String sdcardPath = Environment.getExternalStorageDirectory().toString();
                if (mDrawingView.saveImage(sdcardPath, "DrawImg", Bitmap.CompressFormat.PNG, 100)){
                    Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void loadImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.fs);
        mDrawingView.loadImage(bitmap);
    }
}
