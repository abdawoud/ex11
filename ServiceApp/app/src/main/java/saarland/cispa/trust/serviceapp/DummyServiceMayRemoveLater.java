package saarland.cispa.trust.serviceapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class DummyServiceMayRemoveLater extends IntentService {
    public DummyServiceMayRemoveLater(String name) {
        super(name);
    }

    public DummyServiceMayRemoveLater() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doStuff();
    }

    private void doStuff() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    telephonyManager.getVoiceMailAlphaTag();
            }
        }, 0, 5000);
    }
}