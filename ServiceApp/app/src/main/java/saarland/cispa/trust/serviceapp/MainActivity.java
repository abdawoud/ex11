package saarland.cispa.trust.serviceapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.best.uploadservice.UploaderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String grantAction = "saarland.cispa.trust.intent.service.GRANT_ACCESS_TO_ITEM";
        Intent intent  = getIntent();

        if (intent.getAction().equals(grantAction)) {
            grantUriPermission(intent);
            return;
        }

        registerOperationNotedCallback();

        // Initialize the UploaderService. Do nothing for now!
        new UploaderService(this);

        final Button callHiddenBtn = findViewById(R.id.callHiddenBtn);
        final Button bypassManagerBtn = findViewById(R.id.bypassManagerBtn);

        callHiddenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int appUid = android.os.Process.myUid();
                String successResult = "App not allowed to read or update stored WiFi Ap config (uid = " + appUid + ")";
                String a = callHidden().equals(successResult)? "passed" : "failed";
                Log.d("ServiceApp", "FIRST-TASK: " + a);
            }
        });

        bypassManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> successResults = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14));
                String b = successResults.contains(bypassManager())? "passed" : "failed";
                Log.d("ServiceApp", "SECOND-TASK: " + b);
            }
        });

        startTheDummyServiceThatMightBeRemovedLater(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void registerOperationNotedCallback() {
        // @TODO: Implement
    }

    private void grantUriPermission(Intent intent) {
        Bundle extras = intent.getExtras();
        int id = extras.getInt("ID", -1);
        String packageName = extras.getString("PACKAGE");

        Intent returnIntent = new Intent();

        // do nothing

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private String callHidden() {
        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String result = "";

        Class wifiManagerClass = wifiManager.getClass();
        try {
            // do nothing
        } catch (Exception e) {
            if (e.getCause() != null)
                result = e.getCause().getMessage();
            else
                result = e.getMessage();
        } finally {
            return result;
        }
    }

    private int bypassManager() {
        WifiManager wifiManager = 
                (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        int result = -1;
        Class wifiManagerClass = wifiManager.getClass();
        try {
            // do nothing
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    private void startTheDummyServiceThatMightBeRemovedLater(Context context) {
        Intent intent = new Intent("android.intent.action.START_DUMMY_SERVICE_THAT_MIGHT_BE_REMOVED_LATER");
        intent.setComponent(new ComponentName(context, DummyServiceMayRemoveLater.class));
        context.startService(intent);
    }
}
