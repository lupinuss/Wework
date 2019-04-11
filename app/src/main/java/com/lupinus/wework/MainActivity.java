package com.lupinus.wework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deviceInfo = DeviceInfo.getDeviceInfo(MainActivity.this);
                simple(MainActivity.this, deviceInfo);
            }
        });

        findViewById(R.id.loaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AMapActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit(MainActivity.this);
            }
        });

        findViewById(R.id.config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("HookInfo",MODE_WORLD_READABLE);

                String latitude = sp.getString("latitude","");
                String longitude = sp.getString("longitude","");
                int lac = sp.getInt("lac",0);
                int cid = sp.getInt("cid",0);
                String bssid = sp.getString("bssid","");
                String ssid = sp.getString("ssid","");

                StringBuffer sb = new StringBuffer();
                sb.append("纬度:").append(latitude).append("\n");
                sb.append("经度:").append(longitude).append("\n");
                sb.append("小区号：").append(lac).append("\n");
                sb.append("基站号：").append(cid).append("\n");
                sb.append("Wi-Fi名称:").append(ssid).append("\n");
                sb.append("路由器地址:").append(bssid).append("\n");

                simple(MainActivity.this,sb.toString());
            }
        });
    }

    private void simple(Context context, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.create().show();

    }

    private void edit(Context context) {

        View view = getLayoutInflater().inflate(R.layout.dialog_wifi, null, false);

        final TextInputEditText BSSID = view.findViewById(R.id.bssid);//mac
        final TextInputEditText SSID = view.findViewById(R.id.ssid);//name

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        builder.setTitle("提示");

        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String bssid = BSSID.getText().toString().trim();
                String ssid = SSID.getText().toString().trim();

                SharedPreferences sp = getSharedPreferences("HookInfo",MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("bssid",bssid);
                editor.putString("ssid",ssid);
                editor.commit();

            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();

    }


    public native String stringFromJNI();
}
