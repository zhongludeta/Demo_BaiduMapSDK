package com.csg.zhong.demo_baidumapsdk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by zhong on 2017-06-28-0028.
 */

public class MyOrientationListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Context mContext;
    private Sensor mSensor;

    private float mLastX;

    public MyOrientationListener(Context context) {
        mContext = context;
    }

    public void start() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            if (mSensor != null) {
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[SensorManager.DATA_X];
            if (Math.abs(x - mLastX) > 1.0f) {
                mLastX = x;
                if (mOnOrientationChangedListener != null) {
                    mOnOrientationChangedListener.onOrientationChanged(x);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private OnOrientationChangedListener mOnOrientationChangedListener;

    public void setOnOrientationChangedListener(OnOrientationChangedListener onOrientationChangedListener) {
        mOnOrientationChangedListener = onOrientationChangedListener;
    }

    public interface OnOrientationChangedListener {
        void onOrientationChanged(float x);
    }

}
