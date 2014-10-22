package com.imranyousuf.fsw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by imranyousuf on 10/11/14.
 */
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the demo app activity to complete the install of the deck of cards applet
        Intent launchIntent= new Intent(context, homepage.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(launchIntent);
    }
}
