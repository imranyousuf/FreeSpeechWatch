package com.imranyousuf.fsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.imranyousuf.fsw.drawings.DrawSpace;

public class Main extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
    }

    public void onClick(View view){
        switch (view.getId()){
            case com.imranyousuf.fsw.R.id.drawBtn:
                Intent drawIntent = new Intent(this, DrawSpace.class);
                startActivity( drawIntent);
                break;
        }
    }
}
