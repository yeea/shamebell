package net.squidinc.shamebell;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ShameBellActivity extends AppCompatActivity {

  private static final String TAG = "ShameBellActivity";
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private ShakeDetector mShakeDetector;
  private MediaPlayer mpShame = null;
  private MediaPlayer mpDing = null;
  //  private boolean isMixModeOn = false;

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
        mpShame.setVolume( 0.5f, 0.5f );
        if ( !mpShame.isPlaying() ) {
          mpShame.start();
        } else {
          mpShame.stop();
          mpShame.reset();
          AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd( R.raw.shame );
          if ( afd == null ) {
            return;
          }
          try {
            mpShame.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength() );
            afd.close();
            mpShame.prepare();
            mpShame.start();
          } catch ( IOException e ) {
            e.printStackTrace();
            Log.w( TAG, "Couldn't shame: " + e.getMessage() );
          }

          // TODO cross-fade option
        }
      }
    } );

    // ShakeDetector initialization
    mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
    mAccelerometer = mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
    mShakeDetector = new ShakeDetector();
    mShakeDetector.setOnShakeListener( new ShakeDetector.OnShakeListener() {

      @Override
      public void onShake( int count ) {
        mpDing.setVolume( 0.5f, 0.5f );
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

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate( R.menu.shame_menu, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {

    switch ( item.getItemId() ) {
      case R.id.settings:
        // Open dialog with mixer options
        //            isMixModeOn = true;
        Toast.makeText( this, "Tapped for sound mixer", Toast.LENGTH_SHORT ).show();
        return true;
      default:
        return super.onOptionsItemSelected( item );
    }
  }
}
