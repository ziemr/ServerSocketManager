package com.android.serversocket.socket;
import android.util.Log;

/**
 * ����debug���
 * 
 * @author thinkpad
 * 
 */
public class ShowLogManager {
	public static boolean outputDebug(String tag, String data) {
		if (Info.SHOW_LOG_FLAG) {
			Log.d(tag, data);
			return true;
		}
		return false;//����δ����
	}
}
