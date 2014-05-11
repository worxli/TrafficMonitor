package edu.washington.cs.trafficmonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class SingleAppActivity extends Activity {
	
	private int uuid;
	private int pid;
	private String name;
	private LogService ls;
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleappactivity);
		
		this.uuid = this.getIntent().getExtras().getInt("uuid");
		this.name = this.getIntent().getExtras().getString("name");
		this.pid = this.getIntent().getExtras().getInt("pid");
		
		TextView name = (TextView) findViewById(R.id.name);
		TextView uuid = (TextView) findViewById(R.id.uuid);
		name.setText("Process name: "+this.name);
		uuid.setText("UUID: "+this.uuid);
		
		ListView listView = (ListView) findViewById(R.id.byteView);
		
		List<String> byteList = new ArrayList<String>();
		byteList.add("Total Bytes received: "+TrafficStats.getUidRxBytes(this.uuid));
		byteList.add("Total Bytes transmitted: "+TrafficStats.getUidTxBytes(this.uuid));
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.appitem, byteList);
        listView.setAdapter(adapter);
        
	}
	
	public void startLog(View v){
		
		if(ls!=null){
			Log.d("Log:", "already started");
		} else {
			ls = new LogService(this.uuid, this.pid, 1000, this.name, getApplicationContext());
			ls.start();
		}
		
	}
	
	public void stopLog(View v){
		
		if(ls!=null){
			ls.alive = false;
			ls.interrupt();
		}
		
		ls=null;
		
	}
}
