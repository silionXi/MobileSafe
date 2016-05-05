package com.silion.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
    private SharedPreferences mPref;
    private LocationManager mLocationManager;
    private String mBestProvider;

    public LocationService() {
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude(); //经度
            double latitude = location.getLatitude(); //纬度
            double altitude = location.getAltitude(); //高度
            float accuracy = location.getAccuracy(); //精度
            String result = "经度-" + longitude + ", 纬度-" + latitude + ", 高度-" + altitude + ", 精度-" + accuracy;
            mPref.edit().putString("last_location", result).commit();

            String phone = mPref.getString("security_contact", "18928818247");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, result, null, null);

            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mPref = getSharedPreferences("setting", MODE_PRIVATE);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mBestProvider = mLocationManager.getBestProvider(criteria, true);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationManager.requestLocationUpdates(mBestProvider, 0, 0, mLocationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(mLocationListener);
        super.onDestroy();
    }
}
