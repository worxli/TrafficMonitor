package edu.washington.cs.trafficmonitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private ListView listView;
	private ArrayAdapter<String> adapter;
	public List<RunningAppProcessInfo> runningProcesses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.listView = (ListView) findViewById(R.id.listView);
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		runningProcesses = manager.getRunningAppProcesses();
		
		List<String> processInfo = new ArrayList<String>();
		for (RunningAppProcessInfo process : runningProcesses) {
			processInfo.add(process.processName+" PID: "+process.pid+" UID: "+process.uid);
		}
		
		adapter = new ArrayAdapter<String>(this, R.layout.appitem, processInfo);
        this.listView.setAdapter(adapter);
        
        this.listView.setClickable(true);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
        	@Override
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
        		showDetails(runningProcesses.get(position));
            }
       });
		
	}
	
	private void showDetails(RunningAppProcessInfo app) {
		
		Intent intent = new Intent(this, SingleAppActivity.class);
    	intent.putExtra("uuid", app.uid);
    	intent.putExtra("name", app.processName);
    	intent.putExtra("pid", app.pid);
    	this.startActivity(intent);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         
        switch (item.getItemId()) {
        case R.id.update:
        	updateAdapter();
            return true;
            
        }
		return false;
    }

	private void updateAdapter() {
		
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
		
		List<String> processInfo = new ArrayList<String>();
		for (RunningAppProcessInfo process : runningProcesses) {
			processInfo.add(process.processName+" PID: "+process.pid+" UID: "+process.uid);
		}
		
		adapter.notifyDataSetChanged();
		
		
	}
}
