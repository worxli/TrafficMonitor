package edu.washington.cs.trafficmonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class LogService extends Thread {
	
	public boolean alive = true;
	private int uuid;
	private int pid;
	private String name;
	private int time;
	private Context context;

	
	public LogService(int uuid, int pid, int time, String name, Context context){
		this.uuid = uuid;
		this.pid = pid;
		this.name = name;
		this.time = time;
		this.context = context;
	}

	@Override
	public void run() {
		
		while(alive){
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
	            //Log.d("TCP connection", connection.dst);
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
	           // Log.d("UDP connection", connection.dst);
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
	           // Log.d("TCP6 connection", connection.dst);
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
	            //Log.d("UDP6 connection", connection.dst);
	          }

	          in.close();
	        } catch(Exception e) {
	          Log.w("NetworkLog", e.toString(), e);
	        }
	        
	        for (Connection connection : connections) {
	        	String conn =  "{'uuid':"+connection.uid+", 'dpt':"+connection.dpt+", 'spt':"+connection.spt+", 'src':"+connection.src+"}";
				Log.d("conn", conn);
			}
	        
	        //wifi strength
	        WifiManager wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
	        int wifiSpeed = wifiManager.getConnectionInfo().getRssi();
	        
	        //cell strength
	        /*
	        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
	        CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
	        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
	        */
	        int cellSpeed = 0;//cellSignalStrengthGsm.getDbm();
	        
	        long apptpackets = TrafficStats.getUidTxPackets(this.uuid);
	        long apptbytes = TrafficStats.getUidTxBytes(this.uuid);
	        long apprpackets = TrafficStats.getUidRxPackets(this.uuid);
	        long apprbytes = TrafficStats.getUidRxBytes(this.uuid);
	        long tcpconn = 0;
	        long udpconn = 0;
	        
	        long mtpackets = TrafficStats.getMobileTxPackets();
	        long mtbytes = TrafficStats.getMobileTxBytes();
	        long mrpackets = TrafficStats.getMobileRxPackets();
	        long mrbytes = TrafficStats.getMobileRxBytes();
	        long tpackets = TrafficStats.getTotalTxPackets();
	        long tbytes = TrafficStats.getTotalTxBytes();
	        long rpackets = TrafficStats.getTotalRxPackets();
	        long rbytes = TrafficStats.getTotalRxBytes();
	        
	        //collect app data
	        JSONObject app = new JSONObject();
	        try {
				app.put("uuid", this.uuid);
				app.put("pid", this.pid);
				app.put("name", this.name);
				//app.put("tpackets", TrafficStats.getUidTxPackets(this.uuid));
				app.put("tbytes", TrafficStats.getUidTxBytes(this.uuid));
				//app.put("rpackets", TrafficStats.getUidRxPackets(this.uuid));
				app.put("rbytes", TrafficStats.getUidRxBytes(this.uuid));
				//app.put("tcp", );
				//app.put("udp", );
			} catch (JSONException e) { e.printStackTrace(); }
	       
	        String logstring = System.currentTimeMillis()+","+
	        					wifiSpeed+","+
	        					cellSpeed+","+
	        					apptpackets+","+
	        					apptbytes+","+
	        					apprpackets+","+
	        					apprbytes+","+
	        					tcpconn+","+
	        					udpconn+","+
	        					mtpackets+","+
	        					mtbytes+","+
	        					mrpackets+","+
	        					mrbytes+","+
	        					tpackets+","+
	        					tbytes+","+
	        					rpackets+","+
	        					rbytes;
	        					
	        					
			//collect phone data
	        JSONObject phone = new JSONObject(); 
	        try {
				phone.put("wifi", wifiSpeed);
				phone.put("mobile", cellSpeed);
				phone.put("speed", "?");
				phone.put("mtpackets", TrafficStats.getMobileTxPackets());
				phone.put("mtbytes", TrafficStats.getMobileTxBytes());
				phone.put("mrpackets", TrafficStats.getMobileRxPackets());
				phone.put("mrbytes", TrafficStats.getMobileRxBytes());
				phone.put("tpackets", TrafficStats.getTotalTxPackets());
				phone.put("tbytes", TrafficStats.getTotalTxBytes());
				phone.put("rpackets", TrafficStats.getTotalRxPackets());
				phone.put("rbytes", TrafficStats.getTotalRxBytes());
			} catch (JSONException e) { e.printStackTrace(); }
	        
	        
			JSONObject log = null;
	        try {
				log = new JSONObject("log: {'time': "+System.currentTimeMillis()+", 'phone': "+phone.toString()+", 'app': "+app.toString()+"}}");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        //Log.d("json:", log.toString());
	        Log.d("csv", logstring);
	        
	        //write log
	        File root = android.os.Environment.getExternalStorageDirectory(); 
	        File dir = new File (root.getAbsolutePath() + "/networklogs");
	        dir.mkdirs();
	        File file = new File(dir, "log.txt");
	        
	        try {
	            FileOutputStream f = new FileOutputStream(file, true);
	            PrintWriter pw = new PrintWriter(f);
	            
	            //pw.println(log+",");
	            pw.println(logstring);
	            pw.flush();
	            pw.close();
	            f.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            Log.i("TAG", "******* File not found. Did you" +
	                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }   
		}
		
		try {
			Thread.sleep(this.time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
