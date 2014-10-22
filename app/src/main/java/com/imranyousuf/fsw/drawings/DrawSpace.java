package com.imranyousuf.fsw.drawings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.imranyousuf.fsw.R;
import com.imranyousuf.fsw.drawings.brush.Brush;
import com.imranyousuf.fsw.drawings.brush.CircleBrush;
import com.imranyousuf.fsw.drawings.brush.PenBrush;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.gmail.yuyang226.flickrj.sample.android.FlickrjActivity;
import com.gmail.yuyang226.flickrj.sample.android.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.gmail.yuyang226.flickrj.sample.android.FlickrjActivity;
import com.imranyousuf.fsw.homepage;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.FileOutputStream;

public class DrawSpace extends Activity implements View.OnTouchListener{
    private DrawingSurface drawingSurface;
    private DrawingUndo currentDrawingUndo;
    private Paint currentPaint;

    private Button redoBtn;
    private Button undoBtn;

    private Brush currentBrush;

    private File APP_FILE_PATH = new File("storage/sdcard0/Pictures");

    


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_activity);

        Button btnFlickr = (Button) findViewById(R.id.btnUpload);



        Button btnPick = (Button) findViewById(R.id.btnPick);
        btnPick.setOnClickListener(mPickClickListener);

        setCurrentPaint();
        currentBrush = new PenBrush();

        drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);
        drawingSurface.setOnTouchListener(this);

        redoBtn = (Button) findViewById(R.id.redoBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);

        redoBtn.setEnabled(false);
        undoBtn.setEnabled(false);

    }

    File fileUri;

    View.OnClickListener mPickClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(intent, 102);
        }

    };

    public void shareOnClick(View paramView)
    {
       // if (((DrawingView)findViewById(R.id.DrawSpace)).save())
        //{
            if (this.fileUri == null)
                this.fileUri = new File("/mnt/sdcard/fsm.png");
            Intent localIntent = new Intent(getApplicationContext(), FlickrjActivity.class);
            localIntent.putExtra("flickImagePath", this.fileUri.getAbsolutePath());
            startActivity(localIntent);
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        REST localREST = new REST();
                        localREST.setHost("www.flickr.com");
                        Flickr localFlickr = new Flickr("d74f5a4a5db17e60f326e8666a4dd518", localREST);
                        SearchParameters localSearchParameters = new SearchParameters();
                        localSearchParameters.setSort(SearchParameters.INTERESTINGNESS_DESC);
                        localSearchParameters.setTags(new String[] { "cs160fsm" });
                        PhotoList localPhotoList = localFlickr.getPhotosInterface().search(localSearchParameters, 20, 1);
                        if (localPhotoList != null)
                        {
                            Bitmap localBitmap = BitmapFactory.decodeStream(((Photo)localPhotoList.get(new Random().nextInt(localPhotoList.size()))).getMediumAsStream());
                            Intent localIntent = new Intent(DrawSpace.this.getApplicationContext(), homepage.class);
                            localIntent.putExtra("fetchedImage", Bitmap.createScaledBitmap(localBitmap, 250, 288, false));
                            DrawSpace.this.startActivityForResult(localIntent, 104);
                        }
                        return;
                    }
                    catch (ParserConfigurationException localParserConfigurationException)
                    {
                        localParserConfigurationException.printStackTrace();
                        return;
                    }
                    catch (FlickrException localFlickrException)
                    {
                        localFlickrException.printStackTrace();
                        return;
                    }
                    catch (IOException localIOException)
                    {
                        localIOException.printStackTrace();
                        return;
                    }
                    catch (JSONException localJSONException)
                    {
                        localJSONException.printStackTrace();
                    }
                }
            }
                    .start();
        }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102) {

            if (resultCode == Activity.RESULT_OK) {
                Uri tmp_fileUri = data.getData();

                ((ImageView) findViewById(R.id.drawingSurface))
                        .setImageURI(tmp_fileUri);

                String selectedImagePath = getPath(tmp_fileUri);
                fileUri = new File(selectedImagePath);
            }

        }
    };

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void setCurrentPaint(){
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(0xFFFFFF00);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);

    }




    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            currentDrawingUndo = new DrawingUndo();
            currentDrawingUndo.paint = currentPaint;
            currentDrawingUndo.path = new Path();
            currentBrush.mouseDown(currentDrawingUndo.path, motionEvent.getX(), motionEvent.getY());


        }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            currentBrush.mouseMove( currentDrawingUndo.path, motionEvent.getX(), motionEvent.getY() );

        }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            currentBrush.mouseUp( currentDrawingUndo.path, motionEvent.getX(), motionEvent.getY() );
            

            drawingSurface.addDrawingPath(currentDrawingUndo);
            drawingSurface.isDrawing = true;
            undoBtn.setEnabled(true);
            redoBtn.setEnabled(false);
        }

        return true;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.colorRedBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(0xFFFF0000);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                break;
            case R.id.colorWhiteBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(0xFFFFFFFF);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                break;

            case R.id.colorBlueBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(0xFF00FF00);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                break;
            case R.id.colorGreenBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(0xFF0000FF);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                break;

            case R.id.undoBtn:
                drawingSurface.undo();
                if (drawingSurface.hasMoreUndo() == false) {
                    undoBtn.setEnabled(false);
                }
                redoBtn.setEnabled(true);
                break;

            case R.id.redoBtn:
                drawingSurface.redo();
                if (drawingSurface.hasMoreRedo() == false) {
                    redoBtn.setEnabled(false);
                }

                undoBtn.setEnabled(true);
                break;
            case R.id.saveBtn:
                final Activity currentActivity = this;
                Handler saveHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(currentActivity).create();
                        alertDialog.setTitle("Hurray!!");
                        alertDialog.setMessage("Your drawing had been saved :)");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        alertDialog.show();
                    }
                };
                new ExportBitmapToFile(this, saveHandler, drawingSurface.getBitmap()).execute();
                break;
            case R.id.circleBtn:
                currentBrush = new CircleBrush();
                break;


        }


    }


    private class ExportBitmapToFile extends AsyncTask<Intent,Void,Boolean> {
        private Context mContext;
        private Handler mHandler;
        private Bitmap nBitmap;

        public ExportBitmapToFile(Context context,Handler handler,Bitmap bitmap) {
            mContext = context;
            nBitmap = bitmap;
            mHandler = handler;
        }

        @Override
        protected Boolean doInBackground(Intent... arg0) {
            try {
                if (!APP_FILE_PATH.exists()) {
                    APP_FILE_PATH.mkdirs();
                }

                final FileOutputStream out = new FileOutputStream(new File(APP_FILE_PATH + "/ImranYo.png"));
                nBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
            //mHandler.post(completeRunnable);
            return false;
        }


        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if ( bool ){
                mHandler.sendEmptyMessage(1);
            }
        }
    }
}