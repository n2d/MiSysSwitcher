package www.cnsys.org.MiuiSwitcher;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.media.AudioManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	View proccessView;
	static private CharSequence g_msg;
	static public int currentSystemNo=-1;
	private int runSteps = 0;
	AlertDialog.Builder builder;
	ProgressBar progressBar1;
	static int  reboottype=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    currentSystemNo = JNIMain.getSystemNo();
		builder = new AlertDialog.Builder(this);

		builder.setMessage("确定重启？")
 
		.setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						currentSystemNo = JNIMain.getSystemNo();
						if(currentSystemNo<0)
						{
							Toast.makeText(getBaseContext(), "当前系统编号获取失败",
									Toast.LENGTH_LONG).show();
							return;
						}
						//Toast.makeText(getBaseContext(), "当前运行的是系统"+currentSystemNo,
							//Toast.LENGTH_LONG).show();
						int sys=(currentSystemNo==1?0:1);
						int ret = JNIMain.SwitchSystem(sys);
						if (ret < 0) {
							Toast.makeText(getBaseContext(), "系统切换失败",
									Toast.LENGTH_LONG).show();
						}
						//Intent intent = new Intent(Intent.ACTION_SHUTDOWN);  
						//intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);  
						//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						//startActivity(intent);  
						if(reboottype==0)
						{
							Log.v("","soft reboot");

							 try {
									PowerManager pm = (PowerManager)
									 getSystemService(Context.POWER_SERVICE);
									 pm.reboot("");
							 } catch (Exception e) {
							    Toast.makeText(getApplicationContext(), "自动重启失败,你现在使用系统重启也可以切换系统", Toast.LENGTH_LONG).show();
							 }
						}
						else
						{
							Log.v("","force reboot");
						   runRootCommand("reboot"); 
						}
					}

				})

				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						// dialog.cancel();
					}

				})

				.create();
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void setButtons(boolean flag) {

		findViewById(R.id.button1).setEnabled(flag);
		findViewById(R.id.button2).setEnabled(flag);
		findViewById(R.id.button3).setEnabled(flag);
		findViewById(R.id.button4).setEnabled(flag);
	}

	public void myclick(View v) {
		String sdpath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String firmwarepath;
		File file;
		ArrayList<String> command = new ArrayList<String>(2);
		String cmd;
		switch (v.getId()) {
		case R.id.button1:
			firmwarepath = sdpath + "/mipatcher/baseband/2.3/NON-HLOS.bin";
			file = new File(firmwarepath);
			if (!file.exists()) {
				Toast.makeText(this, firmwarepath + "不存在", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			cmd = "cat " + firmwarepath
					+ " >/dev/block/platform/msm_sdcc.1/by-name/modem";
			proccessView = v;
			setButtons(false);
			g_msg = "MIUI2.3基带更新成功";
			runSteps = 0;
			command.clear();
			command.add(cmd);
			// Toast.makeText(this, "基带程序更新成功", Toast.LENGTH_SHORT).show();
			firmwarepath = sdpath + "/mipatcher/baseband/2.3/recovery.img";
			file = new File(firmwarepath);
			if (!file.exists()) {
				Toast.makeText(this, firmwarepath + "不存在", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			cmd = "cat " + firmwarepath
					+ " >/dev/block/platform/msm_sdcc.1/by-name/recovery";
			command.add(cmd);
			RootCommand(this, command);

			// Toast.makeText(this, "recovery更新成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button2:
			firmwarepath = sdpath + "/mipatcher/baseband/4.0/NON-HLOS.bin";
			file = new File(firmwarepath);
			if (!file.exists()) {
				Toast.makeText(this, firmwarepath + "不存在", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			proccessView = v;
			setButtons(false);
			cmd = "cat " + firmwarepath
					+ " >/dev/block/platform/msm_sdcc.1/by-name/modem";
			command.clear();
			command.add(cmd);
			g_msg = "MIUI4.0基带更新成功";

			// RootCommand(command);
			// Toast.makeText(this, "基带程序更新成功", Toast.LENGTH_SHORT).show();
			firmwarepath = sdpath + "/mipatcher/baseband/4.0/recovery.img";
			file = new File(firmwarepath);
			if (!file.exists()) {
				Toast.makeText(this, firmwarepath + "不存在", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			cmd = "cat " + firmwarepath
					+ " >/dev/block/platform/msm_sdcc.1/by-name/recovery";
			command.add(cmd);
			RootCommand(this, command);

			// Toast.makeText(this, "recovery更新成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button3:
			reboottype=1;
			{
			Message msg = new Message();
			msg.what = 1;
			msg.obj = "";
			commandHandler.sendMessage(msg);
			}
			break;
		case R.id.button5:
			reboottype=0;
			// PowerManager pm=(PowerManager)
			// getSystemService(Context.POWER_SERVICE);
			// pm.reboot("switchsystem");
			// Intent intent = new
			// Intent("android.intent.action.ACTION_SHUTDOWN");
			// intent.putExtra("nowait", 1);
			// intent.putExtra("interval", 1);
			// intent.putExtra("window", 0);
			// sendBroadcast(intent);
			// RootCommand("echo 'switchsystem' > /sys/bootinfo/powerup_reason");
			{
			Message msg = new Message();
			msg.what = 1;
			msg.obj = "";
			commandHandler.sendMessage(msg);
			}
			break;
		case R.id.button4:

			// Intent intent = new Intent(Intent.ACTION_REBOOT);
			// intent.putExtra("nowait", 1);
			// intent.putExtra("interval", 1);
			// intent.putExtra("window", 0);
			// sendBroadcast(intent);
			// RootCommand("echo 'switchsystem' > /sys/bootinfo/powerup_reason");
			// String PM_CLASS_NAME = "android.os.Power";
			// String PM_CLASS_NAME = "com.android.internal.app.ShutdownThread";

			// try {
			// Class pmClass;
			// pmClass = Class.forName(PM_CLASS_NAME);
			// Log.i("", pmClass.toString());
			// Method meths[] = pmClass.getMethods();
			// Method shutdown = null;
			// // Method makenewwindow = policyClass.getMethod("makeNewWindow");
			// for (int i = 0; i < meths.length; i++) {
			// if (meths[i].getName().endsWith("shutdown"))
			// shutdown = meths[i];
			// Log.v("",meths[i].getName());
			// }
			// Log.i("", shutdown.toString());
			// shutdown.invoke(null);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			Intent instance = new Intent(this, Updater.class);
			instance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			instance.setAction("android.intent.action.CALLSTART");
			startActivity(instance);

			break;
		}
	}


	private Handler commandHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				setButtons(true);
				builder.show();
				progressBar1.setVisibility(View.GONE);
			}
		}
	};

	public class ReadDataRunnable implements Runnable {
		List<String> command;
		int mystep = 0;
		MainActivity mactivity;

		public ReadDataRunnable(Context context, List<String> command) {
			// TODO Auto-generated constructor stub
			this.command = command;
			mactivity = (MainActivity) context;
		}

		@Override
		public void run() {
			for (String cmd : command) {
				Log.v("", cmd);
				runRootCommand(cmd);
			}
			Message msg = new Message();
			msg.what = 1;
			msg.obj = "";
			commandHandler.sendMessage(msg);

		}

	}

	public boolean runRootCommand(String command) {
		// TODO Auto-generated method stub
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	public boolean RootCommand(Context context, List<String> command) {
		//
		progressBar1.setVisibility(View.VISIBLE);
		new Thread(new ReadDataRunnable(context, command)).start();

		return true;
	}
}
