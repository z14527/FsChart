package com.gyq.fschart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawingView mDrawingView;
    private static int COLOR_PANEL = 0;
    private static int BRUSH = 0;
    private ImageButton mColorPanel;
    private ImageButton mBrush;
    private ImageButton mUndo;
    private ImageButton mSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initPaintMode();
        loadImage();
    }
    private void initViews() {
        mDrawingView = (DrawingView) findViewById(R.id.img_screenshot);
        mBrush = (ImageButton) findViewById(R.id.brush);
        mColorPanel = (ImageButton) findViewById(R.id.color_panel);
        mUndo = (ImageButton) findViewById(R.id.undo);
        mSave = (ImageButton) findViewById(R.id.save);

        mBrush.setOnClickListener(this);
        mColorPanel.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mSave.setOnClickListener(this);
        initPaintMode();
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
