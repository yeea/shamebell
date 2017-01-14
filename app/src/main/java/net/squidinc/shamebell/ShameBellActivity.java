package net.squidinc.shamebell;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class ShameBellActivity extends AppCompatActivity {

  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private ShakeDetector mShakeDetector;
  private MediaPlayer mpShame = null;
  private MediaPlayer mpDing = null;

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_shame_bell );

    ImageView shameBell = (ImageView) findViewById( R.id.bell );

    mpShame = MediaPlayer.create( this, R.raw.shame );
    mpDing = MediaPlayer.create( this, R.raw.bell );

    shameBell.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick( View v ) {
        // Shame!
        mpShame.start();
      }
    } );

    // ShakeDetector initialization
    mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
    mAccelerometer = mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
    mShakeDetector = new ShakeDetector();
    mShakeDetector.setOnShakeListener( new ShakeDetector.OnShakeListener() {

      @Override
      public void onShake( int count ) {
        mpDing.start();
      }
    } );
  }

  @Override
  public void onResume() {
    super.onResume();
    mSensorManager.registerListener( mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI );
  }

  @Override
  public void onPause() {
    mSensorManager.unregisterListener( mShakeDetector );
    super.onPause();
  }
}
