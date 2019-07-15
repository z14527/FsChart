package com.gyq.fschart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class DrawingView extends View {
    private static final String TAG = "DrawingView";
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Bitmap mOriginBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private boolean mDrawMode;
    private float mX, mY;
    private float mProportion = 0;
    private LinkedList<DrawPath> savePath;
    private DrawPath mLastDrawPath;
    private Matrix matrix;
    private float mPaintBarPenSize;
    private int mPaintBarPenColor;
    private List<Float> fsList = new ArrayList<Float>();
    private float x0 = 726;
    private float y0 = 651;
    private float r = (1090-726);

    public DrawingView(Context c) {
        this(c, null);
    }

    public DrawingView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DrawingView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init();
    }


    private void sendMsg(String s,int type) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.clear();
        bundle.putString("send", s);
        msg.setData(bundle);
        msg.what = type;
        MainActivity.handler.sendMessage(msg);
    }


    private void init() {
      //  sendMsg("init: ");
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mDrawMode = false;
        savePath = new LinkedList<>();
        matrix = new Matrix();
        String logPath = Environment.getExternalStorageDirectory().getPath()+"/download/";

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {
            if ((mBitmap.getHeight() > heightSize) && (mBitmap.getHeight() > mBitmap.getWidth())) {
                widthSize = heightSize * mBitmap.getWidth() / mBitmap.getHeight();
            } else if ((mBitmap.getWidth() > widthSize) && (mBitmap.getWidth() > mBitmap.getHeight())) {
                heightSize = widthSize * mBitmap.getHeight() / mBitmap.getWidth();
            } else {
                heightSize = mBitmap.getHeight();
                widthSize = mBitmap.getWidth();
            }
        }
   //     sendMsg("onMeasure: heightSize: " + heightSize + " widthSize: " + widthSize);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据图片尺寸缩放图片，同样只考虑了高大于宽的情况
        float proportion = (float) canvas.getHeight() / mBitmap.getHeight();
     //   sendMsg("canvas.getHeight()="+canvas.getHeight());
     //   sendMsg("mBitmap.getHeight()="+mBitmap.getHeight());
     //   sendMsg("proportion="+proportion);
        if (proportion < 1) {
            mProportion = proportion;
            matrix.reset();
            matrix.postScale(proportion, proportion);
            matrix.postTranslate((canvas.getWidth() - mBitmap.getWidth() * proportion) / 2, 0);
            canvas.drawBitmap(mBitmap, matrix, mBitmapPaint);
        } else {
            mProportion = 0;
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果你的界面有多个模式，你需要有个变量来判断当前是否可draw
        if (!mDrawMode) {
            return false;
        }
        float x;
        float y;
        if (mProportion != 0) {
            x = (event.getX()) / mProportion;
            y = event.getY() / mProportion;
        } else {
            x = event.getX();
            y = event.getY();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // This happens when we undo a path
                if (mLastDrawPath != null) {
                    mPaint.setColor(mPaintBarPenColor);
                    mPaint.setStrokeWidth(mPaintBarPenSize);
                }
                mPath = new Path();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                mCanvas.drawPath(mPath, mPaint);
        //        sendMsg("x="+x+" y="+y);
                 break;
            case MotionEvent.ACTION_MOVE:
                float dx = abs(x - mX);
                float dy = abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                    for (int k = 0; k < 6; k++) {
                        double theta = abs(180 * Math.atan((y - y0) / (x - x0)) / PI);
                        if (k * 15 < theta && (k + 1) * 15 > theta) {
                            double r1 = sqrt((y - y0) * (y - y0) + (x - x0) * (x - x0));
                            int k1 = k;
                            if (x < x0 && y > y0)
                                k1 = 11 - k1;
                            if (x < x0 && y < y0)
                                k1 = 12 + k1;
                            if (x > x0 && y < y0)
                                k1 = 23 - k1;
                            sendMsg(k1 + ":" + (int) (r1), 3);
                            break;
                        }
                    }
                }
                mCanvas.drawPath(mPath, mPaint);
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                mLastDrawPath = new DrawPath(mPath, mPaint.getColor(), mPaint.getStrokeWidth());
                savePath.add(mLastDrawPath);
                mPath = null;
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    public void initializePen() {
        mDrawMode = true;
        mPaint = null;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @Override
    public void setBackgroundColor(int color) {
        mCanvas.drawColor(color);
        super.setBackgroundColor(color);
    }

    /**
     * This method should ONLY be called by clicking paint toolbar(outer class)
     */
    public void setPenSize(float size) {
        mPaintBarPenSize = size;
        mPaint.setStrokeWidth(size);
    }

    public float getPenSize() {
        return mPaint.getStrokeWidth();
    }

    /**
     * This method should ONLY be called by clicking paint toolbar(outer class)
     */
    public void setPenColor(@ColorInt int color) {
        mPaintBarPenColor = color;
        mPaint.setColor(color);
    }

    public
    @ColorInt
    int getPenColor() {
        return mPaint.getColor();
    }

    /**
     * @return 当前画布上的内容
     */
    public Bitmap getImageBitmap() {
        return mBitmap;
    }

    public void loadImage(Bitmap bitmap) {
        mOriginBitmap = bitmap;
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mPath.reset();
        mPath.moveTo(x0, y0);
        for(int i=0;i<24;i++){
            float x1 = (float)(x0 + r*cos(i*PI/12));
            float y1 = (float)(y0 + r*sin(i*PI/12));
            mPath.lineTo(x1,y1);
            mCanvas.drawPath(mPath, mPaint);
        }
        invalidate();
    }

    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            // 清空画布
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            loadImage(mOriginBitmap);

            savePath.removeLast();

            // 将路径保存列表中的路径重绘在画布上 遍历绘制
            for (DrawPath dp : savePath) {
                mPaint.setColor(dp.getPaintColor());
                mPaint.setStrokeWidth(dp.getPaintWidth());
                mCanvas.drawPath(dp.path, mPaint);
            }
            invalidate();
        }
    }

    /**
     * 保存图片，其实我个人建议在其他类里面写保存方法，{@link #getImageBitmap()}就是内容
     *
     * @param filePath 路径名
     * @param filename 文件名
     * @param format   存储格式
     * @param quality  质量
     * @return 是否保存成功
     */
    public boolean saveImage(String filePath, String filename, Bitmap.CompressFormat format,
                             int quality) {
        if (quality > 100) {
            return false;
        }
        File file;
        FileOutputStream out = null;
        try {
            switch (format) {
                case PNG:
                    file = new File(filePath, filename + ".png");
                    out = new FileOutputStream(file);
                    return mBitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
                case JPEG:
                    file = new File(filePath, filename + ".jpg");
                    out = new FileOutputStream(file);
                    return mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                default:
                    file = new File(filePath, filename + ".png");
                    out = new FileOutputStream(file);
                    return mBitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 路径对象
     */
    private class DrawPath {
        Path path;
        int paintColor;
        float paintWidth;

        DrawPath(Path path, int paintColor, float paintWidth) {
            this.path = path;
            this.paintColor = paintColor;
            this.paintWidth = paintWidth;
        }

        int getPaintColor() {
            return paintColor;
        }

        float getPaintWidth() {
            return paintWidth;
        }
    }
    public boolean panduan(Point a, Point b, Point c, Point p) {
        double abc = triangleArea(a, b, c);
        double abp = triangleArea(a, b, p);
        double acp = triangleArea(a, c, p);
        double bcp = triangleArea(b, c, p);
        if (abc == abp + acp + bcp) {
            return true;
        } else {
            return false;
        }
    }

    private double triangleArea(Point a, Point b, Point c) {// 返回三个点组成三角形的面积
        double result = abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
                - c.x * b.y - a.x * c.y) / 2.0D);
        return result;
    }

}
