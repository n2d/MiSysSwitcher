package www.cnsys.org.MiuiSwitcher;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Updater extends Activity {
	private Builder builder;
	String bootpart="boot";
	ProgressBar progressBar1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updater);
		
		builder = new AlertDialog.Builder(this);

		builder.setMessage("操作成功！重启进入recovery WIPE 所有数据?")

		.setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						runRootCommand("reboot recovery");
					}

				})

				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						// dialog.cancel();
					}

				})

				.create();
		
		
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		OnCheckedChangeListener listener = new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radioButton1:
					bootpart="boot";
					break;
				case R.id.radioButton2:
					bootpart="boot1";
					break;

				}
			}
		};
		rg.setOnCheckedChangeListener(listener);
		
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
	}
	
	public void myclick(View v) {
		String sdpath = Environment.getExternalStorageDirectory()
		.getAbsolutePath();
		String firmwarepath="";
		ArrayList<String> command = new ArrayList<String>(1);
		switch (v.getId()) {
		case R.id.button1:
			firmwarepath=sdpath+"/mipatcher/boots/boot-ys.img";
			break;
		case R.id.button2:
			firmwarepath=sdpath+"/mipatcher/boots/boot-dx.img";
			break;
		case R.id.button3:
			firmwarepath=sdpath+"/mipatcher/boots/boot-miui2.3.img";
			break;
		}
		if(firmwarepath.length()<0)
			return;
		
		File file = new File(firmwarepath);
		if (!file.exists()) {
			Toast.makeText(this, firmwarepath + "不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		String cmd = "cat " + firmwarepath
		+ " >/dev/block/platform/msm_sdcc.1/by-name/"+bootpart;
		command.add(cmd);
		RootCommand(this,command);
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

	private void setButtons(boolean flag) {

		findViewById(R.id.button1).setEnabled(flag);
		findViewById(R.id.button2).setEnabled(flag);
		findViewById(R.id.button3).setEnabled(flag);
	}
	
	public class ReadDataRunnable implements Runnable {
		List<String> command;
		int mystep = 0;
		//MainActivity mactivity;

		public ReadDataRunnable(Context context, List<String> command) {
			// TODO Auto-generated constructor stub
			this.command = command;
			//mactivity = (MainActivity) context;
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
