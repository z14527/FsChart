package com.gyq.fschart;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
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
    private static int COLOR_PANEL = 0;
    private static int BRUSH = 0;
    private ImageButton mColorPanel;
    private ImageButton mBrush;
    private ImageButton mUndo;
    private ImageButton mSave;
    public static Map<String,Integer> map=new HashMap<>();
    public static String fs ="庚申坤未丁午丙巳巽辰乙卯甲寅艮丑癸子壬亥乾戌辛酉";
    private static String lfs = null;

    public static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    String mt = msg.getData().getString("send");
                    if(!mTextView.equals(null)){
                        if(mTextView.getText().length()<1000)
                            mTextView.append("\n"+mt);
                        else
                            mTextView.setText(mt);
                    }
                    break;
                case 3:
                    String mt2 = msg.getData().getString("send");
                    String[] mt3 = mt2.split(":");
                    if(mt3.length>=2){
                        int k = Integer.parseInt(mt3[0]);
                        int r = Integer.parseInt(mt3[1]);
                        if(k>=0 && k<24) {
                            String fs1 = fs.substring(k,k+1);
                            if(fs1.equals(lfs))
                                map.put(fs1,max(map.get(fs1),r));
                            else
                                map.put(fs1,r);
                            lfs = fs1;
                            String info = "";
                            for(int j=0;j<24;j++) {
                                info = info + fs.substring(j,j+1) + ": " + map.get(fs.substring(j,j+1))+"\t\t\t\t\t";
                                if(j%3==2)
                                    info = info +"\n";
                            }
                            mTextView.setText(info);
                        }
                    }
                    break;
                case 4:

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
        mBrush = (ImageButton) findViewById(R.id.brush);
        mColorPanel = (ImageButton) findViewById(R.id.color_panel);
        mUndo = (ImageButton) findViewById(R.id.undo);
        mSave = (ImageButton) findViewById(R.id.save);

        mBrush.setOnClickListener(this);
        mColorPanel.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mSave.setOnClickListener(this);
        initPaintMode();
        for(int i=0;i<24;i++)
            map.put(fs.substring(i,i+1),0);
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
