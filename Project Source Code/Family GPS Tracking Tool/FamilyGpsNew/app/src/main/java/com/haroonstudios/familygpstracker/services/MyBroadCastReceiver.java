package com.haroonstudios.familygpstracker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, LocationShareService.class));
    }
}
