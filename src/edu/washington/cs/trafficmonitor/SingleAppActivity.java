package edu.washington.cs.trafficmonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SingleAppActivity extends Activity {
	
	private int uuid;
	private int pid;
	private String name;

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
		
		TrafficStats.getMobileRxBytes();
		
		ListView listView = (ListView) findViewById(R.id.byteView);
		
		List<String> byteList = new ArrayList<String>();
		//byteList.add("Tag: "+TrafficStats.getThreadStatsTag());
		//byteList.add("TCP: "+TrafficStats.getUidTcpRxBytes(this.uuid));
		byteList.add("Total Bytes received: "+TrafficStats.getUidRxBytes(this.uuid));
		//byteList.add("Total Packets: "+TrafficStats.getUidRxPackets(this.uuid));
		//byteList.add("TCP Segments: "+TrafficStats.getUidTcpRxSegments(this.uuid));
		//byteList.add("TCP Bytes: "+TrafficStats.getUidTcpTxBytes(this.uuid));
		//byteList.add("Total Packets: "+TrafficStats.getUidTxPackets(this.uuid));
		byteList.add("Total Bytes transmitted: "+TrafficStats.getUidTxBytes(this.uuid));
		//byteList.add("UDP Packets: "+TrafficStats.getUidUdpTxPackets(this.uuid));
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.appitem, byteList);
        listView.setAdapter(adapter);
        

        //
        ArrayList<Connection> connections = new ArrayList<Connection>();

        try {
          BufferedReader in = new BufferedReader(new FileReader("/proc/" + this.pid + "/net/tcp"));
          String line;

          while((line = in.readLine()) != null) {
            line = line.trim();
            Log.d("TCP","Netstat: " + line);
            String[] fields = line.split("\\s+", 10);
            int fieldn = 0;

            for(String field : fields) {
               //Log.d("TAG","Field " + (fieldn++) + ": [" + field + "]");
            }

            if(fields[0].equals("sl")) {
              continue;
            }

            Connection connection = new Connection();

            String src[] = fields[1].split(":", 2);
            String dst[] = fields[2].split(":", 2);

            connection.src = getAddress(src[0]);
            connection.spt = String.valueOf(getInt16(src[1]));
            connection.dst = getAddress(dst[0]);
            connection.dpt = String.valueOf(getInt16(dst[1]));
            connection.uid = fields[7];

            connections.add(connection);
            Log.d("TCP connection", connection.dst);
          }

          in.close();

          in = new BufferedReader(new FileReader("/proc/" + this.pid + "/net/udp"));

          while((line = in.readLine()) != null) {
            line = line.trim();
             Log.d("UDP","Netstat: " + line);
            String[] fields = line.split("\\s+", 10);
            int fieldn = 0;

            for(String field : fields) {
               //Log.d("TAG","Field " + (fieldn++) + ": [" + field + "]");
            }

            if(fields[0].equals("sl")) {
              continue;
            }

            Connection connection = new Connection();

            String src[] = fields[1].split(":", 2);
            String dst[] = fields[2].split(":", 2);

            connection.src = getAddress(src[0]);
            connection.spt = String.valueOf(getInt16(src[1]));
            connection.dst = getAddress(dst[0]);
            connection.dpt = String.valueOf(getInt16(dst[1]));
            connection.uid = fields[7];

            connections.add(connection);
            Log.d("UDP connection", connection.dst);
          }

          in.close();

          in = new BufferedReader(new FileReader("/proc/" + this.pid + "/net/tcp6"));

          while((line = in.readLine()) != null) {
            line = line.trim();
             Log.d("TCP6","Netstat: " + line);
            String[] fields = line.split("\\s+", 10);
            int fieldn = 0;

            for(String field : fields) {
               //Log.d("TAG","Field " + (fieldn++) + ": [" + field + "]");
            }

            if(fields[0].equals("sl")) {
              continue;
            }

            Connection connection = new Connection();

            String src[] = fields[1].split(":", 2);
            String dst[] = fields[2].split(":", 2);

            connection.src = getAddress6(src[0]);
            connection.spt = String.valueOf(getInt16(src[1]));
            connection.dst = getAddress6(dst[0]);
            connection.dpt = String.valueOf(getInt16(dst[1]));
            connection.uid = fields[7];

            connections.add(connection);
            Log.d("TCP6 connection", connection.dst);
          }
          

          in.close();

          in = new BufferedReader(new FileReader("/proc/" + this.pid + "/net/udp6"));

          while((line = in.readLine()) != null) {
            line = line.trim();
             Log.d("UDP6","Netstat: " + line);
            String[] fields = line.split("\\s+", 10);
            int fieldn = 0;

            for(String field : fields) {
               //Log.d("TAG","Field " + (fieldn++) + ": [" + field + "]");
            }

            if(fields[0].equals("sl")) {
              continue;
            }

            Connection connection = new Connection();

            String src[] = fields[1].split(":", 2);
            String dst[] = fields[2].split(":", 2);

            connection.src = getAddress6(src[0]);
            connection.spt = String.valueOf(getInt16(src[1]));
            connection.dst = getAddress6(dst[0]);
            connection.dpt = String.valueOf(getInt16(dst[1]));
            connection.uid = fields[7];

            connections.add(connection);
            Log.d("UDP6 connection", connection.dst);
          }

          in.close();
        } catch(Exception e) {
          Log.w("NetworkLog", e.toString(), e);
        }
        
        //write log
        File file = getDir("networklogs");
        try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.append("asdfasdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
	}
	
	public File getDir(String fileName) {
	    // Get the directory for the user's public pictures directory. 
	    File file = new File(Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS), fileName+"/log.txt");
	    if (!file.mkdirs()) {
	        Log.e("TAG", "Directory not created");
	    }
	    return file;
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public class Connection {
	    String src;
	    String spt;
	    String dst;
	    String dpt;
	    String uid;
	  }

	  final String states[] = { "ESTBLSH",   "SYNSENT",   "SYNRECV",   "FWAIT1",   "FWAIT2",   "TMEWAIT",
	    "CLOSED",    "CLSWAIT",   "LASTACK",   "LISTEN",   "CLOSING",  "UNKNOWN"
	  };

	  private final String getAddress(final String hexa) {
	    try {
	      final long v = Long.parseLong(hexa, 16);
	      final long adr = (v >>> 24) | (v << 24) |
	        ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
	      return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "." + ((adr >> 8) & 0xff) + "." + (adr & 0xff);
	    } catch(Exception e) {
	      Log.w("NetworkLog", e.toString(), e);
	      return "-1.-1.-1.-1";
	    }
	  }

	  private final String getAddress6(final String hexa) {
	    try {
	      final String ip4[] = hexa.split("0000000000000000FFFF0000");

	      if(ip4.length == 2) {
	        final long v = Long.parseLong(ip4[1], 16);
	        final long adr = (v >>> 24) | (v << 24) |
	          ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
	        return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "." + ((adr >> 8) & 0xff) + "." + (adr & 0xff);
	      } else {
	        return "-2.-2.-2.-2";
	      }
	    } catch(Exception e) {
	      Log.w("NetworkLog", e.toString(), e);
	      return "-1.-1.-1.-1";
	    }
	  }

	  private final int getInt16(final String hexa) {
	    try {
	      return Integer.parseInt(hexa, 16);
	    } catch(Exception e) {
	      Log.w("NetworkLog", e.toString(), e);
	      return -1;
	    }
	  }
}
