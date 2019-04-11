package com.lupinus.wework;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class AMapActivity extends AppCompatActivity implements AMap.OnMapClickListener {

    private MapView mv;
    private AMap aMap;
    private LatLng latLng;
    private int lac = 0, cid = 0;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        sp = getSharedPreferences("HookInfo",MODE_WORLD_READABLE);

        mv = findViewById(R.id.mv);
        assert mv != null;
        mv.onCreate(savedInstanceState);
        aMap = mv.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setOnMapClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (latLng == null) {
                    Toast.makeText(this, "请点击地图选择一个地点！", Toast.LENGTH_SHORT).show();
                    return true;
                }
                new AlertDialog.Builder(AMapActivity.this).setTitle("注意").setMessage("如果打卡时显示不在范围内，请填写基站信息")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("package_name", pacakgeName);
//                contentValues.put("latitude", latLng.latitude);
//                contentValues.put("longitude", latLng.longitude);
//                contentValues.put("lac", lac);
//                contentValues.put("cid", cid);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("latitude",String.valueOf(latLng.latitude));
                editor.putString("longitude",String.valueOf(latLng.longitude));
                editor.putInt("lac",lac);
                editor.putInt("cid",cid);
                editor.commit();

                break;
            case R.id.search:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null, false);
                final EditText et_key = view.findViewById(R.id.key);
                new AlertDialog.Builder(this).setView(view)
                        .setTitle("搜索位置")
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                search(et_key.getText().toString());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.lac:
                View view1 = getLayoutInflater().inflate(R.layout.dialog_lac_cid, null, false);
                final TextInputEditText etLac = view1.findViewById(R.id.lac);
                final TextInputEditText etCid = view1.findViewById(R.id.cid);
                new AlertDialog.Builder(AMapActivity.this).setTitle("填写基站信息").setView(view1).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canCloseDialog(dialog, false);
                        if (TextUtils.isEmpty(etLac.getText())) {
                            etLac.setError("lac的值不应该为空");
                        }
                        if (TextUtils.isEmpty(etCid.getText())) {
                            etCid.setError("cid的值不应该为空");
                        }
                        if (!TextUtils.isEmpty(etLac.getText()) && !TextUtils.isEmpty(etCid.getText())) {
                            int lac1 = Integer.parseInt(etLac.getText().toString());
                            int cid1 = Integer.parseInt(etCid.getText().toString());
                            if (lac1 <= 0 || lac1 >= 65535) {
                                etLac.setError("lac的值应该是0~65535");
                                lac1 = 0;
                            }
                            if (cid1 <= 0 || cid1 >= 65535) {
                                etCid.setError("cid的值应该是0~65535");
                                cid1 = 0;
                            }
                            lac = lac1;
                            cid = cid1;
                        }
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put("package_name", pacakgeName);
//                        contentValues.put("lac", lac);
//                        contentValues.put("cid", cid);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lac",lac);
                        editor.putInt("cid",cid);
                        editor.commit();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    private void search(final String key) {
        PoiSearch.Query query = new PoiSearch.Query(key, null, null);
        query.setPageSize(10);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i == 1000) {
                    final ArrayList<PoiItem> poiItems = poiResult.getPois();
                    if (poiItems.size() != 0) {
                        String[] keyList = new String[poiItems.size()];
                        for (int j = 0; j < poiItems.size(); j++) {
                            keyList[j] = poiItems.get(j).getTitle();
                        }
                        new AlertDialog.Builder(AMapActivity.this)
                                .setTitle("选择位置")
                                .setSingleChoiceItems(keyList, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(poiItems.get(which).getLatLonPoint().getLatitude(), poiItems.get(which).getLatLonPoint().getLongitude())));
                                        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel()));
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        Toast.makeText(AMapActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        aMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("经度：" + latLng.longitude + ",纬度：" + latLng.latitude);
        aMap.addMarker(markerOptions);
        this.latLng = latLng;
    }
}
