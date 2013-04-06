package www.cnsys.org.MiuiSwitcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class JNIMain {
	public static int getSystemNo() {
		Process process = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		String outInfo = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			is = new DataInputStream(process.getInputStream());
			os.writeBytes("cat /proc/cmdline\n");
			os.writeBytes("exit\n");
			os.flush();
			byte[] buffer = new byte[256];
			is.read(buffer);
			outInfo = new String(buffer);
			process.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sign = "syspart=";
		int sIndex = outInfo.indexOf(sign, 0);
		int eIndex = outInfo.indexOf(" ", sIndex);
		String subStr = outInfo.substring(sIndex + sign.length(), eIndex);
		Log.v("", "" + subStr);
		if (subStr.equalsIgnoreCase("system1"))
			return 1;
		else if (subStr.equalsIgnoreCase("system"))
			return 0;
		return -1;
	}

	
	public static int getSystem() {
		Process process = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		String outInfo = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			is = new DataInputStream(process.getInputStream());
			os.writeBytes("cat /proc/cmdline\n");
			os.writeBytes("exit\n");
			os.flush();
			byte[] buffer = new byte[256];
			is.read(buffer);
			outInfo = new String(buffer);
			process.waitFor();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sign = "syspart=";
		int sIndex = outInfo.indexOf(sign, 0);
		int eIndex = outInfo.indexOf(" ", sIndex);
		String subStr = outInfo.substring(sIndex + sign.length(), eIndex);
		Log.v("", "" + subStr);
		if (subStr.equalsIgnoreCase("system1"))
			return 1;
		else if (subStr.equalsIgnoreCase("system"))
			return 0;
		return -1;
	}
	
	public static native int SwitchSystem(int sys);

	static {
		System.loadLibrary("MiSwitcherJNI");
	}

}
