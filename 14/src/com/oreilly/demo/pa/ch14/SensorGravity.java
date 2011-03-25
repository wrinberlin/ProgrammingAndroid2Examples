package com.oreilly.demo.pa.ch14;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SensorGravity extends Activity implements SensorEventListener {
	
	private boolean hassensor;
	
	private final Handler gravityEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		Bundle data = msg.getData();
																		((TextView) findViewById(R.id.gravityxtext)).setText(data.getString("x"));
																		((TextView) findViewById(R.id.gravityytext)).setText(data.getString("y"));
																		((TextView) findViewById(R.id.gravityztext)).setText(data.getString("z"));
																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getGravitySensors() == null) {
			hassensor = false;
			Toast.makeText(this, "No Gravity Sensors Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		hassensor = true;
		setContentView(R.layout.sensorgravity);
		
		setTitle("Gravity");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(hassensor) registerListener();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterListener();
	}
	
	private List<Sensor> getGravitySensors() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_GRAVITY);
		return list != null && !list.isEmpty() ? list : null;
	}
	
	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getGravitySensors();
		if(list != null) {
			for(Sensor sensor: list) {
				mngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
			}
		}
	}
	
	private void unregisterListener() {
		if(hassensor) {
			SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mngr.unregisterListener(this);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onSensorChanged(SensorEvent event) {
		float gx = event.values[0];
		float gy = event.values.length > 1 ? event.values[1] : 0;  
		float gz = event.values.length > 2 ? event.values[2] : 0;  
		
		Bundle data = new Bundle();
		data.putString("x", "Gravity X: "+gx+" m/s^2");
		data.putString("y", "Gravity Y: "+gy+" m/s^2");
		data.putString("z", "Gravity Z: "+gz+" m/s^2");
		Message msg = Message.obtain();
		msg.setData(data);
		gravityEventHandler.sendMessage(msg);
	}
}
