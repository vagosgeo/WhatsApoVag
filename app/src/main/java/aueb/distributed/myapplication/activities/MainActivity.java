package aueb.distributed.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.IOException;
import java.net.UnknownHostException;

import aueb.distributed.myapplication.R;
import aueb.distributed.myapplication.UserNodeImpl;
import aueb.distributed.myapplication.UserNodeInfo;
import aueb.distributed.myapplication.BrokerImpl;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.*;
public class MainActivity extends AppCompatActivity {

    //permissions
    private static int CAMERA_PERMISSION = 100;
    private static int STORAGE_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Permissions for App*/
        getCameraPermission();
        getStoragePermission();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                Intent mInHome = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(mInHome);
                MainActivity.this.finish();
            }
        }, 5000);
    }


    private void getCameraPermission () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            Log.d("CAMERA_PERMISSION", "Asked for Camera Permission.");
        }
    }

    private void getStoragePermission () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            Log.d("STORAGE_PERMISSION", "Asked for Storage Permission.");
        }
    }
}

