package com.imranyousuf.fsw.drawings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Boolean _run;
    protected DrawThread thread;
    private Bitmap mBitmap;
    public boolean isDrawing = true;

    private ClickSystem clickSystem;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);


        clickSystem = new ClickSystem();
        thread = new DrawThread(getHolder());
    }


    class DrawThread extends  Thread{
        private SurfaceHolder mSurfaceHolder;


        public DrawThread(SurfaceHolder surfaceHolder){
            mSurfaceHolder = surfaceHolder;

        }

        public void setRunning(boolean run) {
            _run = run;
        }


        @Override
        public void run() {
            Canvas canvas = null;
            while (_run){
                if(isDrawing == true){
                    try{
                        canvas = mSurfaceHolder.lockCanvas(null);
                        if(mBitmap == null){
                            mBitmap =  Bitmap.createBitmap (1, 1, Bitmap.Config.ARGB_8888);
                        }
                        final Canvas c = new Canvas (mBitmap);

                        c.drawColor(0, PorterDuff.Mode.CLEAR);
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        
                        clickSystem.executeAll(c);

                        canvas.drawBitmap (mBitmap, 0,  0,null);
                    } finally {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    isDrawing = false;
                }

            }

        }
    }


    public void addDrawingPath (DrawingUndo drawingUndo){
        clickSystem.addCommand(drawingUndo);
    }

    public boolean hasMoreRedo(){
        return clickSystem.hasMoreRedo();
    }

    public void redo(){
        isDrawing = true;
        clickSystem.redo();


    }

    public void undo(){
        isDrawing = true;
        clickSystem.undo();
    }

    public boolean hasMoreUndo(){
        return clickSystem.hasMoreUndo();
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,  int height) {
        // TODO Auto-generated method stub
        mBitmap =  Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);;
    }


    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

}
