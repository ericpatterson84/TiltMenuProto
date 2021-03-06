package com.example.epatterson.tiltmenuproto;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by epatterson on 10/14/2017.
 */

public class TwoWayTiltManager implements SensorEventListener {

    private TwoWayTiltListener twoWayTiltListener = null;
    private SensorManager sensorManager = null;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(
                rotationMatrix, sensorEvent.values);

        // Convert to orientations
        float[] orientations = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientations);

        for(int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }

        if(twoWayTiltListener != null)
        {
            // a wrist twisting motion is movement along the x-axis assuming the neutral
            // device orientation is parallel to the ground
            // index 1 is x-axis rotation
            twoWayTiltListener.onTiltAngleChanged(orientations[1]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public TwoWayTiltManager(SensorManager sensorMgr, TwoWayTiltListener twoWayListener)
    {
        twoWayTiltListener = twoWayListener;
        sensorManager = sensorMgr;
    }

    public boolean startTiltRecord()
    {
        Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Register it
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(rotationVectorSensor == null) {
            Log.e("Proto", "Proximity sensor not available.");
            return false;
        }

        return true;
    }

    public void stopTiltRecord()
    {
        sensorManager.unregisterListener(this);
    }
}
