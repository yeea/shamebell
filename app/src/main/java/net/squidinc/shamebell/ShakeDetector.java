package net.squidinc.shamebell;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Date: 1/12/2017
 * Modified version of detector from http://jasonmcreynolds.com/?p=388
 */

public class ShakeDetector implements SensorEventListener {

  private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
  private static final int SHAKE_SLOP_TIME_MS = 300;
  private static final int SHAKE_COUNT_RESET_TIME_MS = 2000;

  private OnShakeListener mListener;
  private long mShakeTimestamp;
  private int mShakeCount;

  public void setOnShakeListener( OnShakeListener listener ) {
    this.mListener = listener;
  }

  public interface OnShakeListener {
    void onShake( int count );
  }

  @Override
  public void onSensorChanged( SensorEvent sensorEvent ) {
    if ( mListener != null ) {
      double x = sensorEvent.values[0];
      double y = sensorEvent.values[1];
      double z = sensorEvent.values[2];

      double gX = x / SensorManager.GRAVITY_EARTH;
      double gY = y / SensorManager.GRAVITY_EARTH;
      double gZ = z / SensorManager.GRAVITY_EARTH;

      // gForce will be close to 1 when there is no movement.
      double gForce = Math.sqrt( gX * gX + gY * gY + gZ * gZ );

      if ( gForce > SHAKE_THRESHOLD_GRAVITY ) {
        final long now = System.currentTimeMillis();
        // ignore shake events too close to each other (300ms)
        if ( mShakeTimestamp + SHAKE_SLOP_TIME_MS > now ) {
          return;
        }

        // reset the shake count after 2 seconds of no shakes
        if ( mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now ) {
          mShakeCount = 0;
        }

        mShakeTimestamp = now;
        mShakeCount++;

        mListener.onShake( mShakeCount );
      }
    }
  }

  @Override
  public void onAccuracyChanged( Sensor sensor, int i ) {
    // Nothing here
  }
}
